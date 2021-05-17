package com.datastax.astra.jetbrains.explorer

import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.project.Project

fun Project.refreshTree(selectedNode: AbstractTreeNode<*>? = null, structure: Boolean = false) {
    runInEdt {
        ExplorerToolWindow.getInstance(this).invalidateTree(selectedNode, structure)
    }
}
