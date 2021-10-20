package com.datastax.astra.jetbrains.services.database.editor

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.PossiblyDumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

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
