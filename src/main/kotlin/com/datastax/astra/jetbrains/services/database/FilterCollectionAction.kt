package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.explorer.*
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.editor.ui.EndpointCollection
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.layout.*
import kotlinx.coroutines.CoroutineScope
import java.awt.Component
import javax.swing.JComponent

class FilterCollectionAction : DumbAwareAction("Filter Collection", null, null) {
    override fun actionPerformed(e: AnActionEvent) {
        e.getData(ExplorerDataKeys.SELECTED_NODES)?.map { it as? CollectionNode }?.singleOrNull()?.run {
            FilterCollectionDialog(this.nodeProject,EndpointCollection(this.database, this.keyspace.name, this.collection.name),false).show()
        }
    }
}

class FilterEditCollectionAction : DumbAwareAction("Filter and Edit Collection", null, null) {
    override fun actionPerformed(e: AnActionEvent) {
        e.getData(ExplorerDataKeys.SELECTED_NODES)?.map { it as? CollectionNode }?.singleOrNull()?.run {
            FilterCollectionDialog(this.nodeProject,EndpointCollection(this.database, this.keyspace.name, this.collection.name),true).show()
        }
    }
}

class FilterCollectionDialog(
    private val project: Project,
    private val endpoint: EndpointCollection,
    var editOnOpen: Boolean = false,
    parent: Component? = null
) : DialogWrapper(project, parent, false, IdeModalityType.PROJECT),
    CoroutineScope by ApplicationThreadPoolScope("Database") {

    var whereParameter: String = ""

    val view = panel {
        row("Where:") {
            textField(::whereParameter).withValidationOnApply {
                if (it.text.trim().isEmpty()) {
                    ValidationInfo(
                        message("database.create.database.missing.database.name"),
                        it
                    )
                } else null
            }
        }
        row("") {
            checkBox("Enter edit mode",::editOnOpen)
        }
    }

    init {
        title = if(editOnOpen){
            "Browse Filtered Collection and Edit"
        } else {
            "Browse Filtered Collection"
        }
        setOKButtonText("Browse ${endpoint.collection} with this filter")
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
        close(OK_EXIT_CODE)
        CollectionBrowserPanel(project,endpoint,editOnOpen,whereParameter)
    }
}
