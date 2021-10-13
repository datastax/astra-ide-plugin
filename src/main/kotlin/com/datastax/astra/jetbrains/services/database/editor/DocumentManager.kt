package com.datastax.astra.jetbrains.services.database.editor

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project

class DocumentManager {
    companion object {
        fun openEditor(project: Project, virtualFile: DocumentVirtualFile) {
            //val virtualFile = CollectionVirtualFile(node.database, node.keyspace.name, node.value, "")
            //TODO: Check if the file is already created and open in an editor
            FileEditorManager.getInstance(project).openFile(virtualFile, false).first().let {
                //TODO: Is this weird because the object reference will be lost here and it is held by the Editor for callbacks
                //TODO: Does it need to be registered in a weak hashmap or similar and be disposed?
                DocumentHandler(it,project,virtualFile)
            }
        }
    }
}
