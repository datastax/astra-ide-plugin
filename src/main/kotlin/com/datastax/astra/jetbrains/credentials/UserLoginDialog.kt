package com.datastax.astra.jetbrains.credentials

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.jcef.JBCefApp
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefJSQuery
import com.intellij.ui.layout.panel
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel


class UserLoginDialog (
    private val project: Project,
    parent: Component? = null
) : DialogWrapper(project, parent, true, IdeModalityType.PROJECT) {
    //,CoroutineScope by ApplicationThreadPoolScope("Credentials")

    val view = JPanel(BorderLayout())
    val myBrowser = JBCefBrowser("https://astra.datastax.com/")
    val myJSQuery = JBCefJSQuery.create(myBrowser)
    val triggerButton = JButton("Trigger")
    val loginPanel = panel {
        row("Enter credentials then click below when ready.") {
            triggerButton()
        }
    }

    init {
        if (!JBCefApp.isSupported()) {
            // Fallback to an alternative browser-less solution
        }
        //myBrowser.jbCefClient.setProperty("JS_QUERY_POOL_SIZE", 4)
        view.setPreferredSize(Dimension(450, 365))
        view.add(myBrowser.component, BorderLayout.CENTER)
        view.add(loginPanel,BorderLayout.NORTH)
        triggerButton.addActionListener { actionEvent: ActionEvent? ->
            triggerCallback()
        }
        title = "Login"
        setOKButtonText("Login and Get Token")

        myJSQuery.addHandler { returnedString: String? ->
            println(returnedString)
            null // can respond back to JS with JBCefJSQuery.Response
        }

        init()

    }
    override fun createCenterPanel(): JComponent = view

    fun triggerCallback() {
// Inject the query callback into JS
        myBrowser.cefBrowser.executeJavaScript(
            "window.JavaPanelBridge = {" +
                    "myQuery : function(pageTitle) {" +
                    myJSQuery.inject("pageTitle") +
                    "}" +
                    "};" +
                    "JavaPanelBridge.myQuery(document.title);",
            myBrowser.cefBrowser.url, 0
        )
    }
    

    override fun doOKAction() {
        val clickScript = "document.getElementById('kc-login').click();"
        myBrowser.cefBrowser.executeJavaScript(clickScript, null, 0)
        //myBrowser.loadURL(myBrowser.cefBrowser.url+"/settings/tokens")
        //close(OK_EXIT_CODE)
    }


}