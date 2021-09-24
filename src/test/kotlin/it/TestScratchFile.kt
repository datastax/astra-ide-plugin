package it

import TestClient
import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.BatchDocWriter
import com.datastax.astra.jetbrains.utils.editor.ui.EndpointCollection
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.net.URI

class TestBatchFunctions(database: Database, val testClient: TestClient) : CoroutineScope by ApplicationThreadPoolScope("Database") {

    val gson = GsonBuilder().create()
    var endpoint = EndpointCollection(database, "keyspaceOne", "collection_5")
    var myWriter = BatchDocWriter("/home/matthew/Downloads/listings_all.json", endpoint)
    var basePath = database.dataEndpointUrl.orEmpty().removeSuffix(URI(database.dataEndpointUrl!!).rawPath)
    var slidingWindow = mutableListOf<WindowPoint>()

    init {

        launch {
            myWriter.loadAndSendAll(testClient)
        }
    }




}
data class WindowPoint(
    val dbOps: Int,
    val time: Long,
)