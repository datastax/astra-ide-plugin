package com.datastax.astra.jetbrains.explorer

import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.util.treeView.AbstractTreeNode

class ExplorerTreeStructureProvider : TreeStructureProvider {
    /**
     * Allows a plugin to modify the list of children displayed for the specified node in the
     * project view.
     *
     * @param parent   the parent node.
     * @param children the list of child nodes according to the default project structure.
     * Elements of the collection are of type [ProjectViewNode].
     * @param settings the current project view settings.
     * @return the modified collection of child nodes, or `children` if no modifications
     * are required.
     */
    override fun modify(
        parent: AbstractTreeNode<*>,
        children: MutableCollection<AbstractTreeNode<*>>,
        settings: ViewSettings?
    ): MutableCollection<AbstractTreeNode<*>> =
        // By default sort the children in alphabetical order
        children.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.toString() }).toMutableList()
}
