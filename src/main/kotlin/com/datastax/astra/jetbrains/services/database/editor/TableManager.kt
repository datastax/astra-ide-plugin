package com.datastax.astra.jetbrains.services.database.editor

import com.datastax.astra.jetbrains.explorer.TableNode
import com.datastax.astra.jetbrains.services.database.TableHandler
import com.intellij.openapi.fileEditor.FileEditorManager

class TableManager {
    companion object {
        fun openEditor(node: TableNode) {
            val virtualFile = TableVirtualFile(node.endpoint)
            FileEditorManager.getInstance(node.nodeProject).let { manager->
                val fileNotOpen = manager.openFiles.filterIsInstance<TableVirtualFile>().none { it == virtualFile }
                manager.openFile(virtualFile, true).first().let { editor ->
                    if (fileNotOpen) {
                        TableHandler(node.nodeProject, editor as TableEditor, node)
                    }
                }
            }
        }
    }
}
