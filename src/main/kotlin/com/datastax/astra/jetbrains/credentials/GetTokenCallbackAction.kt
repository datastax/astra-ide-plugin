package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.ui.jcef.JBCefBrowser
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.cef.handler.CefResourceRequestHandlerAdapter
import java.awt.*
import java.awt.event.ActionEvent
import javax.swing.*
import javax.swing.BorderFactory.createEmptyBorder
import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.explorer.DatabaseNode
import com.datastax.astra.jetbrains.explorer.ExplorerDataKeys.SELECTED_NODES
import com.datastax.astra.jetbrains.explorer.isProcessing
import com.datastax.astra.jetbrains.utils.EasyWindow
import com.intellij.openapi.actionSystem.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefCookieAccessFilter
import org.cef.handler.CefRequestHandlerAdapter
import org.cef.handler.CefResourceRequestHandler

import org.cef.misc.BoolRef
import org.cef.network.CefCookie
import org.cef.network.CefRequest
import org.cef.network.CefResponse
import org.cef.network.CefURLRequest
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
var returned = false

// TODO: Add all strings to messageBundle
class GetTokenCallbackAction :
    DumbAwareAction("Get Token New", null, null),
    CoroutineScope by ApplicationThreadPoolScope("Credentials") {
    val loginBrowser: JBCefBrowser = JBCefBrowser("https://astra.datastax.com/")
    val loginSize = Dimension(460, 777)

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getRequiredData(LangDataKeys.PROJECT)
        launch {
            var view = JPanel(BorderLayout())
            view.add(loginBrowser.component, BorderLayout.CENTER)
            val loginWindow = EasyWindow().build(project,view,loginSize, ::windowClosed)

            val response = getUserLoginResponse(loginBrowser)
            when (response.userLoginResult) {
                UserLoginResult.SUCCESS -> {
                    //Launch ok dialog
                    //if (ok) {

                }
                UserLoginResult.AWAITING_VERIFICATION -> {
                    println("Awaiting Verification")
                }
            }
        }



    }

    fun windowClosed(){
        loginBrowser.jbCefCookieManager.deleteCookies(false)
        // TODO: Dispose of stuff here when the close button is clicked
    }


    class MyCefResourceRequestHandler(val cont: Continuation<UserLoginResponse>) : CefResourceRequestHandlerAdapter() {
        override fun onResourceLoadComplete(
            browser: CefBrowser?,
            frame: CefFrame?,
            request: CefRequest?,
            response: CefResponse?,
            status: CefURLRequest.Status?,
            receivedContentLength: Long
        ) {
            if(browser!!.url.contains("VERIFY_EMAIL") && !returned){
                returned = true
                cont.resume(UserLoginResponse(UserLoginResult.AWAITING_VERIFICATION, null))
            }
            //Check for verified or verified oauth

        }
        override fun getCookieAccessFilter(
            browser: CefBrowser?,
            frame: CefFrame?,
            request: CefRequest?
        ): CefCookieAccessFilter {
            return object : CefCookieAccessFilter {
                override fun canSendCookie(p0: CefBrowser?, p1: CefFrame?, p2: CefRequest?, p3: CefCookie?): Boolean {
                    return true
                }

                override fun canSaveCookie(
                    p0: CefBrowser?,
                    p1: CefFrame?,
                    p2: CefRequest?,
                    p3: CefResponse?,
                    p4: CefCookie?
                ): Boolean {
                    //Check for dstaxauth cookie
                    //cont.resume(UserLoginResponse(UserLoginResult.SUCCESS, "mycookie"))
                    return true
                }
            }
        }
    }

    class MyCefRequestHandlerAdapter(val cont: Continuation<UserLoginResponse>) : CefRequestHandlerAdapter() {

        override fun getResourceRequestHandler(
            browser: CefBrowser?,
            frame: CefFrame?,
            request: CefRequest?,
            isNavigation: Boolean,
            isDownload: Boolean,
            requestInitiator: String?,
            disableDefaultHandling: BoolRef?
        ): CefResourceRequestHandler {
            return MyCefResourceRequestHandler(cont)
        }
    }
    suspend fun getUserLoginResponse(browser: JBCefBrowser) : UserLoginResponse {
        return suspendCoroutine<UserLoginResponse> { cont ->
            browser.jbCefClient.addRequestHandler(MyCefRequestHandlerAdapter(cont), browser.cefBrowser)
        }
    }


    enum class UserLoginResult {
        CANCELED, SUCCESS, AWAITING_VERIFICATION
    }
    data class UserLoginResponse(val userLoginResult: UserLoginResult, val cookie: String?) {

    }
}