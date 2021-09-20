package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.explorer.TableEndpoint
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.project.PossiblyDumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFile
import kotlinx.coroutines.CoroutineScope
import java.beans.PropertyChangeListener

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
    private val tableWindow = TableManager(project, tableVirtualFile.endpoint).tableUI

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
}

class TableVirtualFile(val endpoint: TableEndpoint) :
    LightVirtualFile(endpoint.table?.name.orEmpty()),
    CoroutineScope by ApplicationThreadPoolScope("Table")

fun openEditor(project: Project, endpoint: TableEndpoint): Array<out FileEditor> {
    return FileEditorManager.getInstance(project).openFile(
        TableVirtualFile(endpoint),
        true
    )
}
