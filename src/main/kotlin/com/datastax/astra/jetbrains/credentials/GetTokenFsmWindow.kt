package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.WindowWrapper
import com.intellij.openapi.ui.WindowWrapperBuilder
import com.intellij.openapi.util.BooleanGetter
import com.intellij.ui.jcef.JBCefAppRequiredArgumentsProvider
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Point
import java.lang.Character.isLetterOrDigit
import javax.swing.JPanel

class GetTokenFSM(
    private val project: Project,
    ): CoroutineScope by ApplicationThreadPoolScope("Credentials") {

    private val fsmWindow: WindowWrapper
    var disposed = false
    lateinit var page: Page
    val view: JPanel = JPanel(BorderLayout())
    private val fsmBrowser: JBCefBrowser = JBCefBrowser("https://astra.datastax.com/")
    private val desiredSize = Dimension(460, 777)
    val url: String
        get() = fsmBrowser.cefBrowser.url

    init {
        page = Page.ASTRA_LOGIN
        view.add(fsmBrowser.component)
        fsmWindow = WindowWrapperBuilder(WindowWrapper.Mode.FRAME, view)
            .setProject(project)
            .setTitle("DataStax Astra Login")
            .setOnCloseHandler {
                //TODO: Dispose of stuff here when the close button is clicked
                println("closed")
                disposed=true
                true
            }
            .build().apply {
                show()
                window.setLocation(getLocation(window.location, window.size))
                window.setSize(desiredSize)
            }
        launch {
            stateMachineLoop()
        }
    }

    //Uses the system generated size and location to move the new window to the middle of the user's view
    private fun getLocation(prevLoc: Point, prevSize: Dimension): Point {
        val x = prevLoc.x + (prevSize.width - desiredSize.width) / 2
        val y = prevLoc.y + (prevSize.height - desiredSize.height) / 2
        return Point(x, y)
    }

    private suspend fun stateMachineLoop() {
        //TODO: Add conditions for disposing this
        while (page != Page.ASTRA_HOME && !disposed) {
            delay(350L)
            checkUrlAndSetState()
        }
        // Don't do this stuff if the loop ends because the window was disposed
        if(page == Page.ASTRA_HOME){

            fsmWindow.close()
            buildTokenDialog()

            //TODO:
            // Get needed variables
            // Pass to dialog
            // Dispose of everything
        }
    }

    private fun checkUrlAndSetState(){
        when(page){
            Page.ASTRA_LOGIN -> {
                if(verfiyPageLoaded()){
                    page = Page.VERIFY_EMAIL
                    //TODO: Add return to login button
                }
                else if(astraHomeLoaded()){
                    page = Page.ASTRA_HOME
                }
            }
            Page.VERIFY_EMAIL -> {
                if(!verfiyPageLoaded()){
                    page = Page.ASTRA_LOGIN
                    //TODO: Remove the return to login button
                }
                else if(astraHomeLoaded()){
                    page = Page.ASTRA_HOME
                }
            }
        }
    }

    private fun verfiyPageLoaded(): Boolean{
        val verfiyPageKeys = Page.VERIFY_EMAIL.urlKey.split(",")
        if(url.contains(verfiyPageKeys[0]) || url.contains(verfiyPageKeys[1])){
            return true
        }
        return false
    }

    private fun astraHomeLoaded(): Boolean {
        if(!url.contains(Page.ASTRA_HOME.urlKey)){
            return false
        }
        fsmBrowser.cefBrowser.url.split("/")[3].split("-").forEachIndexed { index, element ->
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

    fun buildTokenDialog(){
        fsmBrowser.jbCefCookieManager.getCookies()?.forEach {
            if (it.name == "dstaxprodauthz") {
                GetTokenDialog(project,fsmBrowser.cefBrowser.url.split("/")[3],"${it.name}=${it.value}")
            }
            else{
                //TODO: Handle situation where cookie doesn't exist for some reason
            }
        }

    }

    enum class Page(var urlKey: String) {
        ASTRA_LOGIN(urlKey = "https://auth.cloud.datastax.com"),
        VERIFY_EMAIL(urlKey = "VERIFY_EMAIL,first-broker-login"),
        ASTRA_HOME(urlKey = "https://astra.datastax.com/"),
    }
}

//Use JcefProvider extension point to change the user agent and disable a feature so datastax page will load
class JcefProvider : JBCefAppRequiredArgumentsProvider {
    override val options: List<String>
        get() = listOf<String>("--disable-features=OutOfBlinkCors", "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0")
}


