package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.explorer.CollectionNode
import com.datastax.astra.jetbrains.explorer.ExplorerDataKeys
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.editor.ui.EndpointCollection
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory.createSingleFileDescriptor
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.*
import com.jetbrains.rd.swing.mouseClicked
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Component
import java.awt.event.ActionEvent
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.text.JTextComponent

class UploadJsonCollectionAction : DumbAwareAction("Insert Documents", null, null) {
    override fun actionPerformed(e: AnActionEvent) {
        e.getData(ExplorerDataKeys.SELECTED_NODES)?.map { it as? CollectionNode }?.singleOrNull()?.run {
            UploadJsonCollectionDialog(this.nodeProject,
                EndpointCollection(this.database, this.keyspace.name, this.collection.name,),"",false).show()
        }
    }
}

class UploadJsonCreateCollectionAction : DumbAwareAction("Create Collection and Insert Documents", null, null) {
    override fun actionPerformed(e: AnActionEvent) {
        e.getData(ExplorerDataKeys.SELECTED_NODES)?.map { it as? CollectionNode }?.singleOrNull()?.run {
            val filepath = ""
            UploadJsonCollectionDialog(this.nodeProject,
                EndpointCollection(this.database, this.keyspace.name, this.collection.name),filepath,true).show()
        }
    }
}

class UploadJsonCollectionDialog(
    private val project: Project,
    val endpoint: EndpointCollection,
    var filePath: String = "",
    var createNewCollection: Boolean = false,
    var newCollectionName: String = "",
    parent: Component? = null,
) : DialogWrapper(project, parent, false, IdeModalityType.PROJECT),
    CoroutineScope by ApplicationThreadPoolScope("Database") {
    //Define UI components here so they can be accessed or modified after adding to view.
    lateinit var newNameField: CellBuilder<JBTextField>
    lateinit var filePathField: CellBuilder<JBTextField>
    lateinit var checkBox: CellBuilder<JBCheckBox>
    val browseFilesButton = JButton("Browse...")

    val view = panel {
        row {
            browseFilesButton()
            // TODO: Add withValidationOnApply so if a file is not a JSON it won't let you "apply"
            filePathField = textField(::filePath).withValidationOnApply {
                if (it.text.trim().isEmpty()) {
                    ValidationInfo(
                        "A valid JSON file is required",
                        it
                    )
                } else null
            }
        }
        row("") {
            checkBox = checkBox("Create new collection",::createNewCollection)
        }
        row("Name:") {
            newNameField = textField(::newCollectionName).withValidationOnApply {
                if (it.text.trim().isEmpty() && it.isEnabled) {
                    ValidationInfo(
                        "Collection name is required.",
                        it
                    )
                } else null
            }.enableIf(checkBox.selected)
        }
    }

    init {
            //Blank out the new collection name field if it gets disabled. Replace the contents of the text if re-enabled
            //Since the current text is applied, the empty text will overwrite the newNameField value
            newNameField.component.addPropertyChangeListener {
                if (it.propertyName == "enabled" && !(it.newValue as Boolean)) {
                    newCollectionName = newNameField.component.text
                    newNameField.component.text = ""
                }
                if (it.propertyName == "enabled" && it.newValue as Boolean) {
                    newNameField.component.text = newCollectionName
                }
            }
            browseFilesButton.addActionListener { actionEvent: ActionEvent? ->
                changeSelectedFile()
            }
            title = "Create Astra Database"
            setOKButtonText("Create")

            init()



        filePathField.component.isEditable=false
    }

    /**
     * Factory method. It creates panel with dialog options. Options panel is located at the
     * center of the dialog's content pane. The implementation can return `null`
     * value. In this case there will be no options panel.
     */
    override fun createCenterPanel(): JComponent {
        //First show the file chooser, then show our view.
        //Only allow selecting JSON files
        FileChooser.chooseFile(
            createSingleFileDescriptor("json").withTitle("Choose a JSON File"),
            project,
            null
        )?.path.orEmpty().apply {
            if(this.isNotEmpty()){
                filePath=this
            }
            else{

                filePathField.component.toolTipText = "Please select a JSON file"
                filePathField.component.createToolTip()
            }
        }
        filePathField.component.text = filePath.ifEmpty { "Please select a JSON file" }
        return view
    }

    override fun doOKAction() {
        if (!okAction.isEnabled) {
            return
        }
        view.apply()
        //println("Loading documents from $filePath to $newCollectionName")
        close(OK_EXIT_CODE)

    }

    fun changeSelectedFile() {
            view.apply()
            close(CANCEL_EXIT_CODE)
            UploadJsonCollectionDialog(project,endpoint,filePath,createNewCollection,newCollectionName,).show()
    }
}
