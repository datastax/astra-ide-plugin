package com.datastax.astra.jetbrains.credentials

import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.WindowWrapper
import com.intellij.openapi.ui.WindowWrapperBuilder
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Point
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JPanel

class ConfirmCreateTokenWindow(
    private val project: Project,
) {
    private val window: WindowWrapper
    val view: JPanel = JPanel(BorderLayout())
    private val desiredSize = Dimension(460, 777)

    val confirmButton = JButton("Agree and Generate Token")

    init {
        confirmButton.addActionListener { actionEvent: ActionEvent? ->

        }

        window = WindowWrapperBuilder(WindowWrapper.Mode.FRAME, view)
            .setProject(project)
            .setTitle("Confirm Token Creation")
            .build().apply {
                show()
                window.setLocation(getLocation(window.location, window.size))
                window.setSize(desiredSize)
            }
    }

    private fun triggerCallback(): Int {
        return DialogWrapper.OK_EXIT_CODE
    }

    private fun getLocation(prevLoc: Point, prevSize: Dimension): Point {
        val x = prevLoc.x + (prevSize.width - desiredSize.width) / 2
        val y = prevLoc.y + (prevSize.height - desiredSize.height) / 2
        return Point(x, y)
    }
}


