package com.datastax.astra.jetbrains.explorer

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project

class ExplorerRootNode(private val nodeProject: Project) : AbstractTreeNode<Any>(nodeProject, Object()) {
    override fun update(presentation: PresentationData) {}

    override fun getChildren(): List<ExplorerNode<*>> {
        return listOf(DatabaseParentNode(nodeProject))
    }
}
