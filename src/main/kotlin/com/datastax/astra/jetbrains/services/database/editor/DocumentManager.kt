package com.datastax.astra.jetbrains.services.database.editor

import com.datastax.astra.jetbrains.explorer.CollectionNode
import com.datastax.astra.jetbrains.services.database.CollectionHandler
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project

class DocumentManager {
    companion object {
        fun openEditor(project: Project, virtualFile: DocumentVirtualFile) {
            //val virtualFile = CollectionVirtualFile(node.database, node.keyspace.name, node.value, "")

            FileEditorManager.getInstance(project).let { manager ->
                val fileNotOpen = manager.openFiles.filterIsInstance<DocumentVirtualFile>().none { it == virtualFile }
                manager.openFile(virtualFile, true).first().let { editor ->
                    if (fileNotOpen) {
                        DocumentHandler(editor,project,virtualFile)
                    }
                }
            }
        }
    }
}
