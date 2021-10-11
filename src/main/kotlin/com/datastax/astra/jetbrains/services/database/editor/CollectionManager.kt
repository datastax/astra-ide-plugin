package com.datastax.astra.jetbrains.services.database.editor

import com.datastax.astra.jetbrains.explorer.CollectionNode
import com.datastax.astra.jetbrains.services.database.CollectionHandler
import com.intellij.openapi.fileEditor.FileEditorManager

class CollectionManager {
    companion object {
        fun openEditor(node: CollectionNode) {
            val virtualFile = CollectionVirtualFile(node.database, node.keyspace.name, node.value, "")
            //TODO: Check if the file is already created and open in an editor
            FileEditorManager.getInstance(node.nodeProject).openFile(virtualFile, true).first().let {
                //TODO: Is this weird because the object reference will be lost here and it is held by the Editor for callbacks
                //TODO: Does it need to be registered in a weak hashmap or similar and be disposed?
                CollectionHandler(it, node)
            }
        }
    }
}
