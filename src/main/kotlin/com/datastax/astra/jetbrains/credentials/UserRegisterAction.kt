package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.telemetry.ClickTarget
import com.datastax.astra.jetbrains.telemetry.TelemetryManager.trackClick
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.ui.WindowWrapper
import com.intellij.openapi.ui.WindowWrapperBuilder
import com.intellij.ui.jcef.JBCefBrowser
import java.awt.Dimension

// Creates an action that links to an external resource
class UserRegisterAction() : AnAction(message("credentials.register.text")), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {

        val myBrowser = JBCefBrowser("astra.datastax.com/register")

        myBrowser.component.preferredSize = Dimension(450, 600)

        val myWindow = WindowWrapperBuilder(WindowWrapper.Mode.FRAME,myBrowser.component)
            .setProject(e.project)
            .setTitle("MyBrowser")
            .build()

        myWindow.show()
        //UserRegisterDialog(e.getRequiredData(LangDataKeys.PROJECT)).showAndGet()
        trackClick(ClickTarget.LINK, "register account url")
    }
}
