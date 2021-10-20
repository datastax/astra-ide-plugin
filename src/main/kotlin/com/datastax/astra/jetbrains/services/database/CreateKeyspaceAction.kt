package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.explorer.DatabaseNode
import com.datastax.astra.jetbrains.explorer.ExplorerDataKeys.SELECTED_NODES
import com.datastax.astra.jetbrains.explorer.isProcessing
import com.datastax.astra.jetbrains.explorer.refreshTree
import com.datastax.astra.jetbrains.telemetry.CrudEnum
import com.datastax.astra.jetbrains.telemetry.TelemetryManager.trackDevOpsCrud
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.editor.ui.ExplorerTreeChangeEventListener
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.DumbAwareAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CreateKeyspaceAction :
    DumbAwareAction(message("keyspace.create.title"), null, null),
    CoroutineScope by ApplicationThreadPoolScope("Database") {

    override fun actionPerformed(e: AnActionEvent) {
        e.getData(SELECTED_NODES)?.map { it as? DatabaseNode }?.singleOrNull()?.run {
            val dialog = CreateKeyspaceDialog(this, e.getRequiredData(LangDataKeys.PROJECT))
            if (dialog.showAndGet()) {
                val keyspace = dialog.keyspace
                val dbNode = this
                launch {
                    val resp = AstraClient.dbOperationsApi().addKeyspace(database.id, keyspace)
                    if (resp.isSuccessful) {
                        delay(10000)
                        nodeProject.messageBus.syncPublisher(ExplorerTreeChangeEventListener.TOPIC).rebuildEndpointList()
                        dbNode.nodeProject.refreshTree(dbNode, true)
                        trackDevOpsCrud("Keyspace", keyspace, CrudEnum.CREATE, true)
                    } else {
                        trackDevOpsCrud("Keyspace", keyspace, CrudEnum.CREATE, false, mapOf("httpError" to resp.getErrorResponse<Any?>().toString(), "httpResponse" to resp.toString()))
                    }
                }
            }
        }
    }

    // If DB is processing grey out access to creating a keyspace
    override fun update(e: AnActionEvent) {
        if (e.getData(SELECTED_NODES)?.map { it as? DatabaseNode }?.singleOrNull()?.database?.status?.isProcessing() == true) {
            e.presentation.setEnabled(false)
        }
    }
}
