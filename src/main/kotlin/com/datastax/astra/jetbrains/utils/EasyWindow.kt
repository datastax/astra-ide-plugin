package com.datastax.astra.jetbrains.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.WindowWrapper
import com.intellij.openapi.ui.WindowWrapperBuilder
import com.intellij.ui.jcef.JBCefBrowser
import java.awt.*
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextArea

// TODO: This is really being used like a static class. Could change the type to reflect use
object EasyWindow {

    // Build a simple window and attempt to center it based on the default location and size
    fun buildBrowser(project: Project, title: String, mainPanel: JPanel, size: Dimension, browser: JBCefBrowser): WindowWrapper {
        mainPanel.add(browser.component, BorderLayout.CENTER)
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

    // TODO: Evaluate view size based on length of body text
    fun buildOkPanel(okButton: JButton, cancelButton: JButton, bodyText: String, bodyRows: Int): JPanel {
        val buttonPanel = JPanel(GridLayout(1, 2, 6, 6))
        buttonPanel.border = BorderFactory.createEmptyBorder(2, 6, 6, 6)
        buttonPanel.add(okButton, 0)
        buttonPanel.add(cancelButton, 1)
        val view = JPanel(BorderLayout(6, 6))
        val textArea =
            JTextArea(bodyText).apply {
                border = BorderFactory.createEmptyBorder(10, 10, 6, 10)
                background = buttonPanel.background
                font = Font(font.fontName, Font.PLAIN, 14)
                rows = bodyRows
                lineWrap = true
                wrapStyleWord = true
                isEditable = false
                revalidate()
            }
        view.add(textArea, BorderLayout.CENTER)
        view.add(buttonPanel, BorderLayout.SOUTH)

        return view
    }

    fun getLocation(prevLoc: Point, prevSize: Dimension, newSize: Dimension): Point {
        val x = prevLoc.x + ((prevSize.width - newSize.width) / 2)
        val y = prevLoc.y + ((prevSize.height - newSize.height) / 2)
        return Point(x, y)
    }
}
