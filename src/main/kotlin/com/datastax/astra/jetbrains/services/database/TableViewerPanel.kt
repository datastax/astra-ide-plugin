package com.datastax.astra.jetbrains.services.database

import com.intellij.ui.layout.panel
import javax.swing.JComponent

class TableViewerPanel {

    val component: JComponent

    init {
        component = panel {
            row {
                label("Table contents go here")
            }
        }
    }
}
