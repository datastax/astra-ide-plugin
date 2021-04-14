package com.datastax.astra.jetbrains.explorer

import com.intellij.openapi.project.Project

interface ResourceParentNode {
    val nodeProject: Project
    fun getChildren(): List<ExplorerNode<*>> = try {
        val children = getChildrenInternal()
        if (children.isEmpty()) {
            listOf(emptyChildrenNode())
        } else {
            children
        }
    } catch (e: Exception) {
        listOf()
    }

    fun emptyChildrenNode(): ExplorerEmptyNode = ExplorerEmptyNode(nodeProject)
    fun getChildrenInternal(): List<ExplorerNode<*>>
}
