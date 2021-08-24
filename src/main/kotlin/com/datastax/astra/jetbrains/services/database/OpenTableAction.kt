package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.explorer.ExplorerDataKeys
import com.datastax.astra.jetbrains.explorer.TableNode
import com.datastax.astra.jetbrains.telemetry.CrudEnum
import com.datastax.astra.jetbrains.telemetry.TelemetryManager.trackStargateCrud
import com.datastax.astra.jetbrains.utils.editor.ui.EndpointTable
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction

class OpenTableAction : DumbAwareAction(message("table.open.title"), null, null) {
    override fun actionPerformed(e: AnActionEvent) {
        e.getData(ExplorerDataKeys.SELECTED_NODES)?.map { it as? TableNode }?.singleOrNull()?.run {
            TableViewerPanel(this.nodeProject,EndpointTable(this.database, this.table.keyspace.toString(), this.table))

        }
    }
}
