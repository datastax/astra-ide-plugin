package com.datastax.astra.jetbrains.explorer

import com.datastax.astra.jetbrains.MyBundle.message
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project

class DatabaseParentNode(project: Project) :
    ExplorerNode<String>(project, "Databases", null), ResourceParentNode {

    override fun getChildren(): List<ExplorerNode<*>> = super.getChildren()
    override fun getChildrenInternal(): List<ExplorerNode<*>> {
        //TODO: (DEVOPS API) GET /v2/databases
        return listOf(
            DatabaseNode(nodeProject, "Alpha"), DatabaseNode(nodeProject, "Beta"),
            DatabaseNode(nodeProject, "Gamma"), DatabaseNode(nodeProject, "Delta")
        )
    }
}

class DatabaseNode(project: Project, val dbName: String) : ExplorerNode<String>(project, dbName, null), ResourceParentNode {
    override fun emptyChildrenNode(): ExplorerEmptyNode = ExplorerEmptyNode(nodeProject, message("astra.no_keyspaces_in_database"))
    override fun getChildren(): List<ExplorerNode<*>> = super.getChildren()
    override fun getChildrenInternal(): List<ExplorerNode<*>> {
            //TODO: (REST API) GET /v2/schemas/keyspaces
            return listOf(
                KeyspaceNode(nodeProject, displayName() + ": KS ONE"),
                KeyspaceNode(nodeProject, displayName() + ": KS TWO")
            )
        }
}

class KeyspaceNode(project: Project,  val keyspaceName: String) :
    ExplorerNode<String>(project, keyspaceName, null), ResourceParentNode {
    override fun emptyChildrenNode(): ExplorerEmptyNode = ExplorerEmptyNode(nodeProject, message("astra.no_tables_in_keyspace"))
    override fun getChildren(): List<ExplorerNode<*>> = super.getChildren()
    override fun getChildrenInternal(): List<ExplorerNode<*>> {
        //TODO: (REST API) GET /v2/schemas/keyspaces/{keyspaceName}/tables
        //return listOf(TableNode(nodeProject, tableName))
        return emptyList()
    }
}

class TableNode(project: Project, val tableName: String) : ExplorerNode<String>(project,tableName,null) {
    override fun getChildren(): List<AbstractTreeNode<*>> = emptyList()
}
