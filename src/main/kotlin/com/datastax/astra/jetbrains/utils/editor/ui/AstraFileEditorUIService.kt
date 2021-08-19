package com.datastax.astra.jetbrains.utils.editor.ui

import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.credentials.ProfileManager
import com.datastax.astra.jetbrains.credentials.ProfileState
import com.datastax.astra.jetbrains.credentials.ProfileStateChangeNotifier
import com.datastax.astra.jetbrains.services.database.CollectionPagedVirtualFile
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.stargate_document_v2.models.DocCollection
import com.datastax.astra.stargate_rest_v2.models.Keyspace
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.EditorHeaderComponent
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.project.Project

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotifications
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import kotlinx.coroutines.*
import java.awt.BorderLayout
import java.awt.GridLayout
import java.util.UUID.randomUUID
import javax.swing.*

/**
 * Provides the editor header for JSON files
 */
class AstraFileEditorUIService(private val project: Project) :
    Disposable, FileEditorManagerListener, ProfileStateChangeNotifier, CoroutineScope by ApplicationThreadPoolScope("FileEditorUIService") {
    private var databaseList = mutableListOf<SimpleDatabase>()
    private val myLock = Any()
    private var fileEditor: FileEditor? = null
    // private var queryResultLabel: JBLabel? = null
    // private var querySuccessLabel: JBLabel? = null

    // ---- editor tabs listener ----
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        insertEditorHeaderComponentIfApplicable(source, file)
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {}

    // -- editor header component --
    private fun insertEditorHeaderComponentIfApplicable(source: FileEditorManager, file: VirtualFile) {
        // use extension and file type to include scratch files and simply identifying relevant files
        if ((file.extension?.contains("json", true) == true || file is CollectionPagedVirtualFile)) {
            UIUtil.invokeLaterIfNeeded {
                // ensure components are created on the swing thread
                val fileEditor = source.getSelectedEditor(file)
                if (fileEditor is TextEditor) {
                    val editor =
                        fileEditor.editor
                    if (editor.headerComponent is AstraEditorHeaderComponent) {
                        return@invokeLaterIfNeeded
                    }
                    val headerComponent = createHeaderComponent(fileEditor, editor, file)
                    editor.headerComponent = headerComponent
                    if (editor is EditorEx) {
                        editor.permanentHeaderComponent = headerComponent
                    }
                }
            }
        }
    }

    private class AstraEditorHeaderComponent : EditorHeaderComponent()

    private fun createHeaderComponent(fileEditor: FileEditor, editor: Editor, file: VirtualFile): JComponent {
        // If this is a CollectionVirtualFile set the combo boxes to match the file's resources
        val jsonEditorComboBoxes = if (file is CollectionPagedVirtualFile) {
            val collectionFile = file
            EndpointComboBoxes(
                project,
                databaseList,
                collectionFile.endpointInfo
            )
        } else {
            EndpointComboBoxes(project, databaseList)
        }
        val collectionActions = DefaultActionGroup()
        collectionActions.add(InsertDocumentsAction(editor, jsonEditorComboBoxes))
        collectionActions.add(PreviousPageAction(file))
        collectionActions.add(NextPageAction(file))

        // Add upsert documents button

        val headerComponent = AstraEditorHeaderComponent()
        val upsertToolbar = createToolbar(collectionActions, headerComponent)
        val innerPanel = JPanel(GridLayout()).add(upsertToolbar, 0)
        headerComponent.add(jsonEditorComboBoxes.getPanel(), BorderLayout.WEST)
        headerComponent.add(innerPanel, BorderLayout.CENTER)

        return headerComponent
    }

    private fun createToolbar(actionGroup: ActionGroup, parent: JComponent): JComponent {
        val toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.EDITOR_TOOLBAR, actionGroup, true)
        toolbar.setReservePlaceAutoPopupIcon(false) // don't want space after the last button
        toolbar.setTargetComponent(parent)
        val component = toolbar.component
        component.border = BorderFactory.createMatteBorder(0, 0, 0, 2, JBUI.CurrentTheme.ToolWindow.headerBackground())
        return component
    }

    // Build or rebuild database list
    fun indexCollections(cList: List<DocCollection>?, keyspace: Keyspace, database: Database) {
        if (databaseList.filter { it.database == database }.isEmpty()) {
            databaseList.add(SimpleDatabase(database, mutableMapOf<String, SimpleKeyspace>()))
        }
        // TODO: Handle concurrent access exception
        databaseList.first { it.database == database }.keyspaces?.put(
            keyspace.name,
            SimpleKeyspace(keyspace, cList.orEmpty())
        )
    }

    // TODO: do this with the cache maps instead, then there's less requests to the server
    suspend fun buildDatabaseMap() = coroutineScope {
        databaseList.clear()
        val response = AstraClient.dbOperationsApi().listDatabases()
        if (response.isSuccessful && !response.body().isNullOrEmpty()) {
            response.body()?.forEach { database ->
                    launch {
                        val keyspaceResponse = AstraClient.schemasApiForDatabase(database).getKeyspaces(AstraClient.accessToken)
                        if (keyspaceResponse.isSuccessful && !keyspaceResponse.body()?.data.isNullOrEmpty()) {
                            keyspaceResponse.body()?.data?.forEach { keyspace ->
                                    val collectionResponse = AstraClient.documentApiForDatabase(database)
                                        .listCollections(randomUUID(), AstraClient.accessToken, keyspace.name)
                                    if (collectionResponse.isSuccessful && collectionResponse.body()?.data != null) {
                                        indexCollections(collectionResponse.body()?.data, keyspace, database)
                                        }
                            }
                        }
                    }
            }
        }
    }

    fun rebuildAndNotify() {
        launch {
            val defer = async { buildDatabaseMap() }
            defer.await()
            project.getMessageBus().syncPublisher(ProfileChangeEventListener.TOPIC).reloadFileEditorUIResources(databaseList)
        }
    }

    override fun profileStateChanged(newState: ProfileState) {
        when {
            !newState.isTerminal -> project.getMessageBus().syncPublisher(ProfileChangeEventListener.TOPIC)
                .clearFileEditorUIResources()
            newState.displayMessage.contains("placehold") -> rebuildAndNotify()
        }
    }
    // -- instance management --
    override fun dispose() {}

    companion object {

        fun getService(project: Project): AstraFileEditorUIService {
            return ServiceManager.getService(project, AstraFileEditorUIService::class.java)
        }
    }

    init {
        //Get the message bus service

        val messageBusConnection = project.messageBus.connect(this)
        // Subscribe to profile change notifications
        messageBusConnection.subscribe(ProfileManager.CONNECTION_SETTINGS_STATE_CHANGED, this)
        // listen for editor file tab changes to update the list of current errors
        messageBusConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this)
        // add editor headers to already open files since we've only just added the listener for fileOpened()
        val fileEditorManager = FileEditorManager.getInstance(project)
        for (virtualFile in fileEditorManager.openFiles) {
            UIUtil.invokeLaterIfNeeded {
                insertEditorHeaderComponentIfApplicable(
                    fileEditorManager,
                    virtualFile
                )
            }
        }

        // and notify to configure the schema
        EditorNotifications.getInstance(project).updateAllNotifications()

        // Build this in the background
        rebuildAndNotify()
    }

    fun endpoints(): MutableList<SimpleDatabase> {
        return databaseList
    }
}

// TODO: Refactor this as a hash map of collections with a keyspace and database
// Then just import that and filter by what is needed
class SimpleDatabase(
    var database: Database,
    var keyspaces: MutableMap<String, SimpleKeyspace>
) {
    override fun toString(): String {
        return "${database.info.name}"
    }
}

class SimpleKeyspace(
    var keyspace: Keyspace,
    var collections: List<DocCollection>,
) {
    override fun toString(): String {
        return "${keyspace.name}"
    }
}
