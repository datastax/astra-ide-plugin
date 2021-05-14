package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.models.DatabaseInfoCreate
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.layout.panel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.awt.Component
import javax.swing.JComponent

class CreateDatabaseDialog(
    private val project: Project,
    parent: Component? = null
) : DialogWrapper(project, parent, false, IdeModalityType.PROJECT),
    CoroutineScope by ApplicationThreadPoolScope("Database") {

    var name: String = ""
    var keyspace: String = ""

    val cloudProvider = DatabaseInfoCreate.CloudProvider.AWS
    val tier = DatabaseInfoCreate.Tier.SERVERLESS
    var region = "us-west-2"

    val view = panel {
        row("Database Name:") {
            textField(::name).withValidationOnApply {
                if (it.text.trim().isEmpty()) ValidationInfo(
                    message("database.create.database.missing.database.name"),
                    it
                ) else null
            }
        }
        row("Keyspace: ") {
            textField(::keyspace).withValidationOnApply {
                if (it.text.trim().isEmpty()) ValidationInfo(
                    message("database.create.database.missing.database.keyspace"),
                    it
                ) else null
            }
        }
        row("Cloud Provider:") {
            label(cloudProvider.value)
        }
        row("Tier:") {
            label(tier.value)
        }
        row("Region:") {
            label(region)
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
            //TODO: Wouldn't it be nice if this structure had a mapper directly to the View values?
            val databaseInfoCreate = DatabaseInfoCreate(name, keyspace, cloudProvider, tier, 1, region)
            val response = AstraClient.operationsApi().createDatabase(databaseInfoCreate)
            if (response.isSuccessful) {
                val databaseId = response.headers()["Location"]
                TODO("refreshExplorerTree")
                TODO("kick off polling for when database is created")
                TODO("update presentation of node in tree")
                val listresponse = AstraClient.operationsApi().listDatabases()
                val list = listresponse.body()
                //AstraClient.operationsApi()
            } else {
                TODO("notifyError")
            }
        }
    }

}
