package com.datastax.astra.jetbrains.utils.editor

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.PossiblyDumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class PagedEditorProvider : FileEditorProvider, PossiblyDumbAware {
    override fun accept(project: Project, file: VirtualFile): Boolean {
        TODO("Not yet implemented")
    }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        TODO("Not yet implemented")
    }

    override fun getEditorTypeId(): String {
        TODO("Not yet implemented")
    }

    override fun getPolicy(): FileEditorPolicy {
        TODO("Not yet implemented")
    }

}

