package com.datastax.astra.jetbrains.explorer

import com.intellij.ide.projectView.PresentationData
import com.intellij.openapi.project.Project
import com.intellij.ui.SimpleTextAttributes

class ExplorerEmptyNode(project: Project, value: String = "empty") :
    ExplorerNode<String>(project, value, nodeIcon = null) {
    override fun getChildren(): List<ExplorerNode<*>> = emptyList()

    override fun update(presentation: PresentationData) {
        presentation.addText(displayName(), SimpleTextAttributes.GRAYED_ATTRIBUTES)
    }

    override fun isAlwaysLeaf() = true
}
