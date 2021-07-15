package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.EasyWindow
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.WindowWrapper
import com.intellij.ui.jcef.JBCefBrowser
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefCookieAccessFilter
import org.cef.handler.CefRequestHandlerAdapter
import org.cef.handler.CefResourceRequestHandler
import org.cef.handler.CefResourceRequestHandlerAdapter
import org.cef.misc.BoolRef
import org.cef.network.CefCookie
import org.cef.network.CefRequest
import org.cef.network.CefResponse
import org.cef.network.CefURLRequest
import java.awt.*
import java.awt.event.ActionEvent
import javax.swing.*
import javax.swing.BorderFactory.createEmptyBorder

// TODO: Add all strings to messageBundle
class GetTokenCallbackAction :
    DumbAwareAction("Get Token", null, null),
    CoroutineScope by ApplicationThreadPoolScope("Credentials") {

    override fun actionPerformed(e: AnActionEvent) {
        val loginBrowser = JBCefBrowser("https://astra.datastax.com/")
        val loginChannel = Channel<UserLoginResponse>()
        loginBrowser.jbCefClient.addRequestHandler(MyCefRequestHandlerAdapter(loginChannel), loginBrowser.cefBrowser)
        val loginSize = Dimension(460, 777)
        launch {
            var view = JPanel(BorderLayout())
            var window = EasyWindow.buildBrowser(e.getRequiredData(LangDataKeys.PROJECT), "DataStax Astra Login", view, loginSize, loginBrowser)

            var response = getUserLoginResponse(loginChannel)
            when (response.userLoginResult) {
                UserLoginResult.SUCCESS -> {
                    window.close()
                    buildConfirmWindow(e, response)
                }
                UserLoginResult.AWAITING_VERIFICATION -> {
                    addRestartPanel(e, view, window)
                }
                UserLoginResult.CANCELED -> {
                    // Not really a possible state
                    // Possibly call some timeout?
                }
            }
        }
    }

    fun buildConfirmWindow(e: AnActionEvent, response: UserLoginResponse) {
        val view = JPanel(BorderLayout(6, 6))
        var confirmButton = JButton("Agree and Generate Token")
        var cancelButton = JButton("Disagree")
        val buttonPanel = JPanel(GridLayout(1, 2, 6, 6))
        buttonPanel.border = createEmptyBorder(2, 6, 6, 6)
        buttonPanel.add(confirmButton, 0)
        buttonPanel.add(cancelButton, 1)
        val textArea =
            JTextArea("A Database Administrator token will be created under your Datastax account and inserted into the '~/home/.astra/config' file. If this file doesn't exist it will be created.\n\nClick agree below to generate a token and save it to the Astra profile file.").apply {
                border = createEmptyBorder(10, 10, 6, 10)
                background = buttonPanel.background
                font = Font(font.fontName, Font.PLAIN, 14)
                rows = 9
                lineWrap = true
                wrapStyleWord = true
                isEditable = false
                revalidate()
            }
        view.add(textArea, BorderLayout.CENTER)
        view.add(buttonPanel, BorderLayout.SOUTH)
        val desiredSize = view.preferredSize

        val window = EasyWindow.build(e.getRequiredData(LangDataKeys.PROJECT), "Confirm Token Creation", view, desiredSize,)
        confirmButton.addActionListener { actionEvent: ActionEvent? ->
            window.close()
            launch {
                generateToken(e, response)
            }
        }
        cancelButton.addActionListener { actionEvent: ActionEvent? ->
            window.close()
        }
    }

    suspend fun generateToken(e: AnActionEvent, loginResponse: UserLoginResponse) {
        val rawBodyNoOrgId = this::class.java.getResource("/rawtext/GetTokenBody.txt").readText()
        val rawBodyString = rawBodyNoOrgId.substring(0, 64) + loginResponse.orgId +
            rawBodyNoOrgId.substring(64, rawBodyNoOrgId.lastIndex + 1)
        var response = CredentialsClient.internalOpsApi().getDatabaseAdminToken(
            loginResponse.cookie!!,
            rawBodyString.toRequestBody("text/plain".toMediaTypeOrNull())
        )

        response.body()?.data?.generateToken?.token?.let {
            CreateOrUpdateProfilesFileAction().createWithGenToken(
                e.getRequiredData(LangDataKeys.PROJECT),
                it
            )
        }
        // Once a token is made
        ReloadProfilesAction().actionPerformed(e)
    }

    fun addRestartPanel(e: AnActionEvent, oldView: JPanel, window: WindowWrapper) {
        val returnButton = JButton("Return to Login")
        val cancelLoginButton = JButton("Cancel")
        returnButton.addActionListener { actionEvent: ActionEvent? ->
            launch {
                window.close()
                GetTokenCallbackAction().actionPerformed(e)
            }
        }
        cancelLoginButton.addActionListener { actionEvent: ActionEvent? ->
            window.close()
        }
        val returnPanel = EasyWindow.buildOkPanel(
            returnButton,
            cancelLoginButton,
            "Need to verify your email?\n\nClick 'Return to Login' when ready to continue.",
            3,
        )
        oldView.add(returnPanel, BorderLayout.NORTH)
        oldView.revalidate()
    }
}
class MyCefResourceRequestHandler(val channel: Channel<UserLoginResponse>) : CefResourceRequestHandlerAdapter() {
    override fun onResourceLoadComplete(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?,
        response: CefResponse?,
        status: CefURLRequest.Status?,
        receivedContentLength: Long
    ) {
        // If verify screen appears from native registration or OAUTH registration set return result AWAITING_VERIFICATION
        if (!channel.isClosedForSend && (browser!!.url.contains("VERIFY_EMAIL") || browser.url.contains("first-broker-login"))) {
            runBlocking {
                channel.send(UserLoginResponse(UserLoginResult.AWAITING_VERIFICATION, null, null))
                channel.close()
            }
        }
    }

    override fun getCookieAccessFilter(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?
    ): CefCookieAccessFilter {
        return object : CefCookieAccessFilter {
            override fun canSendCookie(p0: CefBrowser?, p1: CefFrame?, p2: CefRequest?, p3: CefCookie?): Boolean {
                // If cookie named this is able to be sent check the url
                if (!channel.isClosedForSend && p3?.name == "dstaxprodauthz") {
                    // If the url contains an orgId preform a callback and pass the data needed to generate a token
                    if (browser!!.url.contains(Regex("""(https://astra.datastax.com/)([0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12})"""))) {
                        runBlocking {
                            channel.send(
                                UserLoginResponse(
                                    UserLoginResult.SUCCESS,
                                    "${p3.name}=${p3.value}",
                                    p0?.url?.split("/")!![3]
                                )
                            )
                            channel.close()
                        }
                    }
                }
                return true
            }
            override fun canSaveCookie(
                p0: CefBrowser?,
                p1: CefFrame?,
                p2: CefRequest?,
                p3: CefResponse?,
                p4: CefCookie?
            ): Boolean {
                return true
            }
        }
    }
}

class MyCefRequestHandlerAdapter(val channel: Channel<UserLoginResponse>) : CefRequestHandlerAdapter() {
    override fun getResourceRequestHandler(
        browser: CefBrowser?,
        frame: CefFrame?,
        request: CefRequest?,
        isNavigation: Boolean,
        isDownload: Boolean,
        requestInitiator: String?,
        disableDefaultHandling: BoolRef?
    ): CefResourceRequestHandler {
        return MyCefResourceRequestHandler(channel)
    }
}

suspend fun getUserLoginResponse(loginChannel: Channel<UserLoginResponse>): UserLoginResponse {
    return loginChannel.receive()
}

fun CoroutineScope.addHandler(browser: JBCefBrowser, loginChannel: Channel<UserLoginResponse>) {
    launch {

    }
}

enum class UserLoginResult {
    CANCELED, SUCCESS, AWAITING_VERIFICATION
}

data class UserLoginResponse(val userLoginResult: UserLoginResult, val cookie: String?, val orgId: String?)
