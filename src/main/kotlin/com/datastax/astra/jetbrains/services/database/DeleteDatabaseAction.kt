package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.devops_v2.models.StatusEnum
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.explorer.DatabaseNode
import com.datastax.astra.jetbrains.explorer.ExplorerDataKeys.SELECTED_NODES
import com.datastax.astra.jetbrains.explorer.isProcessing
import com.datastax.astra.jetbrains.telemetry.CrudEnum
import com.datastax.astra.jetbrains.telemetry.TelemetryManager.trackDevOpsCrud
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteDatabaseAction :
    DumbAwareAction(message("database.delete.title"), null, null),
    CoroutineScope by ApplicationThreadPoolScope("Database") {

    override fun actionPerformed(e: AnActionEvent) {
        e.getData(SELECTED_NODES)?.map { it as? DatabaseNode }?.singleOrNull()?.run {
            if (DeleteDatabaseDialog(this.nodeProject).showAndGet()) {
                val databaseNode = this
                launch {
                    var response = AstraClient.dbOperationsApi().terminateDatabase(databaseNode.database.id)
                    if (response.isSuccessful) {
                        databaseNode.database = databaseNode.database.copy(status = StatusEnum.TERMINATING)
                        trackDevOpsCrud("Database", databaseNode.database.info.name!!, CrudEnum.DELETE, true)
                    } else {
                        // TODO("implement unsuccessful delete handling")
                        trackDevOpsCrud("Database", databaseNode.database.info.name!!, CrudEnum.CREATE, false, mapOf("httpError" to response.getErrorResponse<Any?>().toString(), "httpResponse" to response.toString()))
                    }
                }
            }
        }
    }

    // If DB is processing grey out access to delete database
    override fun update(e: AnActionEvent) {
        if (e.getData(SELECTED_NODES)?.map { it as? DatabaseNode }?.singleOrNull()?.database?.status?.isProcessing() == true) {
            e.presentation.setEnabled(false)
        }
    }
}
