package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.MessagesBundle
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.DumbAware

class GetTokenFsmAction : AnAction(MessagesBundle.message("credentials.get_token.text")), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        GetTokenFSM(e.getRequiredData(LangDataKeys.PROJECT))
        // TelemetryManager.trackClick(ClickTarget.LINK, "register account url")
    }
}
