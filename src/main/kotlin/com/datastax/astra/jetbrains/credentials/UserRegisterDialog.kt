package com.datastax.astra.jetbrains.credentials

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.util.Disposer
import com.intellij.ui.jcef.JBCefApp
import com.intellij.ui.jcef.JBCefBrowser
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JPanel

class UserRegisterDialog(
    private val project: Project,
    parent: Component? = null
) : DialogWrapper(project, parent, true, IdeModalityType.PROJECT) {
    // ,CoroutineScope by ApplicationThreadPoolScope("Credentials")

    val view = JPanel(BorderLayout())

    val myBrowser = JBCefBrowser("astra.datastax.com/register")

    init {

        if (!JBCefApp.isSupported()) {
            // Fallback to an alternative browser-less solution
        }
        view.setPreferredSize(Dimension(450, 600))
        view.add(myBrowser.component)
        title = "Register"
        setOKButtonText("Done Registering")
        init()
    }

    override fun createCenterPanel(): JComponent = view

    // When user finished registering load the login page
    override fun doOKAction() {
        close(OK_EXIT_CODE)
        UserLoginDialog(project).showAndGet()
    }

    // If user clicks cancel dispose of the current browser session
    override fun doCancelAction() {
        Disposer.dispose(myBrowser)
        close(CANCEL_EXIT_CODE)
    }
}
