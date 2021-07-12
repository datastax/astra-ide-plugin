package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.services.database.CreateKeyspaceDialog
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.WindowWrapper
import com.intellij.openapi.ui.WindowWrapperBuilder
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.layout.panel
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Point
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JPanel

class GetTokenAction :
//TODO: Add all strings to messageBundle
    DumbAwareAction("Get Token", null, null),
    CoroutineScope by ApplicationThreadPoolScope("Credentials") {
    val loginBrowser: JBCefBrowser = JBCefBrowser("https://astra.datastax.com/")
    lateinit var loginState: BrowserState

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getRequiredData(LangDataKeys.PROJECT)

        launch {
            val loginSize = Dimension(460, 777)
            var view = JPanel(BorderLayout())
            view.add(loginBrowser.component)
            val loginWindow = WindowWrapperBuilder(WindowWrapper.Mode.FRAME, view)
                .setProject(project)
                .setTitle("DataStax Astra Login")
                .setOnCloseHandler {
                    //TODO: Dispose of stuff here when the close button is clicked
                    loginState = BrowserState.CANCELED
                    true
                }
                .build().apply {
                    show()
                    //Change size after or it won't apply since show() overrides those settings
                    window.setLocation(getLocation(window.location, window.size, loginSize))
                    window.setSize(loginSize)
                }

            loginState = browserResponse()

            when (loginState) {
                //TODO: Handle this somewhere else since if can't necessarily be handled by
                BrowserState.CANCELED -> {

                }
                BrowserState.AWAITING_EMAIL -> {
                    println("Tried to inject alert")
                    loginBrowser.cefBrowser.executeJavaScript(
                        "delete window.alert; alert('Hello! I am an alert box!!');",
                        loginBrowser.cefBrowser.url,
                        0
                    )
                }
                BrowserState.LOGGED_IN -> {
                    loginWindow.close()
                    createConfirmWindow(project)
                    //println("Now moving to dialog")
                }
            }
        }
    }


    fun createConfirmWindow(project: Project) {
        val view: JPanel = JPanel(BorderLayout())
        //val desiredSize = Dimension(460, 777)
        val confirmButton = JButton("Agree and Generate Token")
        lateinit var confirmWindow: WindowWrapper
        confirmButton.addActionListener { actionEvent: ActionEvent? ->
            launch {
                confirmTokenGen(project, confirmWindow)
            }
        }
        val textPanel = panel {
            row("A token will be created under your datastax account and inserted into the ~/home/.astra/config file. If this file doesn't exist it will be created. Click confirm below to generate a token and save it to the Astra profile file."){}
        }
        view.add(textPanel,BorderLayout.CENTER)
        view.add(confirmButton,BorderLayout.SOUTH)

        confirmWindow = WindowWrapperBuilder(WindowWrapper.Mode.FRAME, view)
            .setProject(project)
            .setTitle("Confirm Token Creation")
            .setOnCloseHandler {
                //TODO: Dispose of stuff here when the close button is clicked
                loginState = BrowserState.CANCELED
                true
            }
            .build().apply {
                show()
                //Change size after or it won't apply since show() overrides those settings
                window.setLocation(getLocation(window.location, window.size, Dimension(400, 300)))
                window.setSize(Dimension(400, 300))
            }

    }

    suspend fun confirmTokenGen(project: Project, windowWrapper: WindowWrapper) {
        println("Go ahead and make that token")
        windowWrapper.close()

        val dStaxCookie = loginBrowser.jbCefCookieManager.getCookies()?.find() {
            it.name == "dstaxprodauthz" }
        val rawBodyNoOrgId = this::class.java.getResource("/rawtext/GetTokenBody.txt").readText()
        val rawBodyString =
            rawBodyNoOrgId.substring(0, 64) + loginBrowser.cefBrowser.url.split("/")[3] + rawBodyNoOrgId.substring(64, rawBodyNoOrgId.lastIndex + 1)
        val rawBody = rawBodyString.toRequestBody("text/plain".toMediaTypeOrNull())
        var response =
                    CredentialsClient.internalOpsApi().getDatabaseAdminToken("${dStaxCookie?.name}=${dStaxCookie?.value}", rawBody)
        response.body()?.data?.generateToken?.token?.let {
            CreateOrUpdateProfilesFileAction().createWithGenToken(project,
                it)
        }
    }



    private fun getLocation(prevLoc: Point, prevSize: Dimension, newSize: Dimension): Point {
        val x = prevLoc.x + (prevSize.width - newSize.width) / 2
        val y = prevLoc.y + (prevSize.height - newSize.height) / 2
        return Point(x, y)
    }

    //TODO: Check if this gets disposed when window is closed
    suspend fun browserResponse(): BrowserState {
        var loginState = BrowserState.SIGN_IN
        //TODO: Add conditions for disposing this
        while (loginState == BrowserState.SIGN_IN) {
            delay(350L)
            if(verifyPageLoaded()){
                loginState = BrowserState.AWAITING_EMAIL
            }
            else if(astraHomeLoaded()){
                loginState = BrowserState.LOGGED_IN
            }
        }
        return loginState
    }

    private fun verifyPageLoaded(): Boolean =
        loginBrowser.cefBrowser.url.contains("VERIFY_EMAIL") || loginBrowser.cefBrowser.url.contains("first-broker-login")

    private fun astraHomeLoaded(): Boolean {
        val url = loginBrowser.cefBrowser.url
        if(!url.contains("https://astra.datastax.com/")){
            return false
        }
        url.split("/")[3].split("-").forEachIndexed { index, element ->
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

    //TODO: Refactor to BrowserResponse with callback changes
    enum class BrowserState {
        SIGN_IN, CANCELED, LOGGED_IN, AWAITING_EMAIL,
    }
}