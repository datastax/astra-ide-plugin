package com.datastax.astra.jetbrains.utils.editor.ui

import com.datastax.astra.jetbrains.services.database.CollectionVirtualFile
import com.datastax.astra.jetbrains.services.database.TableVirtualFile
import com.datastax.astra.jetbrains.utils.editor.PagedVirtualFile
import kotlin.reflect.KFunction0
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.layout.*
import kotlinx.coroutines.CoroutineScope
import java.awt.Component
import javax.swing.JComponent

class CustomPageSizeDialog(
    private val project: Project,
    private val file: PagedVirtualFile,
    parent: Component? = null,
) : DialogWrapper(project, parent, false, IdeModalityType.PROJECT),
    CoroutineScope by ApplicationThreadPoolScope("Database") {
    //Define UI components here so they can be accessed or modified after adding to view.
    var newPageSize: Int = 1
    val fieldLabel = when(file){
        is CollectionVirtualFile -> "Documents:"
        is TableVirtualFile -> "Rows:"
        else -> "Elements:"
    }

    val view = panel {
        row(fieldLabel) {
            intTextField(::newPageSize, 5).withValidationOnApply {
                if (it.text.trim().isEmpty() || it.text.toInt()< 1 || it.text.toInt()>1000) {
                    ValidationInfo(
                        "Please choose a number between 1 and 1000",
                        it
                    )
                } else null
            }
        }
    }

    init {
        title = "Set Custom Page Size"
        setOKButtonText("OK")
        init()
    }

    /**
     * Factory method. It creates panel with dialog options. Options panel is located at the
     * center of the dialog's content pane. The implementation can return `null`
     * value. In this case there will be no options panel.
     */
    override fun createCenterPanel(): JComponent = view

    override fun doOKAction() {
        if (!okAction.isEnabled) {
            return
        }
        view.apply()
        file.setVirtualPageSize(newPageSize)
        close(OK_EXIT_CODE)

    }
}
