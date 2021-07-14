package com.datastax.astra.jetbrains.utils

import com.datastax.astra.jetbrains.credentials.GetTokenAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.WindowWrapper
import com.intellij.openapi.ui.WindowWrapperBuilder
import java.awt.Dimension
import java.awt.Point
import javax.swing.JPanel

class EasyWindow {


    fun build(project: Project, mainPanel: JPanel, size: Dimension, function: () -> (Unit)): WindowWrapper {
        val window = WindowWrapperBuilder(WindowWrapper.Mode.FRAME, mainPanel)
            .setProject(project)
            .setTitle("DataStax Astra Login")
            .setOnCloseHandler {
                function()
                true
            }
            .build().apply {
                show()
                // Change size after or it won't apply since show() overrides those settings
                window.location = getLocation(window.location, window.size, size)
                window.size = size
            }


        return window
    }

    fun getLocation(prevLoc: Point, prevSize: Dimension, newSize: Dimension): Point {
        val x = prevLoc.x + (prevSize.width - newSize.width) / 2
        val y = prevLoc.y + (prevSize.height - newSize.height) / 2
        return Point(x, y)
    }
}