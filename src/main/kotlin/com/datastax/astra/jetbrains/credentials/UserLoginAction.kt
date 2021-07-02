package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.MessagesBundle
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.DumbAware

class UserLoginAction : AnAction(MessagesBundle.message("credentials.register.text")), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        UserLoginDialog(e.getRequiredData(LangDataKeys.PROJECT)).showAndGet()
        // TelemetryManager.trackClick(ClickTarget.LINK, "register account url")
    }
}
