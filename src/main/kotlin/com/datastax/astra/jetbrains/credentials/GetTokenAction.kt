package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.explorer.ExplorerToolWindow
import com.datastax.astra.jetbrains.telemetry.ClickTarget
import com.datastax.astra.jetbrains.telemetry.TelemetryService
import com.datastax.astra.jetbrains.utils.*
import com.datastax.astra.jetbrains.utils.internal_apis.models.GenerateTokenRequest
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.WindowWrapper
import com.intellij.ui.jcef.JBCefBrowser
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
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

class GetTokenAction :
    DumbAwareAction(message("credentials.get_token.text"), null, null),
    CoroutineScope by ApplicationThreadPoolScope("Credentials") {

    val edtContext = getCoroutineUiContext()

    override fun actionPerformed(e: AnActionEvent) {
        e.getRequiredData(LangDataKeys.PROJECT).service<TelemetryService>().trackClick(ClickTarget.LINK, message("telemetry.get_token.start"))
        val loginBrowser = JBCefBrowser(message("credentials.login.link"))
        val loginChannel = Channel<UserLoginResponse>()
        loginBrowser.jbCefClient.addRequestHandler(MyCefRequestHandlerAdapter(loginChannel), loginBrowser.cefBrowser)
        val loginSize = Dimension(460, 777)
        val view = JPanel(BorderLayout())
        val window = buildBrowser(
            e.getRequiredData(LangDataKeys.PROJECT),
            message("credentials.get_token.browser.title"),
            view,
            loginSize,
            loginBrowser
        )

        launch {
            val response = loginChannel.receive()
            withContext(edtContext) {
                when (response.userLoginResult) {
                    UserLoginResult.SUCCESS -> {
                        window.close()
                        buildConfirmWindow(e, response)
                        e.getRequiredData(LangDataKeys.PROJECT).service<TelemetryService>().trackAction(message("telemetry.get_token.login.success"))
                    }
                    UserLoginResult.AWAITING_VERIFICATION -> {
                        addRestartPanel(e, view, window)
                        e.getRequiredData(LangDataKeys.PROJECT).service<TelemetryService>().trackAction(message("telemetry.get_token.login.verify"))
                    }
                }
            }
        }
    }

    fun buildConfirmWindow(e: AnActionEvent, response: UserLoginResponse) {
        val confirmButton = JButton(message("credentials.get_token.confirm.okay"))
        val cancelButton = JButton(message("credentials.get_token.confirm.cancel"))
        val view = buildOkPanel(
            confirmButton,
            cancelButton,
            message("credentials.get_token.confirm.body"),
            9,
        )

        val window = buildWindow(
            e.getRequiredData(LangDataKeys.PROJECT),
            message("credentials.get_token.confirm.title"),
            view,
            view.preferredSize
        )
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
        val response = CredentialsClient.internalOpsApi().getDatabaseAdminToken(
            loginResponse.cookie!!,
            GenerateTokenRequest(loginResponse.orgId!!).graphqlBlob
        )
        if (response.isSuccessful && response.body()?.data != null) {
            e.getRequiredData(LangDataKeys.PROJECT).service<TelemetryService>().trackAction(message("telemetry.get_token.success"))

            response.body()?.data?.generateToken?.token?.let {
                CreateOrUpdateProfilesFileAction().createWithGenToken(
                    e.getRequiredData(LangDataKeys.PROJECT),
                    it
                )
            }
            ExplorerToolWindow.getInstance(e.project!!).showWaitPanel()
            // TODO: Instead of waiting here use the "still loading resource" animation then reload when reachable
            runBlocking {
                // Once a token is made wait for server's to authorize it then refresh list
                delay(6500)
                ActionUtil.performActionDumbAware(ReloadProfilesAction(), e)
            }
        } else {
            generateTokenFailure()
            e.getRequiredData(LangDataKeys.PROJECT).service<TelemetryService>().trackAction(message("telemetry.get_token.failed"), mapOf("httpError" to response.getErrorResponse<Any?>().toString(), "httpResponse" to response.toString()))
        }
    }

    fun addRestartPanel(e: AnActionEvent, oldView: JPanel, window: WindowWrapper) {
        val returnButton = JButton(message("credentials.get_token.restart.okay"))
        val cancelLoginButton = JButton(message("credentials.get_token.restart.cancel"))
        returnButton.addActionListener { actionEvent: ActionEvent? ->
            e.getRequiredData(LangDataKeys.PROJECT).service<TelemetryService>().trackClick(ClickTarget.BUTTON, message("telemetry.get_token.login.restart"))
            window.close()
            ActionUtil.performActionDumbAware(GetTokenAction(), e)
        }
        cancelLoginButton.addActionListener { actionEvent: ActionEvent? ->
            window.close()
        }
        val returnPanel = buildOkPanel(
            returnButton,
            cancelLoginButton,
            message("credentials.get_token.restart.body"),
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

enum class UserLoginResult {
    SUCCESS, AWAITING_VERIFICATION
}

data class UserLoginResponse(val userLoginResult: UserLoginResult, val cookie: String?, val orgId: String?)
