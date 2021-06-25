package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.devops_v2.models.AvailableRegionCombination
import com.datastax.astra.devops_v2.models.DatabaseInfoCreate
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.explorer.DatabaseParentNode
import com.datastax.astra.jetbrains.explorer.ExplorerToolWindow
import com.datastax.astra.jetbrains.explorer.refreshTree
import com.datastax.astra.jetbrains.telemetry.CrudEnum
import com.datastax.astra.jetbrains.telemetry.TelemetryManager.trackDevOpsCrud
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.layout.*
import com.intellij.util.ui.tree.TreeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.annotations.NotNull
import java.awt.Component
import javax.swing.JComponent
import javax.swing.tree.DefaultMutableTreeNode

class CreateDatabaseDialog(
    private val project: Project,
    parent: Component? = null
) : DialogWrapper(project, parent, false, IdeModalityType.PROJECT),
    CoroutineScope by ApplicationThreadPoolScope("Database") {
    var allRegions: List<AvailableRegionCombination> = emptyList()

    init {
        CreateDatabaseGetRegions(project)
        runBlocking { allRegions = CreateDatabaseGetRegions.getRegions() }
    }

    // Property values for create database params
    var name: String = ""
    var keyspace: String = ""
    var cloudProvider = DatabaseInfoCreate.CloudProvider.AWS
    var tier = DatabaseInfoCreate.Tier.SERVERLESS
    val regionForProvider: MutableMap<String, AvailableRegionCombination> = mutableMapOf()

    // UI variables
    lateinit var providerButtons: Map<String, JBRadioButton>

    val view = panel {
        row("Database Name:") {
            textField(::name).withValidationOnApply {
                if (it.text.trim().isEmpty()) {
                    ValidationInfo(
                        message("database.create.database.missing.database.name"),
                        it
                    )
                } else null
            }
        }
        row("Keyspace: ") {
            textField(::keyspace).withValidationOnApply {
                if (it.text.trim().isEmpty()) {
                    ValidationInfo(
                        message("database.create.database.missing.database.keyspace"),
                        it
                    )
                } else null
            }
        }
        row("Cloud Provider:") {
            cell {
                buttonGroup(::cloudProvider) {
                    providerButtons = listOf("AWS", "GCP", "AZURE").associate {
                        it to radioButton(it, DatabaseInfoCreate.CloudProvider.AWS).component
                    }
                }
            }
        }
        row("Tier:") {
            label(tier.value)
        }
        row("Region:") {
            cell {
                allRegions
                    .filter { it.tier == "serverless" }
                    .filter { enumValues<DatabaseInfoCreate.CloudProvider>().any { enum -> enum.value == it.cloudProvider } }
                    .groupBy { it.cloudProvider }
                    .forEach { regionsByProvider ->
                        regionForProvider[regionsByProvider.key] = regionsByProvider.value.first()
                        comboBox(
                            CollectionComboBoxModel(regionsByProvider.value),
                            getter = { regionForProvider[regionsByProvider.key] },
                            setter = { it?.apply { regionForProvider[regionsByProvider.key] = it } },
                            renderer = listCellRenderer { value, _, _ -> text = value.region }
                        )
                            .visibleIf(providerButtons[regionsByProvider.key]!!.selected)
                        // .withLeftGap()//.component.setMinimumAndPreferredWidth(160)
                    }
            }
        }
    }

    init {
        title = "Create Astra Database"
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
        launch {
            // TODO: Wouldn't it be nice if this structure had a mapper directly to the View values?
            val databaseInfoCreate = DatabaseInfoCreate(name, keyspace, cloudProvider, tier, 1, regionForProvider[cloudProvider.value]?.region.orEmpty())
            val response = AstraClient.dbOperationsApi().createDatabase(databaseInfoCreate)
            if (response.isSuccessful) {
                val databaseParent =
                    TreeUtil.findNode(ExplorerToolWindow.getInstance(project).tree.model.root as @NotNull DefaultMutableTreeNode) {
                        it.userObject is DatabaseParentNode
                    }?.userObject as DatabaseParentNode
                databaseParent.clearCache()
                project.refreshTree(databaseParent, true)

                // This is disabled for now to reduce potential data leakage
                // val databaseId = response.headers()["Location"]
                trackDevOpsCrud("Database", name, CrudEnum.CREATE, true)
            } else {
                trackDevOpsCrud("Database", name, CrudEnum.CREATE, false, mapOf("httpError" to response.getErrorResponse<Any?>().toString(), "httpResponse" to response.toString()))
            }
        }
    }
}
