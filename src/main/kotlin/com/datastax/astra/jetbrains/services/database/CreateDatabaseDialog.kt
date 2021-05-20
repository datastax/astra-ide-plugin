package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.models.AvailableRegionCombination
import com.datastax.astra.devops_v2.models.Costs
import com.datastax.astra.devops_v2.models.DatabaseInfoCreate
import com.datastax.astra.devops_v2.models.RegionCombination
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.explorer.DatabaseParentNode
import com.datastax.astra.jetbrains.explorer.ExplorerToolWindow
import com.datastax.astra.jetbrains.explorer.refreshTree
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.codeInsight.daemon.impl.createActionLabel
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.layout.cellPanel
import com.intellij.ui.layout.panel
import com.intellij.ui.layout.selected
import com.intellij.util.ui.tree.TreeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.annotations.NotNull
import java.awt.Component
import javax.swing.ButtonGroup
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JRadioButton
import javax.swing.tree.DefaultMutableTreeNode

class CreateDatabaseDialog(
    private val project: Project,
    parent: Component? = null
) : DialogWrapper(project, parent, false, IdeModalityType.PROJECT),
    CoroutineScope by ApplicationThreadPoolScope("Database") {
    var allRegions: List<AvailableRegionCombination> = emptyList()
    init{
        CreateDatabaseGetRegions(project)
        runBlocking { allRegions = CreateDatabaseGetRegions.getRegions() }
    }
    //TODO:Add all regions for all providers to same group but different panels. Avoid setting wrong region from another page

    var name: String = ""
    var keyspace: String = ""

    var cloudProvider = DatabaseInfoCreate.CloudProvider.AWS

    //TODO: Show/change the tier when the selection is made/changed
    var tier = DatabaseInfoCreate.Tier.SERVERLESS
    var region = "us-west-2"

    val awsButton = JRadioButton("AWS")
    val gcpButton = JRadioButton("GCP")
    val azureButton = JRadioButton("Azure")
    var providerGroup = ButtonGroup()


    var regionsList: MutableList<RegionCombination> = mutableListOf()

    //TODO: Generate these ComboBoxes based on the providers listed in allRegions
    var awsRegionsComboBox = ComboBox<DatabaseComboBoxItem>()
    var gcpRegionsComboBox = ComboBox<DatabaseComboBoxItem>()
    var azureRegionsComboBox = ComboBox<DatabaseComboBoxItem>()


    val view = panel {
        providerGroup.add(awsButton)
        providerGroup.add(gcpButton)
        providerGroup.add(azureButton)
        allRegions.forEach {
            if(it.tier=="serverless")
            when(it.cloudProvider) {
                "AWS" -> awsRegionsComboBox.addItem(DatabaseComboBoxItem(it))
                "GCP" -> gcpRegionsComboBox.addItem(DatabaseComboBoxItem(it))
                "AZURE" -> azureRegionsComboBox.addItem(DatabaseComboBoxItem(it))
            }
        }

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
            awsButton()
            gcpButton()
            azureButton()
        }
        row("Tier:") {
            //Thought I needed to do this but I don't since they only support creating serverless from rest
            //This kind of works but only updates when provider changes
            //createActionLabel(awsRegionsComboBox.item.regionInfo.tier,awsRegionsComboBox.actionPerformed())
            /*label(awsRegionsComboBox.item.regionInfo.tier).visibleIf(awsButton.selected)
            label(gcpRegionsComboBox.item.regionInfo.tier).visibleIf(gcpButton.selected)
            label(azureRegionsComboBox.item.regionInfo.tier).visibleIf(azureButton.selected)*/
            label(tier.value)
        }
        row("Regions:") {
            awsRegionsComboBox().visibleIf(awsButton.selected)
            gcpRegionsComboBox().visibleIf(gcpButton.selected)
            azureRegionsComboBox().visibleIf(azureButton.selected)
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
            //This is where we designate where to make a DB based on the selection
            when(providerGroup.selection){
                awsButton -> {
                    cloudProvider=DatabaseInfoCreate.CloudProvider.AWS
                    region = awsRegionsComboBox.item.regionInfo.region
                }
                gcpButton -> {
                    cloudProvider=DatabaseInfoCreate.CloudProvider.GCP
                    region = gcpRegionsComboBox.item.regionInfo.region
                }
                azureButton -> {
                    //Can't do this
                    //TODO: Fail gracefully
                }
            }

            //TODO: Wouldn't it be nice if this structure had a mapper directly to the View values?
            val databaseInfoCreate = DatabaseInfoCreate(name, keyspace, cloudProvider, tier, 1, region)
            val response = AstraClient.dbOperationsApi().createDatabase(databaseInfoCreate)
            if (response.isSuccessful) {
                val databaseParent =
                    TreeUtil.findNode(ExplorerToolWindow.getInstance(project).tree.model.root as @NotNull DefaultMutableTreeNode) {
                        it.userObject is DatabaseParentNode
                    }?.userObject as DatabaseParentNode
                databaseParent.clearCache()
                project.refreshTree(databaseParent, true)
                //val databaseId = response.headers()["Location"]
            } else {
                TODO("notifyError")
            }
        }
    }
}
