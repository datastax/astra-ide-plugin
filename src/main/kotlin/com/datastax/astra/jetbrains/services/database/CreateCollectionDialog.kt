package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.devops_v2.models.AvailableRegionCombination
import com.datastax.astra.devops_v2.models.DatabaseInfoCreate
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.explorer.*
import com.datastax.astra.jetbrains.telemetry.CrudEnum
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.layout.buttonGroup
import com.intellij.ui.layout.listCellRenderer
import com.intellij.ui.layout.panel
import com.intellij.ui.layout.selected
import com.intellij.util.ui.tree.TreeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.RequestBody.Companion.toRequestBody
import org.jetbrains.annotations.NotNull
import java.awt.Component
import javax.swing.JComponent
import javax.swing.tree.DefaultMutableTreeNode

class CreateCollectionDialog(
    private val keyspaceNode: KeyspaceNode,
    private val project: Project,
        parent: Component? = null
    ) : DialogWrapper(project, parent, false, IdeModalityType.PROJECT),
        CoroutineScope by ApplicationThreadPoolScope("Database") {

        init {

        }

        // Property values for create database params
        var name: String = ""

        // UI variables

        val view = panel {
            row("Collection Name:") {
                textField(::name).withValidationOnApply {
                    if (it.text.trim().isEmpty()) {
                        ValidationInfo(
                            "Collection name is required",
                            it
                        )
                    } else null
                }
            }
        }

        init {
            view.withPreferredWidth(300)
            title = "Create Document Collection to ${keyspaceNode.keyspace.name} keyspace"
            setOKButtonText("Create")
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
        }
    }
