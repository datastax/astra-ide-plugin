package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.explorer.TableEndpoint
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.project.PossiblyDumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import java.beans.PropertyChangeListener
import javax.swing.JComponent

class TableEditorProvider : FileEditorProvider, PossiblyDumbAware {

    override fun isDumbAware() = true

    override fun accept(project: Project, file: VirtualFile) = file is TableVirtualFile

    override fun createEditor(project: Project, file: VirtualFile): FileEditor =
        TableEditor(project, file as TableVirtualFile)

    override fun getEditorTypeId() = EDITOR_TYPE_ID

    override fun getPolicy() = FileEditorPolicy.HIDE_DEFAULT_EDITOR

    companion object {
        const val EDITOR_TYPE_ID = "Table Viewer"
    }
}

class TableEditor(project: Project, tableVirtualFile: TableVirtualFile) : UserDataHolderBase(), FileEditor {
    protected val edtContext = getCoroutineUiContext(disposable = this)
    private val tableWindow = TableManager(this,project, tableVirtualFile.endpoint).tableUI
    var myHeaderPanel = tableWindow.toolbar
    private val PERMANENT_HEADER = Key.create<JComponent>("PERMANENT_HEADER")

    override fun dispose() {}

    override fun getComponent() = tableWindow.component

    override fun getPreferredFocusedComponent() = tableWindow.tableView

    override fun getName() = "Table Panel"

    override fun setState(state: FileEditorState) {}

    override fun isModified(): Boolean = false

    override fun isValid(): Boolean = true

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {}

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {}

    override fun getCurrentLocation(): FileEditorLocation? = null

    override fun getFile(): VirtualFile {
        return FileEditor.FILE_KEY[this]
    }

    suspend fun setHeaderComponent(header: JComponent?) {
        withContext(edtContext) {
            var header = header
            myHeaderPanel.removeAll()
            header = header ?: getPermanentHeaderComponent()
            if (header != null) {
                myHeaderPanel.add(header)
            }
            myHeaderPanel.isEnabled = true
            myHeaderPanel.revalidate()
            myHeaderPanel.repaint()
            // myPanel.add(myHeader,BorderLayout.NORTH)
            //            myPanel.revalidate()
            //            myPanel.repaint()
        }
        // TODO: Ask Garrett about touchbar
    }

    fun getPermanentHeaderComponent(): JComponent? {
        return getUserData(PERMANENT_HEADER)
    }

    fun setPermanentHeaderComponent(component: JComponent?) {
        putUserData(PERMANENT_HEADER, component)
    }

    fun hasHeaderComponent(): Boolean = myHeaderPanel.isEnabled


}

class TableVirtualFile(val endpoint: TableEndpoint) :
    LightVirtualFile(endpoint.table?.name.orEmpty()),
    CoroutineScope by ApplicationThreadPoolScope("Table")

fun openEditor(project: Project, endpoint: TableEndpoint): Editor?  {
    return FileEditorManager.getInstance(project).openTextEditor(
        OpenFileDescriptor(
            project,
            TableVirtualFile(endpoint),
        ),
        true
    )
}
