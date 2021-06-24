package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.layout.panel
import kotlinx.coroutines.CoroutineScope
import javax.swing.JComponent

class DeleteDatabaseDialog(
    private val project: Project
) : DialogWrapper(project),
    CoroutineScope by ApplicationThreadPoolScope("Database") {

    val view = panel {
        row {
            label("Are you sure you want to delete the database?")
        }
    }

    init {
        init()
    }

    override fun createCenterPanel(): JComponent = view

    override fun doOKAction() {
        if (!okAction.isEnabled) {
            return
        }
        close(OK_EXIT_CODE)
    }
}
