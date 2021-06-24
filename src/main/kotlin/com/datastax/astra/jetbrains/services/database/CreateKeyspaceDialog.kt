package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.explorer.DatabaseNode
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.layout.panel
import kotlinx.coroutines.CoroutineScope
import java.awt.Component
import javax.swing.JComponent

class CreateKeyspaceDialog(
    private val databaseNode: DatabaseNode,
    private val project: Project,
    parent: Component? = null
) : DialogWrapper(project, parent, false, IdeModalityType.PROJECT),
    CoroutineScope by ApplicationThreadPoolScope("Database") {

    var keyspace: String = ""

    val view = panel {
        row("Keyspace: ") {
            textField(::keyspace).withValidationOnApply {
                if (it.text.trim().isEmpty()) ValidationInfo(
                    message("database.create.database.missing.database.keyspace"),
                    it
                ) else null
            }
        }
    }

    init {
        title = "Add Keyspace to ${databaseNode.database.info.name}"
        setOKButtonText("Add")
        init()
    }

    override fun createCenterPanel(): JComponent = view

    override fun doOKAction() {
        if (!okAction.isEnabled) {
            return
        }
        view.apply()
        close(OK_EXIT_CODE)
        /*
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

         */
    }
}
