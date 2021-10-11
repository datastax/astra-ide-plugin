package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.MessagesBundle
import com.datastax.astra.jetbrains.explorer.ExplorerDataKeys
import com.datastax.astra.jetbrains.explorer.TableNode
import com.datastax.astra.jetbrains.services.database.editor.TableManager
import com.datastax.astra.jetbrains.telemetry.CrudEnum
import com.datastax.astra.jetbrains.telemetry.TelemetryManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction

class OpenTableAction : DumbAwareAction(MessagesBundle.message("table.open.title"), null, null) {
    override fun actionPerformed(e: AnActionEvent) {
        e.getData(ExplorerDataKeys.SELECTED_NODES)?.map { it as? TableNode }?.singleOrNull()?.let {
            TableManager.openEditor(it)
            TelemetryManager.trackStargateCrud("Table", it.endpoint.table.name!!, CrudEnum.READ, true)
        }
    }
}
