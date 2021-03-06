package com.datastax.astra.jetbrains.utils.editor.ui

import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.devops_v2.models.StatusEnum
import com.datastax.astra.jetbrains.credentials.ProfileManager
import com.datastax.astra.jetbrains.credentials.ProfileState
import com.datastax.astra.jetbrains.credentials.ProfileStateChangeNotifier
import com.datastax.astra.jetbrains.explorer.cached
import com.datastax.astra.jetbrains.explorer.fetchCollections
import com.datastax.astra.jetbrains.explorer.fetchDatabases
import com.datastax.astra.jetbrains.explorer.fetchKeyspaces
import com.datastax.astra.jetbrains.services.database.editor.CollectionVirtualFile
import com.datastax.astra.jetbrains.services.database.editor.DocumentVirtualFile
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.getCoroutineBgContext
import com.datastax.astra.stargate_document_v2.models.DocCollection
import com.datastax.astra.stargate_rest_v2.models.Keyspace
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.EditorHeaderComponent
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotifications
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Provides the editor header for JSON files
 */
class AstraFileEditorUIService(private val project: Project) :
    Disposable, FileEditorManagerListener, ProfileStateChangeNotifier, ExplorerTreeChangeEventListener {
    private var databaseList = mutableListOf<SimpleDatabase>()
    private var rebuildingJob: Job? = null
    private val coroutineScope = ApplicationThreadPoolScope("AstraFileEditorUIService")
    private val bg = getCoroutineBgContext()

    // ---- editor tabs listener ----
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        insertEditorHeaderComponentIfApplicable(source, file)
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {}

    // -- editor header component --
    private fun insertEditorHeaderComponentIfApplicable(source: FileEditorManager, file: VirtualFile) {
        // use extension and file type to include scratch files and simply identifying relevant files
        if ((file.extension?.contains("json", true) == true) && file !is DocumentVirtualFile) {
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
        val jsonEditorComboBoxes = if (file is CollectionVirtualFile) {
            val collectionFile = file
            ToolbarComboBoxes(
                project,
                databaseList,
                collectionFile.database.id,
                collectionFile.keyspaceName,
                collectionFile.collectionName,
            )
        } else {
            ToolbarComboBoxes(project, databaseList)
        }
        val collectionActions = DefaultActionGroup()
        collectionActions.add(InsertDocumentsAction(editor, jsonEditorComboBoxes))

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
        databaseList.first { it.database == database }.keyspaces.put(
            keyspace.name,
            SimpleKeyspace(keyspace, cList.orEmpty())
        )
    }

    // TODO: do this with the cache maps instead, then there's less requests to the server
    suspend fun buildDatabaseMap() {
        databaseList.clear()
        val databases = try {
            cached(project, "", loader = fetchDatabases(project))
        } catch (e: Exception) {
            listOf()
        }
        databases.filter {
            it.status == StatusEnum.ACTIVE
        }.forEach { database ->
            val keyspaces = try {
                cached(project, database, loader = fetchKeyspaces(project))
            } catch (e: Exception) {
                listOf()
            }
            keyspaces?.forEach { keyspace ->
                val collections = try {
                    cached(project, Pair(database, keyspace), loader = fetchCollections(project))
                } catch (e:Exception) {
                    listOf()
                }
                indexCollections(collections, keyspace, database)
            }
        }
    }

    fun rebuildAndNotify() {
        if (rebuildingJob?.isActive != true) {
            rebuildingJob = coroutineScope.launch(bg) {
                buildDatabaseMap()
                project.messageBus.syncPublisher(ProfileChangeEventListener.TOPIC)
                    .reloadFileEditorUIResources(databaseList)
            }
        }
    }

    //Implements ExplorerTreeChangeEventListener
    override fun rebuildEndpointList() {
        project.messageBus.syncPublisher(ProfileChangeEventListener.TOPIC)
            .clearFileEditorUIResources()
        rebuildAndNotify()
    }

    //Implements ProfileStateChangeNotifier
    override fun profileStateChanged(newState: ProfileState) {
        when {
            !newState.isTerminal -> project.messageBus.syncPublisher(ProfileChangeEventListener.TOPIC)
                .clearFileEditorUIResources()
            newState.displayMessage.contains("placehold") -> rebuildAndNotify()
        }
    }

    // -- instance management --
    override fun dispose() {
        if (rebuildingJob?.isActive == true) {
            rebuildingJob?.cancel()
        }
    }

    init {

        // Get the message bus service
        val messageBusConnection = project.messageBus.connect(this)
        // Subscribe to tree change notifications
        messageBusConnection.subscribe(ExplorerTreeChangeEventListener.TOPIC, this)
        // Subscribe to profile change notifications
        messageBusConnection.subscribe(ProfileManager.CONNECTION_SETTINGS_STATE_CHANGED, this)

        // and notify to configure the schema
        EditorNotifications.getInstance(project).updateAllNotifications()

        // Build this in the background if the token is set
        rebuildAndNotify()
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
        return keyspace.name
    }
}
