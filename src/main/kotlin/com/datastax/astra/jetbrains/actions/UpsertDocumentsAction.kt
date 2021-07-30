package com.datastax.astra.jetbrains.actions

import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.credentials.ProfileManager
import com.datastax.astra.jetbrains.explorer.refreshTree
import com.datastax.astra.jetbrains.telemetry.ClickTarget
import com.datastax.astra.jetbrains.telemetry.TelemetryManager
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import kotlinx.coroutines.CoroutineScope

class UpsertDocumentsAction(text: String = message("collection.editor.upsert.title")):
    AnAction(text, null, AllIcons.Actions.StepOutCodeBlock),
    CoroutineScope by ApplicationThreadPoolScope("Credentials") {

    override fun update(e: AnActionEvent) {
    }

    override fun actionPerformed(e: AnActionEvent) {

    }
}