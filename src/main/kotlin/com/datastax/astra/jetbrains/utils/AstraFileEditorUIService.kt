package com.datastax.astra.jetbrains.utils

import com.datastax.astra.jetbrains.actions.UpdateCollectionAction
import com.google.gson.*
import com.intellij.codeInsight.hint.HintManager
import com.intellij.codeInsight.hint.HintManagerImpl
import com.intellij.codeInsight.hint.HintUtil
import com.intellij.icons.AllIcons
import com.intellij.ide.util.PropertiesComponent
import com.intellij.json.JsonFileType
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.EditorHeaderComponent
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorProvider
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task.Backgroundable
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vcs.CodeSmellDetector
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.impl.source.codeStyle.CodeStyleManagerImpl
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.*
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.ui.content.impl.ContentImpl
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.ui.UIUtil
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.time.StopWatch
import org.apache.http.Header
import org.apache.http.HttpRequest
import org.apache.http.client.methods.HttpPost
import org.apache.http.util.EntityUtils
import java.awt.BorderLayout
import java.awt.Cursor
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.IOException
import java.lang.Double
import java.lang.reflect.Type
import java.security.GeneralSecurityException
import javax.swing.*
import javax.swing.border.EmptyBorder
import kotlin.Any
import kotlin.Boolean
import kotlin.IllegalArgumentException
import kotlin.IllegalStateException
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.let
import kotlin.require

/**
 * Provides the editor header for JSON files
 */
class AstraFileEditorUIService(private val myProject: Project) :
    Disposable, FileEditorManagerListener {

    private val myLock = Any()
    private var fileEditor: FileEditor? = null
    //private var queryResultLabel: JBLabel? = null
    //private var querySuccessLabel: JBLabel? = null

    // ---- editor tabs listener ----
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        insertEditorHeaderComponentIfApplicable(source, file)
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {}

    // -- editor header component --
    private fun insertEditorHeaderComponentIfApplicable(source: FileEditorManager, file: VirtualFile) {
        //use extension and not file type to include scratch files and simply identifying relevant files
        if (file.extension?.contains("json",true) == true) {
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
        val headerComponent = AstraEditorHeaderComponent()

        val helloButton = JButton("Hello world")
        headerComponent.add(helloButton)
        return headerComponent
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
        val messageBusConnection = myProject.messageBus.connect(this)

        // listen for editor file tab changes to update the list of current errors
        messageBusConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this)

        // add editor headers to already open files since we've only just added the listener for fileOpened()
        val fileEditorManager = FileEditorManager.getInstance(myProject)
        for (virtualFile in fileEditorManager.openFiles) {
            UIUtil.invokeLaterIfNeeded {
                insertEditorHeaderComponentIfApplicable(
                    fileEditorManager,
                    virtualFile
                )
            }
        }

        // and notify to configure the schema
        EditorNotifications.getInstance(myProject).updateAllNotifications()
    }
}