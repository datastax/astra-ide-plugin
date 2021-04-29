package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.MessagesBundle.message
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction

class DeleteDatabaseAction : DumbAwareAction(message("database.delete.title"), null, null) {
    override fun actionPerformed(e: AnActionEvent) {
        TODO("Implement")
    }
}
