package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.explorer.CollectionNode
import com.datastax.astra.jetbrains.explorer.ExplorerDataKeys
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.editor.ui.EndpointCollection
import com.datastax.astra.jetbrains.utils.editor.ui.failedDocInsertNotification
import com.datastax.astra.jetbrains.utils.editor.ui.successfulDocInsertNotification
import com.datastax.astra.stargate_document_v2.infrastructure.Serializer
import com.datastax.astra.stargate_document_v2.models.InlineResponse202
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
import com.jetbrains.rd.util.measureTimeMillis
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.awt.Component
import java.io.*
import java.lang.Thread.sleep
import java.util.*
import javax.swing.JComponent
import kotlin.collections.ArrayList
var megaList = mutableListOf<Int>()

class UploadJsonCollectionAction : DumbAwareAction("Insert Documents", null, null) {
    override fun actionPerformed(e: AnActionEvent) {
        e.getData(ExplorerDataKeys.SELECTED_NODES)?.map { it as? CollectionNode }?.singleOrNull()?.run {
            UploadJsonCollectionDialog(this.nodeProject,
                EndpointCollection(this.database, this.keyspace.name, this.collection.name), "", false).show()
        }
    }
}

class UploadJsonCreateCollectionAction : DumbAwareAction("Create Collection and Insert Documents", null, null) {
    override fun actionPerformed(e: AnActionEvent) {
        e.getData(ExplorerDataKeys.SELECTED_NODES)?.map { it as? CollectionNode }?.singleOrNull()?.run {
            val filepath = ""
            UploadJsonCollectionDialog(this.nodeProject,
                EndpointCollection(this.database, this.keyspace.name, this.collection.name), filepath, true).show()
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
        row {
            createCollectionCheckBox = checkBox("Create new collection", ::createNewCollection)
            newNameField = textField(::newCollectionName).withValidationOnApply {
                if (it.text.trim().isEmpty() && it.isEnabled) {
                    ValidationInfo(
                        "Collection name is required.",
                        it
                    )
                } else null
            }.enableIf(createCollectionCheckBox.selected)
        }
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
        newNameField.component.addPropertyChangeListener {
            if (it.propertyName == "enabled" && !(it.newValue as Boolean)) {
                newCollectionName = newNameField.component.text
                newNameField.component.text = ""
            }
            if (it.propertyName == "enabled" && it.newValue as Boolean) {
                newNameField.component.text = newCollectionName
            }
        }


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
                //parseSplitSerialize(filePath).forEachIndexed{index,requestString ->
                //    println("Request #$index: , Size: ${requestString.length}")
                //}
                //https://stackoverflow.com/questions/20442265/how-to-decode-json-with-unknown-field-using-gson


                //    println(jsonFieldName)
                   uploadAndGiveFeedback(parseSplitSerialize(filePath),jsonFieldName.takeIf{ useFieldAsDocid }.orEmpty())
            }
        } catch (ioException: IOException) {
            ioException.printStackTrace()


        } finally {
            close(OK_EXIT_CODE)
        }

        //TODO:
        // 1) Access file and turn it into a PSI file
        // 2) process it like before
        // 3) Allow choosing a field to name the document with
        //   i)replaceDoc if provided
        //   ii)addDoc if not provided
        //launch(ApplicationThreadPoolScope("Explorer").coroutineContext ) {

        //}


        //println("Loading documents from $filePath to $newCollectionName")
    }

    fun parseSplitSerialize(jsonFilePath: String?): List<String>{
        //create JsonReader object and pass it the json file,json source or json text.
        val serializedSubLists = mutableListOf<String>()
        try {
            JsonReader(
                InputStreamReader(
                    FileInputStream(jsonFilePath), Charsets.UTF_8)).use { jsonReader ->
                val gson = GsonBuilder().create()
                jsonReader.beginArray() //start of json array
                val nextDocsList = mutableListOf<LinkedTreeMap<*, *>>()
                val fieldSizeList = mutableListOf<Int>()
                var fieldCount = 0
                while (jsonReader.hasNext()) { //next json array element
                    val nextDoc = (gson.fromJson(jsonReader, Any::class.java) as LinkedTreeMap<*, *>)
                    fieldCount += nextDoc.size
                    nextDocsList.add(nextDoc)
                    //If it's larger than 800000 chars or 4096 field changes/s it could be too large to send over the wire or hit rate limit
                    //Try to send <~4000 field changes/s or 4 split requests/s
                    if(nextDocsList.size%8==0 && nextDocsList.toString().toByteArray(Charsets.UTF_8).size > 600000 || fieldCount>900){
                        if(nextDocsList.toString().toByteArray(Charsets.UTF_8).size > 800000 || fieldCount>1000){
                            serializedSubLists.add(gson.toJson(nextDocsList.subList(0,(nextDocsList.size/2)-1)).toString())
                            serializedSubLists.add(gson.toJson(nextDocsList.subList(nextDocsList.size/2,nextDocsList.size-1)).toString())
                            fieldSizeList.add(fieldCount/2)
                            fieldSizeList.add(fieldCount/2)
                        }
                        else {
                            fieldSizeList.add(fieldCount)
                            serializedSubLists.add(gson.toJson(nextDocsList).toString())
                        }
                        nextDocsList.clear()
                        fieldCount=0
                    }
                }
                if(nextDocsList.isNotEmpty()){
                    serializedSubLists.add(gson.toJson(nextDocsList).toString())
                    fieldSizeList.add(fieldCount)
                }
                megaList = fieldSizeList
                jsonReader.endArray()
                println("Total requests required: ${serializedSubLists.size}")
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            // TODO: Handle this!
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return serializedSubLists
    }

    suspend fun uploadAndGiveFeedback(
        serializedSubLists: List<String>,
        jsonFieldName: String = "",
    ) {
        try {
            val startTime = System.currentTimeMillis()
            var time1 = 0L
            var time2 = 0L
            var timeDif = 0L
            var index = 0
            var lastFieldSize = 0
            var lastlastFieldSize = 0
            var requestCount = 0
            var fieldChangeTotal = 0
            //TODO: Make sure we aren't exceeding the max number of connections vs rate-limit
            val responses = runBlocking {
                serializedSubLists.map { doc ->
                    fieldChangeTotal += megaList[index]
                    requestCount = lastFieldSize + lastlastFieldSize
                    lastlastFieldSize = lastFieldSize
                    lastFieldSize = megaList[index]
                    timeDif = System.currentTimeMillis()-time2
                    time2 = time1
                    time1 = System.currentTimeMillis()
                    println("Sending request ${index++}. Avg request rate: ${requestCount/(timeDif/1000.0)}")
                    println("Total Avg request rate: ${fieldChangeTotal/((time1-startTime)/1000.0)}")
                    sleep(2000)
                    async {
                        AstraClient.documentApiForDatabase(endpoint.database).addMany(
                            UUID.randomUUID(),
                            AstraClient.accessToken,
                            endpoint.keyspace,
                            endpoint.collection,
                            doc.toRequestBody("text/plain".toMediaTypeOrNull()),
                            jsonFieldName.ifEmpty { null },
                        )
                    }
                }
            }.awaitAll()

            val success = responses.filter { it.isSuccessful }
            val failed = responses.filter { !it.isSuccessful }
            // Do the rest in the UI context because we show some notifications and possibly generate dialog boxes from them
            withContext(edtContext) {
                if (failed.isNotEmpty()) {
                    //failedDocInsertNotification(failed.size)
                    failed.forEach {
                        println(it.getErrorResponse<Any?>().toString())
                    }
                    // TODO: Add telemetry for this action
                    // TODO: Add more information about failed inserts
                }
                if (success.isNotEmpty()) {
                    println("${success.size} requests succeeded")
                    //successfulDocInsertNotification(success.size, cBoxes.collectionComboBox.selectedItem)
                    // TODO: Add telemetry for this action
                }
            }
        } catch (e: Exception) {
            // TODO: Cancel doing stuff here and tell user
            // TODO: Telemetry about failure
        }

    }
}