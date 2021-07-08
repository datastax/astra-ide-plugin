package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.jcef.JBCefApp
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefJSQuery
import com.intellij.ui.layout.panel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.tools.ant.taskdefs.Execute.launch
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class UserLoginDialog(
    private val project: Project,
    parent: Component? = null
) : DialogWrapper(project, parent, true, IdeModalityType.PROJECT),
    CoroutineScope by ApplicationThreadPoolScope("Credentials") {

    val myBrowser = JBCefBrowser("https://astra.datastax.com/")
    val myJSQuery = JBCefJSQuery.create(myBrowser)
    val getDom = JBCefJSQuery.create(myBrowser)
    val triggerButton = JButton("Trigger")
    val anotherButton = JButton("Load Page")
    init {
        if (!JBCefApp.isSupported()) {
            // Fallback to an alternative browser-less solution
        }
        myBrowser.jbCefClient.setProperty("JS_QUERY_POOL_SIZE", 4)
        myJSQuery.addHandler { returnedString: String? ->
            println(returnedString)
            null // can respond back to JS with JBCefJSQuery.Response
        }
        getDom.addHandler { returnedString: String? ->
            println(returnedString)
            null // can respond back to JS with JBCefJSQuery.Response
        }

        triggerButton.addActionListener { actionEvent: ActionEvent? ->
            triggerCallback()
        }
        anotherButton.addActionListener { actionEvent: ActionEvent? ->
            changeURL()
        }
    }

    val loginPanel = panel {
        row("Enter credentials then click below when ready.") {
            triggerButton()
        }
        row("Buttons: ") {
            anotherButton()
        }
    }

    val view = JPanel(BorderLayout())
    init {

        view.setPreferredSize(Dimension(450, 365))
        view.add(myBrowser.component, BorderLayout.CENTER)
        view.add(loginPanel, BorderLayout.NORTH)

        title = "Login"
        setOKButtonText("Login and Get Token")
        init()
        launch {
            detectAstraHomeURL()
        }
    }
    override fun createCenterPanel(): JComponent = view

    fun triggerCallback() {
// Inject the query callback into JS
        myBrowser.cefBrowser.executeJavaScript(
            "window.JavaPanelBridge = {" +
                "myQuery : function(string) {" +
                myJSQuery.inject("string") +
                "}" +
                "};" +
                // "document.getElementsByClassName('astra-MuiSelect-nativeInput').value = 43745b73-ad46-46e4-b826-c15d06d2cea0" +
                // "document.getElementById('generate-token-select-button').click();"+
                "var delayMillis = 1000;" +
                "setTimeout(function(){" +
                "" +
                "var n = document.documentElement.innerHTML.search('AstraCS');" +
                "JavaPanelBridge.myQuery(delayMillis.toString());" +
                "}, delayMillis);",
            myBrowser.cefBrowser.url,
            0
        )
    }

    fun changeURL() {
        val urlStr = myBrowser.cefBrowser.url.split("/")
        myBrowser.loadURL("https://astra.datastax.com/org/${urlStr[3]}/settings/tokens")
    }

    suspend fun detectAstraHomeURL() {
        val astraURLBase = "https://astra.datastax.com/"
        var astraMainLoaded = false
        delay(650L)
        while (!astraMainLoaded) {
            delay(350L)
            if (myBrowser.cefBrowser.url.contains(astraURLBase)) {
                if (orgIDValid(myBrowser.cefBrowser.url.split("/")[3])) {
                    astraMainLoaded = true
                }
            }
        }
        println("URL Changed!")
        delay(500L)
        myBrowser.cefBrowser.url.split("/").forEach {
            println(it)
        }
        myBrowser.jbCefCookieManager.getCookies()?.forEach {
            if (it.name == "dstaxprodauthz") {
                println("Cookie: ${it.name}")
                println("Value: ${it.value}")
            }
        }
    }

    fun orgIDValid(orgUUID: String): Boolean {
        orgUUID.split("-").forEachIndexed { index, element ->
            when (index) {
                0 -> if (!(element.length == 8 || element.all { it.isLetterOrDigit() })) return false
                1 -> if (!(element.length == 4 || element.all { it.isLetterOrDigit() })) return false
                2 -> if (!(element.length == 4 || element.all { it.isLetterOrDigit() })) return false
                3 -> if (!(element.length == 4 || element.all { it.isLetterOrDigit() })) return false
                4 -> if (!(element.length == 12 || element.all { it.isLetterOrDigit() })) return false
                else -> {
                    return false
                }
            }
        }
        return true
    }

    override fun doOKAction() {
        myBrowser.cefBrowser.url.split("/").forEach {
            println(it)
        }
    }
}
