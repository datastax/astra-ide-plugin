package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.explorer.CollectionNode
import com.datastax.astra.jetbrains.explorer.ExplorerDataKeys
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.editor.ui.EndpointCollection
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
    var slidingAvg = 0.0
    val slidingWindow = mutableListOf<WindowPoint>()
    var requestIndex = 0
    var throttlingDelay = 500L
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
                count(filePath)
                parseSplitSerialize(filePath)
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

    fun parseSplitSerialize(jsonFilePath: String?): Pair<List<String>, List<Int>> {
        //create JsonReader object and pass it the json file,json source or json text.
        val serializedSubLists = mutableListOf<String>()
        val fieldSizeList = mutableListOf<Int>()
        val startTime = System.currentTimeMillis()
        var maxStreams = 100
        var firstOverrate = true
        try {
            JsonReader(
                InputStreamReader(
                    FileInputStream(jsonFilePath), Charsets.UTF_8)).use { jsonReader ->
                val gson = GsonBuilder().create()
                jsonReader.beginArray() //start of json array
                val nextDocList = mutableListOf<String>()

                var fieldCount = 0
                var concurrentRequests = 8
                var nonRateFailure = false
                var docListIndex = 0
                while (!nonRateFailure && jsonReader.hasNext()) { //next json array element
                    val nextDoc = (gson.fromJson(jsonReader, Any::class.java) as LinkedTreeMap<*, *>)
                    nextDoc.forEach {
                        if (it.value is Collection<*>) {
                            fieldCount += (it.value as Collection<*>).size
                        } else {
                            fieldCount++
                        }
                    }
                    fieldSizeList.add(fieldCount)
                    serializedSubLists.add(gson.toJson(nextDoc))
                    fieldCount = 0
                    nextDocList.add(gson.toJson(nextDoc))
                    if (nextDocList.size% concurrentRequests == 0) {
                        val concurrentRequestsStart = concurrentRequests
                        var overRate = false
                        runBlocking {
                            do {
                                val responses = if (overRate) {
                                    println("Resending set #${docListIndex++} with ${nextDocList.size} request(s). RateLimit:$rateLimit, Delay:$throttlingDelay, Rate:$slidingAvg")
                                    overRate = false
                                    addAndWait(nextDocList, fieldSizeList, startTime)

                                } else {
                                    println("Sending set #${docListIndex++} with ${nextDocList.size} requests. RateLimit:$rateLimit, Delay:$throttlingDelay, Rate:$slidingAvg")
                                    addAndWait(nextDocList, fieldSizeList, startTime)
                                }

                                val success = responses.filter { it.isSuccessful }
                                val failed = responses.filter { !it.isSuccessful }
                                // Do the rest in the UI context because we show some notifications and possibly generate dialog boxes from them

                                if (failed.isNotEmpty()) {

                                    //failedDocInsertNotification(failed.size)
                                    if (failed.filter {
                                            it.getErrorResponse<Any?>().toString().contains("500")
                                        }.size == failed.size) {
                                        overRate = true
                                        val newList = mutableListOf<String>()
                                        failed.forEach {
                                            requestIndex--
                                            rateLimit = if (rateLimit > 500) {
                                                rateLimit - 250
                                            } else {
                                                rateLimit
                                            }
                                            throttlingDelay += 100
                                            newList.add(nextDocList[responses.indexOf(it)])
                                        }
                                        if(concurrentRequestsStart - concurrentRequests<2){
                                            concurrentRequests--
                                        }
                                        nextDocList.clear()
                                        nextDocList.addAll(newList)
                                        docListIndex--
                                        println("${failed.size} request(s) failed")
                                    } else {
                                        nonRateFailure = true
                                    }

                                    if(firstOverrate){
                                        firstOverrate=false
                                        maxStreams=concurrentRequests
                                    }
                                }

                                if (success.isNotEmpty()) {
                                    if (rateLimit <= 4000) {
                                        rateLimit += (100 * success.size)
                                    } else if (rateLimit < 4400) {
                                        rateLimit += 100
                                    }

                                    if(concurrentRequests - concurrentRequestsStart<2 && concurrentRequests<maxStreams){
                                        concurrentRequests++
                                    }

                                    println("${success.size} request(s) succeeded. $requestIndex/??")
                                    //successfulDocInsertNotification(success.size, cBoxes.collectionComboBox.selectedItem)
                                }
                            } while (!nonRateFailure && overRate)
                            nextDocList.clear()
                        }
                    }
                }
                if (nextDocList.isNotEmpty()) {
                    //serializedSubLists.add(gson.toJson(nextDocsList).toString())
                    //fieldSizeList.add(fieldCount)
                }
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
        return Pair(serializedSubLists, fieldSizeList)
    }

    fun count(jsonFilePath: String?): Pair<List<String>, List<Int>> {
        //create JsonReader object and pass it the json file,json source or json text.
        val serializedSubLists = mutableListOf<String>()
        val fieldSizeList = mutableListOf<Int>()
        val startTime = System.currentTimeMillis()
        var maxStreams = 100
        var firstOverrate = true
        try {
            JsonReader(
                InputStreamReader(
                    FileInputStream(jsonFilePath), Charsets.UTF_8)).use { jsonReader ->
                val gson = GsonBuilder().create()
                jsonReader.beginArray() //start of json array
                val nextDocList = mutableListOf<String>()

                var fieldCount = 0
                var concurrentRequests = 8
                var nonRateFailure = false
                var docListCount = 0
                while (!nonRateFailure && jsonReader.hasNext()) { //next json array element
                    val nextDoc = (gson.fromJson(jsonReader, Any::class.java) as LinkedTreeMap<*, *>)
                    docListCount++
                }
                if (nextDocList.isNotEmpty()) {
                    //serializedSubLists.add(gson.toJson(nextDocsList).toString())
                    //fieldSizeList.add(fieldCount)
                }
                println("Total Doc Count: $docListCount")
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
        return Pair(serializedSubLists, fieldSizeList)
    }

    suspend fun uploadAndGiveFeedback(
        sublistAndRates: Pair<List<String>, List<Int>>,
        jsonFieldName: String = "",
    ) {
        for (i in 0..5) {
            slidingWindow.add(0, WindowPoint(sublistAndRates.second[i], System.currentTimeMillis() + (i * 200)))
        }
        slidingAvg =
            slidingWindow.sumOf { it.fieldSize } / ((slidingWindow.first().time - slidingWindow.last().time) / 1000.0)
        try {
            var nonRateFailure = false
            var overRate = false
            var sublistIndex = 0
            val concurrentRequests = 14
            val splitLists = sublistAndRates.first.chunked(concurrentRequests)
            val startTime = System.currentTimeMillis()
            var requestsSublist = mutableListOf<String>()
            while (!nonRateFailure && sublistIndex < splitLists.size) {

                val responses = if (overRate) {
                    println("Resending set #${sublistIndex++} with ${requestsSublist.size} request(s). RateLimit:$rateLimit, Delay:$throttlingDelay, Rate:$slidingAvg")
                    overRate = false
                    addAndWait(requestsSublist, sublistAndRates.second, startTime)

                } else {
                    requestsSublist.clear()
                    requestsSublist.addAll(splitLists[sublistIndex])
                    println("Sending set #${sublistIndex++} with ${requestsSublist.size} requests. RateLimit:$rateLimit, Delay:$throttlingDelay, Rate:$slidingAvg")
                    addAndWait(requestsSublist, sublistAndRates.second, startTime)
                }

                val success = responses.filter { it.isSuccessful }
                val failed = responses.filter { !it.isSuccessful }
                // Do the rest in the UI context because we show some notifications and possibly generate dialog boxes from them

                if (failed.isNotEmpty()) {

                    //failedDocInsertNotification(failed.size)
                    if (failed.filter {
                            it.getErrorResponse<Any?>().toString().contains("500")
                        }.size == failed.size) {
                        overRate = true
                        val newSublist = mutableListOf<String>()
                        failed.forEach {
                            requestIndex--
                            rateLimit = if (rateLimit > 500) {
                                rateLimit - 250
                            } else {
                                rateLimit
                            }
                            throttlingDelay += 100
                            newSublist.add(requestsSublist[responses.indexOf(it)])
                        }
                        requestsSublist.clear()
                        requestsSublist.addAll(newSublist)
                        sublistIndex--
                        println("${failed.size} request(s) failed")
                    } else {
                        nonRateFailure = true
                    }
                }

                if (success.isNotEmpty()) {
                    if (rateLimit <= 4000) {
                        rateLimit += (100 * success.size)
                    } else if (rateLimit < 4400) {
                        rateLimit += 100
                    }



                    println("${success.size} request(s) succeeded. $requestIndex/${sublistAndRates.first.size}")
                    //successfulDocInsertNotification(success.size, cBoxes.collectionComboBox.selectedItem)
                }
            }

            println("Finished while loop")

        } finally {

            // TODO: Cancel doing stuff here and tell user
            // TODO: Telemetry about failure
        }

    }

    suspend fun addAndWait(
        requestsSublist: List<String>,
        fieldSizeList: List<Int>,
        startTime: Long,
    ): List<Response<Unit>> {
        val responses = runBlocking {
            sleep(throttlingDelay / requestsSublist.size)
            requestsSublist.map { doc ->
                slidingWindow.add(0, WindowPoint(fieldSizeList[requestIndex++], System.currentTimeMillis()))
                slidingAvg =
                    slidingWindow.sumOf { it.fieldSize } / ((slidingWindow.first().time - slidingWindow.last().time) / 1000.0)
                if (slidingAvg < rateLimit && throttlingDelay >= 2) {
                    throttlingDelay -= if (throttlingDelay >= 200) {
                        100
                    } else if (throttlingDelay >= 20) {
                        10
                    } else {
                        2
                    }
                } else if (slidingAvg > rateLimit && throttlingDelay <= 1000) {
                    throttlingDelay += 50
                }
                if ((slidingWindow.first().time - slidingWindow.last().time) > 2000) {
                    slidingWindow.removeLast()
                }
                if (slidingWindow.size > (requestsSublist.size * 1.5)) {
                    slidingWindow.removeLast()
                }
                sleep(throttlingDelay)
                async {
                    AstraClient.documentApiForDatabase(endpoint.database).addDoc(
                        UUID.randomUUID(),
                        AstraClient.accessToken,
                        endpoint.keyspace,
                        endpoint.collection,
                        doc.toRequestBody("text/plain".toMediaTypeOrNull()),
                        //jsonFieldName.ifEmpty { null },
                    )
                }
            }
        }
        return responses.awaitAll()
    }

}

data class WindowPoint(
    val fieldSize: Int,
    val time: Long,
)