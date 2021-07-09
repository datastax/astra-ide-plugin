package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.WindowWrapper
import com.intellij.openapi.ui.WindowWrapperBuilder
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.layout.panel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Point
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class GetTokenDialog(
    private val project: Project,
    private val orgId: String,
    private val dStaxCookie: String,
): CoroutineScope by ApplicationThreadPoolScope("Credentials") {

    private val gtWindow: WindowWrapper

    val gtView: JPanel = JPanel(BorderLayout())
    private val desiredSize = Dimension(450, 300)

    init {
        val confirmButton = JButton("Agree and Generate Token")
        confirmButton.addActionListener { actionEvent: ActionEvent? ->
            agreeAndGenerate()
        }
        val bottomPanel = panel {
            row("Confirm: ") {
                confirmButton()
            }
        }
        gtView.setPreferredSize(Dimension(450, 365))
        gtView.add(bottomPanel, BorderLayout.SOUTH)
        gtView.add(JBTextArea("We're gonna make a token and all that jazz? You jazzy?"),BorderLayout.CENTER)
        gtWindow = WindowWrapperBuilder(WindowWrapper.Mode.FRAME, gtView)
            .setProject(project)
            .setTitle("Confirm Token Creation")
            .setOnCloseHandler {
                //TODO: Dispose of stuff here when the close button is clicked
                println("closed")
                true
            }
            .build().apply {
                show()
                //window.setLocation(getLocation(window.location, window.size))
                //window.setSize(desiredSize)
            }
    }

    private fun getLocation(prevLoc: Point, prevSize: Dimension): Point {
        val x = prevLoc.x + (prevSize.width - desiredSize.width) / 2
        val y = prevLoc.y + (prevSize.height - desiredSize.height) / 2
        return Point(x, y)
    }

    fun agreeAndGenerate() {
        runBlocking {
            val rawBodyNoOrgId = this::class.java.getResource("/rawtext/GetTokenBody.txt").readText()
            val rawBodyString =
                rawBodyNoOrgId.substring(0, 64) + orgId + rawBodyNoOrgId.substring(64, rawBodyNoOrgId.lastIndex + 1)
            val rawBody = rawBodyString.toRequestBody("text/plain".toMediaTypeOrNull())
            var response = CredentialsClient.internalOpsApi().getDatabaseAdminToken(dStaxCookie, rawBody)
            println(response.body()!!.data.generateToken.token)
            //Create cred file if needed
            //Modify the credential file
        }
    }
}
