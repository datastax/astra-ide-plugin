package com.datastax.astra.jetbrains.utils

import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.utils.editor.ui.EndpointCollection
import com.datastax.astra.stargate_document_v2.infrastructure.getErrorResponse
import com.datastax.astra.stargate_document_v2.models.InlineResponse202
import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.stream.JsonReader
import com.jetbrains.rd.util.AtomicInteger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.*
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

//TODO: Validate file path before passing it to this class
class BatchDocWriter(
    val filePath: String,
    val endpoint: EndpointCollection,
    val jsonFieldName: String = ""
): CoroutineScope by ApplicationThreadPoolScope("Database") {
    val batchChannel = Channel<List<LinkedTreeMap<*, *>>>(3)

    //TODO: Use to shutdown reader if problem occurs? Just close the channel?
    var readerEnabled = AtomicBoolean(true)
    var maxOpsPerBatch = AtomicInteger(300)
    val gson = GsonBuilder().create()
    val astraClient = AstraClient

    init {

        launch {
            streamBatches(filePath,batchChannel,maxOpsPerBatch)
        }

    }

    suspend fun buildRequestList(){

    }

    suspend fun loadAndSendAll(client: AstraClientBase) {
        var overRate = false
        var nonRateFailure = false
        var requestCount = 0
        var errors = 0
        var requestList = mutableListOf<List<LinkedTreeMap<*, *>>>()
        var concurrentRequests = 1

        while (!batchChannel.isClosedForReceive && readerEnabled.get()) {
            if (!batchChannel.isEmpty && !overRate) {
                requestList.add(batchChannel.receive())

                //Check request list size here

            }
            if(requestList.size>=concurrentRequests || overRate){
                val responseList = sendBatchAndWait(requestList,client)
                requestCount+= responseList.size
                val failed = responseList.filter { !it.isSuccessful }
                if (failed.isNotEmpty()) {
                    //failedDocInsertNotification(failed.size)
                    if(failed.all { it.getErrorResponse<Any?>().toString().contains("500")})  {
                        overRate = true
                        val newSublist = mutableListOf<List<LinkedTreeMap<*, *>>>()
                        failed.forEach {
                            errors++
                            //myWriter.opsPerRequest -= 50
                            val failed = requestList[responseList.indexOf(it)]
                            newSublist.add(failed.subList(0,(failed.size/2)-1))
                            newSublist.add(failed.subList(failed.size/2,failed.size-1))
                        }
                        requestList.clear()
                        requestList.addAll(newSublist)

                    }
                    else { nonRateFailure = true }

                }
                if (failed.isEmpty()) {
                    if(!overRate && maxOpsPerBatch.get() < 1100 && errors < 3) {
                        //opsPerRequest += 50
                    }
                    overRate = false
                    requestList.clear()
                }
            }
        }

    }

    suspend fun streamBatches(filePath: String, channel: Channel<List<LinkedTreeMap<*, *>>>, opsPerBatch: AtomicInteger){
        //create JsonReader and keep the channel filled with documents until told to stop or
        try {
            JsonReader( InputStreamReader(FileInputStream(filePath), Charsets.UTF_8) ).use { jsonReader ->
                val gson = GsonBuilder().create()
                jsonReader.beginArray() //start of json array
                var batchOpsCount = 0
                var docArray = mutableListOf<LinkedTreeMap<*,*>>()
                // This won't always work because you could get several large docs in a row that causes overrate
                //var docsPerRequest = 10

                while (readerEnabled.get() && jsonReader.hasNext()) { //next json array element
                    val nextDoc = gson.fromJson(jsonReader, Any::class.java) as LinkedTreeMap<*, *>
                    var docOpsCount = 0
                    nextDoc.forEach {
                        if (it.value is Collection<*>) {
                            docOpsCount += (it.value as Collection<*>).size
                        } else {
                            docOpsCount++
                        }
                    }
                    if((batchOpsCount+docOpsCount) >= opsPerBatch.get()){
                        channel.send(docArray)
                        docArray.clear()
                        batchOpsCount = 0
                    }
                    docArray.add(nextDoc)
                    batchOpsCount += docOpsCount
                }
                jsonReader.endArray()
                    if(docArray.size > 0){
                    channel.send(docArray)
                }
            }
        } catch (e: UnsupportedEncodingException) {
            readerEnabled.set(false)
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            // TODO: Handle this!
            readerEnabled.set(false)
            e.printStackTrace()
        } catch (e: IOException) {
            readerEnabled.set(false)
            e.printStackTrace()
        }
        finally {

            channel.close()
        }
    }

    suspend fun sendBatchAndWait(requests: List<List<LinkedTreeMap<*, *>>>, client: AstraClientBase=astraClient): List<Response<InlineResponse202>> {
        val responses = runBlocking {
            requests.map { batch ->
                async {
                    client.documentApiForDatabase(endpoint.database).addMany(
                        UUID.randomUUID(),
                        client.accessToken,
                        endpoint.keyspace,
                        endpoint.collection,
                        gson.toJson(batch).toRequestBody("text/plain".toMediaTypeOrNull()),
                        jsonFieldName.ifEmpty { null },
                    )
                }
            }
        }
        return responses.awaitAll()
    }

}

