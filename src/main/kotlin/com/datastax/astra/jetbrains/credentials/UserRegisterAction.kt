package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.MessagesBundle.message
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

//Creates an action that links to an external resource
class UserRegisterAction () : AnAction("Register"), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        //TODO: Track that the user clicked the register button in telemetry
        BrowserUtil.browse(message("credentials.register.link"))
    }
}