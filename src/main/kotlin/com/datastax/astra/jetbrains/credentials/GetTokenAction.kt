package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.WindowWrapper
import com.intellij.openapi.ui.WindowWrapperBuilder
import com.intellij.ui.jcef.JBCefBrowser
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.awt.*
import java.awt.event.ActionEvent
import javax.swing.*
import javax.swing.BorderFactory.createEmptyBorder

// TODO: Add all strings to messageBundle
class GetTokenAction :
    DumbAwareAction("Get Token", null, null),
    CoroutineScope by ApplicationThreadPoolScope("Credentials") {
    val loginBrowser: JBCefBrowser = JBCefBrowser("https://astra.datastax.com/")
    lateinit var loginState: BrowserState

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getRequiredData(LangDataKeys.PROJECT)

        launch {
            val loginSize = Dimension(460, 777)
            var view = JPanel(BorderLayout())
            view.add(loginBrowser.component, BorderLayout.CENTER)
            val loginWindow = WindowWrapperBuilder(WindowWrapper.Mode.FRAME, view)
                .setProject(project)
                .setTitle("DataStax Astra Login")
                .setOnCloseHandler {
                    loginBrowser.jbCefCookieManager.deleteCookies(false)
                    // TODO: Dispose of stuff here when the close button is clicked
                    loginState = BrowserState.CANCELED
                    true
                }
                .build().apply {
                    show()
                    // Change size after or it won't apply since show() overrides those settings
                    window.location = getLocation(window.location, window.size, loginSize)
                    window.size = loginSize
                }

            loginState = browserResponse()

            when (loginState) {
                // TODO: Handle this somewhere else since/if it can't necessarily be handled by
                BrowserState.CANCELED -> {

                }
                BrowserState.AWAITING_EMAIL -> {
                    //If ok is pressed on this it recalls GetTokenAction
                    addRestartPanel(e, loginWindow, view)
                }
                BrowserState.LOGGED_IN -> {
                    loginWindow.close()
                    buildConfirmWindow(e, project)
                }
            }
        }
    }

    fun buildConfirmWindow(e: AnActionEvent, project: Project) {
        val view = JPanel(BorderLayout(6, 6))
        // val desiredSize = Dimension(350, 200)
        val confirmButton = JButton("Agree and Generate Token")
        val cancelButton = JButton("Disagree")
        lateinit var confirmWindow: WindowWrapper
        confirmButton.addActionListener { actionEvent: ActionEvent? ->
            launch {
                confirmTokenGen(e, project, confirmWindow)
            }
        }
        cancelButton.addActionListener { actionEvent: ActionEvent? ->
            cancelTokenGen(confirmWindow)
        }
        val buttonPanel = JPanel(GridLayout(1, 2, 6, 6))
        buttonPanel.border = createEmptyBorder(2, 6, 6, 6)
        buttonPanel.add(confirmButton, 0)
        buttonPanel.add(cancelButton, 1)
        val textArea =
            JTextArea("A Database Administrator token will be created under your Datastax account and inserted into the ~/home/.astra/config file. If this file doesn't exist it will be created.\n\nClick agree below to generate a token and save it to the Astra profile file.").apply {
                border = createEmptyBorder(10, 10, 6, 10)
                background = buttonPanel.background
                font = Font(font.fontName, Font.PLAIN, 14)
                rows = 8
                lineWrap = true
                wrapStyleWord = true
                isEditable = false
                revalidate()
            }
        view.add(textArea, BorderLayout.CENTER)
        view.add(buttonPanel, BorderLayout.SOUTH)

        val desiredSize = view.preferredSize

        confirmWindow = WindowWrapperBuilder(WindowWrapper.Mode.FRAME, view)
            .setProject(project)
            .setTitle("Confirm Token Creation")
            .setOnCloseHandler {
                // TODO: Dispose of stuff here when the close button is clicked
                loginState = BrowserState.CANCELED
                true
            }
            .build().apply {
                show()
                // Change size after or it won't apply since show() overrides those settings
                window.location = getLocation(window.location, window.size, desiredSize)
                window.size = desiredSize
            }
    }

    suspend fun confirmTokenGen(e: AnActionEvent, project: Project, windowWrapper: WindowWrapper) {
        windowWrapper.close()

        val dStaxCookie = loginBrowser.jbCefCookieManager.getCookies()?.find() {
            it.name == "dstaxprodauthz"
        }
        val rawBodyNoOrgId = this::class.java.getResource("/rawtext/GetTokenBody.txt").readText()
        val rawBodyString =
            rawBodyNoOrgId.substring(0, 64) + loginBrowser.cefBrowser.url.split("/")[3] + rawBodyNoOrgId.substring(64,
                rawBodyNoOrgId.lastIndex + 1)
        val rawBody = rawBodyString.toRequestBody("text/plain".toMediaTypeOrNull())
        var response =
            CredentialsClient.internalOpsApi()
                .getDatabaseAdminToken("${dStaxCookie?.name}=${dStaxCookie?.value}", rawBody)
        response.body()?.data?.generateToken?.token?.let {
            CreateOrUpdateProfilesFileAction().createWithGenToken(
                project,
                it
            )
        }
        //Once a token is made
        ReloadProfilesAction().actionPerformed(e)
    }

    fun addRestartPanel(e: AnActionEvent, loginWindow: WindowWrapper, oldView: JPanel) {
        val returnButton = JButton("Return to Login")
        val cancelLogin = JButton("Cancel")
        returnButton.addActionListener { actionEvent: ActionEvent? ->
            launch {
                loginWindow.close()
                GetTokenAction().actionPerformed(e)
            }
        }
        cancelLogin.addActionListener { actionEvent: ActionEvent? ->
            cancelTokenGen(loginWindow)
        }
        val buttonPanel = JPanel(GridLayout(1, 2, 6, 6))
        buttonPanel.border = createEmptyBorder(2, 6, 6, 6)
        buttonPanel.add(returnButton, 0)
        buttonPanel.add(cancelLogin, 1)
        val view = JPanel(BorderLayout(6, 6))
        val textArea = JTextArea("Need to verify your email?\n\nClick 'Return to Login' when ready to continue.").apply {
            border = createEmptyBorder(10, 10, 6, 10)
            background = buttonPanel.background
            font = Font(font.fontName, Font.PLAIN, 14)
            rows = 3
            lineWrap = true
            wrapStyleWord = true
            isEditable = false
            revalidate()
        }
        view.add(textArea, BorderLayout.CENTER)
        view.add(buttonPanel, BorderLayout.SOUTH)
        oldView.add(view, BorderLayout.NORTH)
        oldView.revalidate()

    }

    fun cancelTokenGen(windowWrapper: WindowWrapper) {
        windowWrapper.close()
        loginBrowser.jbCefCookieManager.deleteCookies(false)
        // dispose of some stuff
    }

    private fun getLocation(prevLoc: Point, prevSize: Dimension, newSize: Dimension): Point {
        val x = prevLoc.x + (prevSize.width - newSize.width) / 2
        val y = prevLoc.y + (prevSize.height - newSize.height) / 2
        return Point(x, y)
    }

    // TODO: Check if this gets disposed when window is closed
    suspend fun browserResponse(): BrowserState {
        var loginState = BrowserState.SIGN_IN
        // TODO: Add conditions for disposing this
        while (loginState == BrowserState.SIGN_IN) {
            delay(350L)
            if (verifyPageLoaded()) {
                loginState = BrowserState.AWAITING_EMAIL
            } else if (astraHomeLoaded()) {
                loginState = BrowserState.LOGGED_IN
            }
        }
        return loginState
    }

    private fun verifyPageLoaded(): Boolean =
        loginBrowser.cefBrowser.url.contains("VERIFY_EMAIL") || loginBrowser.cefBrowser.url.contains("first-broker-login")

    private fun astraHomeLoaded(): Boolean {
        val url = loginBrowser.cefBrowser.url
        if (!url.contains("https://astra.datastax.com/")) {
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

    // TODO: Refactor to BrowserResponse with callback changes
    enum class BrowserState {
        SIGN_IN, CANCELED, LOGGED_IN, AWAITING_EMAIL,
    }
}
