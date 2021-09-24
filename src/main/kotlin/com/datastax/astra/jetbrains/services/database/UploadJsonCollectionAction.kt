package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.explorer.CollectionNode
import com.datastax.astra.jetbrains.explorer.ExplorerDataKeys
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.BatchDocWriter
import com.datastax.astra.jetbrains.utils.editor.ui.EndpointCollection
import com.datastax.astra.jetbrains.utils.editor.ui.successfulBatchUploadNotification
import com.datastax.astra.stargate_document_v2.infrastructure.Serializer
import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.stream.JsonReader
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory.createSingleFileDescriptor
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.*
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.awt.Component
import java.io.*
import java.lang.Thread.sleep
import java.util.*
import javax.swing.JComponent


class UploadJsonCollectionAction : DumbAwareAction("Upload Documents", null, null) {
    override fun actionPerformed(e: AnActionEvent) {
        e.getData(ExplorerDataKeys.SELECTED_NODES)?.map { it as? CollectionNode }?.singleOrNull()?.run {
            UploadJsonCollectionDialog(this.nodeProject,
                EndpointCollection(this.database, this.keyspace.name, this.collection.name), "", false).show()
        }
    }
}

class UploadJsonCollectionDialog(
    private val project: Project,
    val endpoint: EndpointCollection,
    var filePath: String = "",
    var createNewCollection: Boolean = false,
    parent: Component? = null,
) : DialogWrapper(project, parent, false, IdeModalityType.PROJECT),
    CoroutineScope by ApplicationThreadPoolScope("Database") {
    var rateLimit = 2000
    val gson = Serializer.gsonBuilder
        .setPrettyPrinting()
        .disableHtmlEscaping()
        //.enableComplexMapKeySerialization()
        .create()

    //Define UI components here, so they can be accessed or modified after adding to view.
    //File row
    lateinit var filePathField: CellBuilder<TextFieldWithBrowseButton>

    //Use field as DocId row
    lateinit var useFieldAsDocidCheckBox: CellBuilder<JBCheckBox>
    var useFieldAsDocid = false
    lateinit var jsonFieldTextField: CellBuilder<JBTextField>
    var jsonFieldName = ""

    //Create Collection Row
    lateinit var createCollectionCheckBox: CellBuilder<JBCheckBox>
    lateinit var newNameField: CellBuilder<JBTextField>
    var newCollectionName: String = ""

    val edtContext = getCoroutineUiContext()


    val view = panel {
        row {
            // TODO: Add withValidationOnApply so if a file is not a JSON it won't let you "apply"
            filePathField = textFieldWithBrowseButton(
                ::filePath,
                "Choose a JSON File",
                project,
                createSingleFileDescriptor("json"),
                null,
            ).withValidationOnApply {
                if (it.text.trim().isEmpty()) {
                    ValidationInfo(
                        "A valid JSON file is required",
                        it
                    )
                } else null
            }

        }
        row {
            useFieldAsDocidCheckBox = checkBox("Use field as document-id", ::useFieldAsDocid)
            jsonFieldTextField = textField(::jsonFieldName).withValidationOnApply {
                if (it.text.trim().isEmpty() && it.isEnabled) {
                    ValidationInfo(
                        "Field name is required.",
                        it
                    )
                } else null
            }.enableIf(useFieldAsDocidCheckBox.selected)

        }
        //row("") {}
        /*row {
            createCollectionCheckBox = checkBox("Create new collection", ::createNewCollection)
            newNameField = textField(::newCollectionName).withValidationOnApply {
                if (it.text.trim().isEmpty() && it.isEnabled) {
                    ValidationInfo(
                        "Collection name is required.",
                        it
                    )
                } else null
            }.enableIf(createCollectionCheckBox.selected)
        }*/
    }

    init {


        //Blank out the new collection name field if it gets disabled. Replace the contents of the text if re-enabled
        //Since the current text is applied, the empty text will overwrite the newNameField value
        jsonFieldTextField.component.addPropertyChangeListener {
            if (it.propertyName == "enabled" && !(it.newValue as Boolean)) {
                jsonFieldName = jsonFieldTextField.component.text
                jsonFieldTextField.component.text = ""
            }
            if (it.propertyName == "enabled" && it.newValue as Boolean) {
                jsonFieldTextField.component.text = jsonFieldName
            }
        }
        /*newNameField.component.addPropertyChangeListener {
            if (it.propertyName == "enabled" && !(it.newValue as Boolean)) {
                newCollectionName = newNameField.component.text
                newNameField.component.text = ""
            }
            if (it.propertyName == "enabled" && it.newValue as Boolean) {
                newNameField.component.text = newCollectionName
            }
        }*/


        //browseFilesButton.addActionListener { actionEvent: ActionEvent? ->
        //    changeSelectedFile()
        //}
        title = "Upload Documents from JSON File"
        setOKButtonText("Upload Documents")

        init()


        //filePathField.component.lab
        filePathField.component.isEditable = false
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
            if (this.isNotEmpty()) {
                filePath = this
            } else {

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
        //Load file


        try {
            launch {
                val batchWriter = BatchDocWriter(filePath,endpoint,jsonFieldName)
                val loadComplete = batchWriter.loadAndSendAll(AstraClient)
                if(loadComplete){
                    successfulBatchUploadNotification(endpoint.collection)
                }
            }
        } catch (ioException: IOException) {
            ioException.printStackTrace()


        } finally {
            close(OK_EXIT_CODE)
        }


    }

}
