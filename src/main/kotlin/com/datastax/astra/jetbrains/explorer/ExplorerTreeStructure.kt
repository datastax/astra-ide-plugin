package com.datastax.astra.jetbrains.explorer

import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.util.treeView.AbstractTreeStructureBase
import com.intellij.openapi.project.Project

class ExplorerTreeStructure(project: Project) : AbstractTreeStructureBase(project) {

    val defaultTreeStructureProvider = ExplorerTreeStructureProvider()

    override fun getRootElement() = ExplorerRootNode(myProject)

    override fun commit() {}

    override fun hasSomethingToCommit() = false

    override fun getProviders(): List<TreeStructureProvider>? = listOf(defaultTreeStructureProvider)
}
