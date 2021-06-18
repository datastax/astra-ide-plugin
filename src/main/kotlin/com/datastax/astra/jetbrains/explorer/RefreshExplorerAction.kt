package com.datastax.astra.jetbrains.explorer;

import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.DumbAware
import kotlinx.coroutines.CoroutineScope

class RefreshExplorerAction(text: String = message("explorer.refresh.description")) :
    AnAction(text, null, AllIcons.Actions.Refresh),
    DumbAware,
    CoroutineScope by ApplicationThreadPoolScope("Explorer") {

    override fun update(e: AnActionEvent) {
    }

    override fun actionPerformed(e: AnActionEvent) {
        //TODO:This is where we should record telemetry on the refresh button
        // But would send many records if clicked a lot and AWS doesn't record this afaik
        e.getRequiredData(LangDataKeys.PROJECT).refreshTree()
    }
}
