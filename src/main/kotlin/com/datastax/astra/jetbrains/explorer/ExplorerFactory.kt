package com.datastax.astra.jetbrains.explorer

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class ExplorerFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val explorer = ExplorerToolWindow.getInstance(project)
        val contentManager = toolWindow.contentManager
        val content = contentManager.factory.createContent(explorer, null, false)
        contentManager.addContent(content)
    }
}
