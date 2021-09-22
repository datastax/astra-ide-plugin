import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.BatchDocWriter
import com.datastax.astra.jetbrains.utils.editor.ui.EndpointCollection
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import java.net.URI
import java.util.*

//TODO:
// Make sure client sends proper requests for with a JSON field ID and without (success)

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

suspend fun main() {
    val testClient = TestClient
    testClient.setToken("AstraCS:LoPreOfZnulFjZUEGKWjYhKp:139275a045f60931ae575d5aaf88f1042f6e06209c8a278051d4c40796925e8f")
    val response = testClient.dbOperationsApi().getDatabase("11cb78bd-4b16-4737-8099-3f4af07bbed1")
    val database = response.body()!!

    TestBatchFunctions(database,testClient)

}

data class WindowPoint(
    val dbOps: Int,
    val time: Long,
)