package com.datastax.astra.jetbrains.explorer

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.ex.ActionManagerEx
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.PopupHandler
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.TreeUIHelper
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.ui.tree.AsyncTreeModel
import com.intellij.ui.tree.StructureTreeModel
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.concurrency.Invoker
import com.intellij.util.ui.UIUtil
import java.awt.Component
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeModel

class ExplorerToolWindow(project: Project) : SimpleToolWindowPanel(true, true), Disposable {
    private val actionManager = ActionManagerEx.getInstanceEx()
    private val treePanelWrapper = NonOpaquePanel()

    private val treeModel = ExplorerTreeStructure(project)

    private val structureTreeModel =
        StructureTreeModel(treeModel, null, Invoker.forBackgroundPoolWithReadAction(this), this)
    private val tree = createTree(AsyncTreeModel(structureTreeModel, true, this))
    private val treePanel = ScrollPaneFactory.createScrollPane(tree)

    init {
        background = UIUtil.getTreeBackground()
        setContent(treePanelWrapper)
        treePanelWrapper.setContent(treePanel)
    }

    companion object {
        fun getInstance(project: Project): ExplorerToolWindow =
            ServiceManager.getService(project, ExplorerToolWindow::class.java)
        const val explorerToolWindowPlace = "ExplorerToolWindow"
    }

    private fun createTree(model: TreeModel): Tree {
        val tree = Tree(model)
        tree.isRootVisible = false
        tree.autoscrolls = true

        TreeUIHelper.getInstance().installTreeSpeedSearch(tree)


        tree.addMouseListener(object : PopupHandler() {
            override fun invokePopup(comp: Component?, x: Int, y: Int) {
                val explorerNode = getSelectedNodesSameType<ExplorerNode<*>>()?.get(0) ?: return
                val actionGroupname = (explorerNode as? ResourceActionNode)?.actionGroupName()
                val totalActions = mutableListOf<AnAction>()
                (actionGroupname?.let { actionManager.getAction(it) } as? ActionGroup)?.let {
                    totalActions.addAll(
                        it.getChildren(
                            null
                        )
                    )
                }
                val actionGroup = DefaultActionGroup(totalActions)
                if (actionGroup.childrenCount > 0) {
                    val popupMenu = actionManager.createActionPopupMenu(explorerToolWindowPlace, actionGroup)
                    popupMenu.component.show(comp, x, y)
                }
            }
        })
        return tree
    }

    override fun dispose() {
    }

    private inline fun <reified T : ExplorerNode<*>> getSelectedNodesSameType(): List<T>? {
        val selectedNodes = getSelectedNodes<T>()
        return if (selectedNodes.isNotEmpty() && selectedNodes.all { selectedNodes[0]::class.java.isInstance(it) })
            selectedNodes
        else
            null
    }

    private inline fun <reified T : ExplorerNode<*>> getSelectedNodes() = tree.selectionPaths?.let {
        it.map { it.lastPathComponent }
            .filterIsInstance<DefaultMutableTreeNode>()
            .map { it.userObject }
            .filterIsInstance<T>()
            .toList()
    } ?: emptyList<T>()
}
