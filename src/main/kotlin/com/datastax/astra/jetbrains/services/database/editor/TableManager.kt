package com.datastax.astra.jetbrains.services.database.editor

import com.datastax.astra.jetbrains.explorer.TableNode
import com.datastax.astra.jetbrains.services.database.TableHandler
import com.intellij.openapi.fileEditor.FileEditorManager

class TableManager {
    companion object {
        fun openEditor(node: TableNode) {
            val virtualFile = TableVirtualFile(node.endpoint)
            //TODO: Check if the file is already created and open in an editor
            FileEditorManager.getInstance(node.nodeProject).openFile(virtualFile, true).first().let {
                //TODO: Is this weird because the object reference will be lost here and it is held by the Editor for callbacks
                //TODO: Does it need to be registered in a weak hashmap or similar and be disposed?
                TableHandler(it as TableEditor, node)
            }
        }
    }
}
