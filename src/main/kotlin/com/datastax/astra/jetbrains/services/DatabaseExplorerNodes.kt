package com.datastax.astra.jetbrains.explorer

import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.stargate_v2.models.Keyspace
import com.datastax.astra.stargate_v2.models.Table
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import kotlinx.coroutines.runBlocking
import java.net.URI

class DatabaseParentNode(project: Project) :
    ExplorerNode<String>(project, "Databases", null),
    ResourceActionNode, ResourceParentNode {

    override fun actionGroupName(): String = "astra.explorer.databases"
    override fun getChildren(): List<ExplorerNode<*>> = super.getChildren()
    override fun getChildrenInternal(): List<ExplorerNode<*>> = runBlocking {
        val response = AstraClient.operationsApi(project).listDatabases()
        if (response.isSuccessful) {
            response.body()?.map { DatabaseNode(nodeProject, it) } ?: emptyList()
        } else {
            emptyList()
        }
    }


}

class DatabaseNode(project: Project, val database: Database) :
    ExplorerNode<String>(project, database.info.name.orEmpty(), null),
    ResourceActionNode, ResourceParentNode {

    override fun actionGroupName(): String = "astra.explorer.databases.database"
    override fun emptyChildrenNode(): ExplorerEmptyNode =
        ExplorerEmptyNode(nodeProject, message("astra.no_keyspaces_in_database"))

    override fun getChildren(): List<ExplorerNode<*>> = super.getChildren()
    override fun getChildrenInternal(): List<ExplorerNode<*>> = runBlocking {
        val basePath = database.dataEndpointUrl.orEmpty().removeSuffix(URI(database.dataEndpointUrl).rawPath)
        AstraClient.schemasApi(basePath)
            .getKeyspaces(AstraClient.accessToken, null).body()?.data?.map {
                KeyspaceNode(nodeProject, it, database)
            } ?: emptyList()
    }
}

class KeyspaceNode(project: Project, val keyspace: Keyspace, val database: Database) :
    ExplorerNode<String>(project, keyspace.name, null),
    ResourceActionNode, ResourceParentNode {

    override fun actionGroupName(): String = "astra.explorer.databases.keyspace"
    override fun emptyChildrenNode(): ExplorerEmptyNode =
        ExplorerEmptyNode(nodeProject, message("astra.no_tables_in_keyspace"))

    override fun getChildren(): List<ExplorerNode<*>> = super.getChildren()
    override fun getChildrenInternal(): List<ExplorerNode<*>> = runBlocking {
        val basePath = database.dataEndpointUrl.orEmpty().removeSuffix(URI(database.dataEndpointUrl).rawPath)
        AstraClient.schemasApi(basePath)
            .getTables(AstraClient.accessToken, keyspace.name, null).body()?.data?.map {
                TableNode(nodeProject, it)
            } ?: emptyList()
    }
}

class TableNode(project: Project, val table: Table) : ExplorerNode<String>(project, table.name.orEmpty(), null) {
    override fun getChildren(): List<AbstractTreeNode<*>> = emptyList()
}
