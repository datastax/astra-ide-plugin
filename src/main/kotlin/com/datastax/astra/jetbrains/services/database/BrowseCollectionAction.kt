package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.explorer.CollectionNode
import com.datastax.astra.jetbrains.explorer.ExplorerDataKeys
import com.datastax.astra.jetbrains.utils.editor.ui.EndpointCollection
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction

class BrowseCollectionAction : DumbAwareAction(message("collection.open.title"), null, null) {
    override fun actionPerformed(e: AnActionEvent) {
        e.getData(ExplorerDataKeys.SELECTED_NODES)?.map { it as? CollectionNode }?.singleOrNull()?.run {
            CollectionBrowserPanel(this.nodeProject, EndpointCollection(this.database, this.keyspace.name, this.collection.name),false)
        }
    }
}

class BrowseAndEditCollectionAction : DumbAwareAction(message("collection.open.title"), null, null) {
    override fun actionPerformed(e: AnActionEvent) {
        e.getData(ExplorerDataKeys.SELECTED_NODES)?.map { it as? CollectionNode }?.singleOrNull()?.run {
            CollectionBrowserPanel(this.nodeProject, EndpointCollection(this.database, this.keyspace.name, this.collection.name),false)
        }
    }
}
