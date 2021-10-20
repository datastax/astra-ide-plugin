package com.datastax.astra.jetbrains.explorer

import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.credentials.*
import com.datastax.astra.jetbrains.explorer.ExplorerDataKeys.SELECTED_NODES
import com.datastax.astra.jetbrains.utils.buildTextPanel
import com.datastax.astra.jetbrains.utils.editor.ui.AstraFileEditorUIService
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.ide.util.treeView.TreeState
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ActionManagerEx
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.DoubleClickListener
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
import java.awt.GridBagLayout
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeModel

class ExplorerToolWindow(val project: Project) : SimpleToolWindowPanel(true, true), ProfileStateChangeNotifier, Disposable {
    private val actionManager = ActionManagerEx.getInstanceEx()
    private val treePanelWrapper = NonOpaquePanel()

    private val treeModel = ExplorerTreeStructure(project)

    private val structureTreeModel =
        StructureTreeModel(treeModel, null, Invoker.forBackgroundPoolWithReadAction(this), this)
    val tree = createTree(AsyncTreeModel(structureTreeModel, true, this))
    private val treePanel = ScrollPaneFactory.createScrollPane(tree)
    private val profileManager = ProfileManager.getInstance(project)

    init {

        val group = DefaultActionGroup(
            SettingsSelectorComboBoxAction(project),
        )
        toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, group, true).apply {
            layoutPolicy = ActionToolbar.WRAP_LAYOUT_POLICY
            setTargetComponent(this@ExplorerToolWindow)
        }.component

        background = UIUtil.getTreeBackground()
        setContent(treePanelWrapper)
        treePanelWrapper.setContent(treePanel)

        project.messageBus.connect(this).subscribe(ProfileManager.CONNECTION_SETTINGS_STATE_CHANGED, this)
        profileStateChanged(profileManager.profileState)

        //Start fileEditor listener for JSON toolbar
        AstraFileEditorUIService.getService(project)
    }

    companion object {
        fun getInstance(project: Project): ExplorerToolWindow =
            project.getService(ExplorerToolWindow::class.java)
        const val explorerToolWindowPlace = "ExplorerToolWindow"
    }
    private fun createInfoPanel(): JComponent {
        val panel = NonOpaquePanel(GridBagLayout())
        return panel
    }
    fun showWaitPanel() {
        treePanelWrapper.setContent(
            buildTextPanel("Please wait while the server processes the token...", 20, 16)
        )
    }

    override fun profileStateChanged(newProfile: ProfileState) {
        runInEdt {
            treePanelWrapper.setContent(
                when (newProfile) {
                    is ProfileState.ValidConnection -> {
                        clearCacheMap(this.project)
                        invalidateTree()
                        treePanel
                    }
                    is ProfileState.IncompleteConfiguration -> {
                        createInfoPanel()
                    }
                    else -> createInfoPanel()
                }
            )
        }
    }

    fun invalidateTree(selectedNode: AbstractTreeNode<*>? = null, structure: Boolean = false) {
        withSavedState(tree) {
            if (selectedNode != null) {
                structureTreeModel.invalidate(selectedNode, structure)
            } else {
                structureTreeModel.invalidate()
            }
        }


        // TODO: Trigger reset and watching of databaseMap
    }

    private fun withSavedState(tree: Tree, block: () -> Unit) {
        val state = TreeState.createOn(tree)
        block()
        state.applyTo(tree)
    }

    override fun getData(dataId: String): Any? {
        return if (SELECTED_NODES.`is`(dataId)) {
            getSelectedNodes<ExplorerNode<*>>()
        } else {
            super.getData(dataId)
        }
    }

    private fun createTree(model: TreeModel): Tree {
        val tree = Tree(model)
        tree.isRootVisible = false
        tree.autoscrolls = true

        TreeUIHelper.getInstance().installTreeSpeedSearch(tree)

        object : DoubleClickListener() {
            override fun onDoubleClick(event: MouseEvent): Boolean {
                val path = tree.getClosestPathForLocation(event.x, event.y)
                ((path?.lastPathComponent as? DefaultMutableTreeNode)?.userObject as? ExplorerNode<*>)?.onDoubleClick()
                return true
            }
        }.installOn(tree)

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
        return if (selectedNodes.isNotEmpty() && selectedNodes.all { selectedNodes[0]::class.java.isInstance(it) }) {
            selectedNodes
        } else {
            null
        }
    }

    private inline fun <reified T : ExplorerNode<*>> getSelectedNodes() = tree.selectionPaths?.let {
        it.map { it.lastPathComponent }
            .filterIsInstance<DefaultMutableTreeNode>()
            .map { it.userObject }
            .filterIsInstance<T>()
            .toList()
    } ?: emptyList<T>()
}

object ExplorerDataKeys {
    val SELECTED_NODES = DataKey.create<List<ExplorerNode<*>>>("astra.explorer.explorerNodes")
}
