package com.datastax.astra.jetbrains.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.WindowWrapper
import com.intellij.openapi.ui.WindowWrapperBuilder
import com.intellij.ui.jcef.JBCefBrowser
import java.awt.*
import javax.swing.JPanel

// TODO: This is really being used like a static class. Could change the type to reflect use
object EasyWindow {

    // Build a simple window and attempt to center it based on the default location and size
    fun buildBrowser(project: Project, title: String, mainPanel: JPanel, size: Dimension, browser: JBCefBrowser): WindowWrapper {
        val easyWindow = WindowWrapperBuilder(WindowWrapper.Mode.FRAME, mainPanel)
            .setProject(project)
            .setTitle(title)
            .setOnCloseHandler {
                browser.jbCefCookieManager.deleteCookies(true)
                true
            }
            .build()
        easyWindow.show()

        // Setting the size after the location seemed to move the window sometimes
        val newPoint = getLocation(easyWindow.window.location, easyWindow.window.size, size)
        easyWindow.window.size = size
        easyWindow.window.location = newPoint

        return easyWindow
    }

    fun build(project: Project, title: String, mainPanel: JPanel, size: Dimension,): WindowWrapper {
        val easyWindow = WindowWrapperBuilder(WindowWrapper.Mode.FRAME, mainPanel)
            .setProject(project)
            .setTitle(title)
            .build()
        easyWindow.show()

        // Setting the size after the location seemed to move the window sometimes
        val newPoint = getLocation(easyWindow.window.location, easyWindow.window.size, size)
        easyWindow.window.size = size
        easyWindow.window.location = newPoint

        return easyWindow
    }

    fun getLocation(prevLoc: Point, prevSize: Dimension, newSize: Dimension): Point {
        val x = prevLoc.x + ((prevSize.width - newSize.width) / 2)
        val y = prevLoc.y + ((prevSize.height - newSize.height) / 2)
        return Point(x, y)
    }
}
