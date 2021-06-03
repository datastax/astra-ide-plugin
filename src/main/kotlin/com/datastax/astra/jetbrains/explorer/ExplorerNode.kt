package com.datastax.astra.jetbrains.explorer

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.ui.SimpleTextAttributes
import javax.swing.Icon

abstract class ExplorerNode<T>(val nodeProject: Project, value: T, private val nodeIcon: Icon?) :
    AbstractTreeNode<T>(nodeProject, value) {
    override fun update(presentation: PresentationData) {
        presentation.let {
            it.addText(displayName(), SimpleTextAttributes.REGULAR_ATTRIBUTES)
        }
    }

    open fun onDoubleClick() {}
    open fun displayName() = value.toString()
    override fun toString(): String = displayName()
}
