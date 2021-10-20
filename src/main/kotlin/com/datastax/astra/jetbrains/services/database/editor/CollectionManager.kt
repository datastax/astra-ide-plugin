package com.datastax.astra.jetbrains.services.database.editor

import com.datastax.astra.jetbrains.explorer.CollectionNode
import com.datastax.astra.jetbrains.explorer.TableNode
import com.datastax.astra.jetbrains.services.database.CollectionHandler
import com.datastax.astra.jetbrains.services.database.TableHandler
import com.intellij.openapi.fileEditor.FileEditorManager

class CollectionManager {
    companion object {
        fun openEditor(node: CollectionNode) {
            val virtualFile = CollectionVirtualFile(node.database, node.keyspace.name, node.value, "")
            FileEditorManager.getInstance(node.nodeProject).let { manager ->
                val fileNotOpen = manager.openFiles.filterIsInstance<CollectionVirtualFile>().none { it == virtualFile }
                manager.openFile(virtualFile, true).first().let { editor ->
                    if (fileNotOpen) {
                        CollectionHandler(node.nodeProject, editor, node)
                    }
                }
            }
        }
    }
}
