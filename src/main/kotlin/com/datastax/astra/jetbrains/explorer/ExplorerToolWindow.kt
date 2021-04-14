package com.datastax.astra.jetbrains.explorer

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.TreeUIHelper
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.ui.tree.AsyncTreeModel
import com.intellij.ui.tree.StructureTreeModel
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.concurrency.Invoker
import com.intellij.util.ui.UIUtil
import javax.swing.tree.TreeModel

class ExplorerToolWindow(project: Project) : SimpleToolWindowPanel(true, true), Disposable {
    private val treePanelWrapper = NonOpaquePanel()

    private val treeModel = ExplorerTreeStructure(project)

    private val structureTreeModel = StructureTreeModel(treeModel, null, Invoker.forBackgroundPoolWithReadAction(this), this)
    private val tree = createTree(AsyncTreeModel(structureTreeModel, true, this))
    private val treePanel = ScrollPaneFactory.createScrollPane(tree)

    init {
        background = UIUtil.getTreeBackground()
        setContent(treePanelWrapper)
        treePanelWrapper.setContent(treePanel)
    }
    companion object {
        fun getInstance(project: Project): ExplorerToolWindow = ServiceManager.getService(project, ExplorerToolWindow::class.java)
    }

    private fun createTree(model: TreeModel): Tree {
        val tree = Tree(model)
        tree.isRootVisible = false
        tree.autoscrolls = true

        TreeUIHelper.getInstance().installTreeSpeedSearch(tree)
        return tree
    }

    override fun dispose() {
    }
}
