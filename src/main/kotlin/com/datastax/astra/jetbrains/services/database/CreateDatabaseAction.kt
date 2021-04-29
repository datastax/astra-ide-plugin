package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.MessagesBundle.message
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.DumbAwareAction

class CreateDatabaseAction : DumbAwareAction(message("database.create.title"), null, null) {
    override fun actionPerformed(e: AnActionEvent) {
        CreateDatabaseDialog(e.getRequiredData(LangDataKeys.PROJECT)).show()
    }
}
