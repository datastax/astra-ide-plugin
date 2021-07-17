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

// Build a simple window and attempt to center it based on the default location and size
fun buildBrowser(
    project: Project,
    title: String,
    mainPanel: JPanel,
    size: Dimension,
    browser: JBCefBrowser
): WindowWrapper {
    mainPanel.add(browser.component, BorderLayout.CENTER)
    val windowWrapper = WindowWrapperBuilder(WindowWrapper.Mode.FRAME, mainPanel)
        .setProject(project)
        .setTitle(title)
        .setOnCloseHandler {
            browser.jbCefCookieManager.deleteCookies(false)
            true
        }
        .build()
    windowWrapper.show()


    // Setting the size after the location seemed to move the window sometimes
    val newPoint = getLocation(windowWrapper.window.location, windowWrapper.window.size, size)
    windowWrapper.window.size = size
    windowWrapper.window.location = newPoint

    return windowWrapper
}

fun buildWindow(project: Project, title: String, mainPanel: JPanel, size: Dimension): WindowWrapper {
    val windowWrapper = WindowWrapperBuilder(WindowWrapper.Mode.FRAME, mainPanel)
        .setProject(project)
        .setTitle(title)
        .build()
    windowWrapper.show()

    // Setting the size after the location seemed to move the window sometimes
    val newPoint = getLocation(windowWrapper.window.location, windowWrapper.window.size, size)
    windowWrapper.window.size = size
    windowWrapper.window.location = newPoint

    return windowWrapper
}

// TODO: Evaluate view size based on length of body text
fun buildOkPanel(okButton: JButton, cancelButton: JButton, body: String, bodyRows: Int): JPanel {
    val buttonPanel = JPanel(GridLayout(1, 2, 6, 6))
    buttonPanel.border = BorderFactory.createEmptyBorder(2, 6, 6, 6)
    buttonPanel.add(okButton, 0)
    buttonPanel.add(cancelButton, 1)
    val jpanel = JPanel(BorderLayout(6, 6))
    val textArea =
        JTextArea(body).apply {
            border = BorderFactory.createEmptyBorder(10, 10, 6, 10)
            background = buttonPanel.background
            font = Font(font.fontName, Font.PLAIN, 14)
            rows = bodyRows
            lineWrap = true
            wrapStyleWord = true
            isEditable = false
            revalidate()
        }
    jpanel.add(textArea, BorderLayout.CENTER)
    jpanel.add(buttonPanel, BorderLayout.SOUTH)

    return jpanel
}

fun buildTextPanel(body: String, chRows: Int, fontSize: Int): JPanel {
    val jpanel = JPanel(BorderLayout(6, 6))
    val textArea =
        JTextArea(body).apply {
            border = BorderFactory.createEmptyBorder(10, 10, 6, 10)
            font = Font(font.fontName, Font.PLAIN, fontSize)
            rows = chRows
            lineWrap = true
            wrapStyleWord = true
            isEditable = false
            revalidate()
        }
    jpanel.add(textArea, BorderLayout.CENTER)
    return jpanel
}

fun getLocation(prevLoc: Point, prevSize: Dimension, newSize: Dimension): Point {
    val x = prevLoc.x + ((prevSize.width - newSize.width) / 2)
    val y = prevLoc.y + ((prevSize.height - newSize.height) / 2)
    return Point(x, y)
}
