package com.datastax.astra.jetbrains.services.database

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.project.PossiblyDumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import java.beans.PropertyChangeListener
import javax.swing.JComponent

class TableViewerEditorProvider : FileEditorProvider, PossiblyDumbAware {

    override fun isDumbAware() = true

    override fun accept(project: Project, file: VirtualFile) = file is TableVirtualFile

    override fun createEditor(project: Project, file: VirtualFile): FileEditor = TableViewerEditor()

    override fun getEditorTypeId() = EDITOR_TYPE_ID

    override fun getPolicy() = FileEditorPolicy.HIDE_DEFAULT_EDITOR

    companion object {
        const val EDITOR_TYPE_ID = "Table Viewer"
    }
}

class TableViewerEditor : UserDataHolderBase(), FileEditor{

    private val tablePanel: TableViewerPanel = TableViewerPanel()

    override fun dispose() {}

    override fun getComponent() = tablePanel.component

    override fun getPreferredFocusedComponent() = tablePanel.component

    override fun getName() = "Table Panel"

    override fun setState(state: FileEditorState) {}

    override fun isModified(): Boolean = false

    override fun isValid(): Boolean = true

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {}

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {}

    override fun getCurrentLocation(): FileEditorLocation? = null
}

fun openEditor(project: Project, table: com.datastax.astra.stargate_v2.models.Table): Editor? {
    return FileEditorManager.getInstance(project).openTextEditor(
        OpenFileDescriptor(
            project,
            TableVirtualFile()
        ),
        true
    )
}
