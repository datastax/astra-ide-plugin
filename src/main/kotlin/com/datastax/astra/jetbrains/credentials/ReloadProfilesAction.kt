package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.explorer.refreshTree
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.DumbAware
import kotlinx.coroutines.CoroutineScope

class ReloadProfilesAction(text: String = message("credentials.file.reload")) :
    AnAction(text, null, AllIcons.Actions.SwapPanels),
    DumbAware,
    CoroutineScope by ApplicationThreadPoolScope("Credentials") {

    override fun update(e: AnActionEvent) {
    }

    override fun actionPerformed(e: AnActionEvent) {
        val profileSettingsManager = ProfileManager.getInstance(e.getRequiredData(LangDataKeys.PROJECT))
        profileSettingsManager.loadProfiles()
        e.getRequiredData(LangDataKeys.PROJECT).refreshTree()
    }
}
