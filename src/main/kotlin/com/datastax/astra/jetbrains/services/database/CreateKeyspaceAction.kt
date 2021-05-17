package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.explorer.DatabaseNode
import com.datastax.astra.jetbrains.explorer.ExplorerDataKeys.SELECTED_NODES
import com.datastax.astra.jetbrains.explorer.KeyspaceNode
import com.datastax.astra.jetbrains.services.database.CreateDatabaseDialog
import com.datastax.astra.jetbrains.services.database.CreateKeyspaceDialog
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.DumbAwareAction
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.launch

class CreateKeyspaceAction
    : DumbAwareAction(message("keyspace.create.title"), null, null),
    CoroutineScope by ApplicationThreadPoolScope("Database") {

    override fun actionPerformed(e: AnActionEvent) {
        e.getData(SELECTED_NODES)?.map { it as? DatabaseNode }?.singleOrNull()?.run {
            val dialog = CreateKeyspaceDialog(this,e.getRequiredData(LangDataKeys.PROJECT))
            val ok = dialog.showAndGet()
            val keyspace = dialog.keyspace
            print(keyspace)
        }
    }
}
