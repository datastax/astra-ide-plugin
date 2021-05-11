package com.datastax.astra.jetbrains.explorer

import com.datastax.astra.jetbrains.credentials.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ActionManagerEx
import com.intellij.openapi.application.runInEdt
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
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeModel

class ExplorerToolWindow(project: Project) : SimpleToolWindowPanel(true, true), ProfileStateChangeNotifier, Disposable {
    private val actionManager = ActionManagerEx.getInstanceEx()
    private val treePanelWrapper = NonOpaquePanel()

    private val treeModel = ExplorerTreeStructure(project)

    private val structureTreeModel =
        StructureTreeModel(treeModel, null, Invoker.forBackgroundPoolWithReadAction(this), this)
    private val tree = createTree(AsyncTreeModel(structureTreeModel, true, this))
    private val treePanel = ScrollPaneFactory.createScrollPane(tree)

    init {
        val group = DefaultActionGroup(
            SettingsSelectorComboBoxAction(project, ChangeProfileSettingsMode.PROFILE),
            SettingsSelectorComboBoxAction(project, ChangeProfileSettingsMode.TOKEN)
        )
        toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, group, true).apply {
            layoutPolicy = ActionToolbar.WRAP_LAYOUT_POLICY
        }.component


        background = UIUtil.getTreeBackground()
        setContent(treePanelWrapper)
        treePanelWrapper.setContent(treePanel)

        project.messageBus.connect(this).subscribe(ProfileManager.CONNECTION_SETTINGS_STATE_CHANGED, this)

    }

    companion object {
        fun getInstance(project: Project): ExplorerToolWindow =
            ServiceManager.getService(project, ExplorerToolWindow::class.java)
        const val explorerToolWindowPlace = "ExplorerToolWindow"
    }
    private fun createInfoPanel(state: ProfileState): JComponent {
        val panel = NonOpaquePanel(GridBagLayout())
        return panel
    }

    override fun profileStateChanged(newProfile: ProfileState) {
        runInEdt {
            treePanelWrapper.setContent(
                when (newProfile) {
                    is ProfileState.ValidConnection -> {
                        //invalidateTree()
                        treePanel
                    }
                    else -> createInfoPanel(newProfile)
                }
            )
        }
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
