import com.datastax.astra.devops_v2.apis.DBOperationsApi
import com.datastax.astra.devops_v2.infrastructure.Serializer
import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.devops_v2.models.StatusEnum
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.AstraClientBase
import com.datastax.astra.jetbrains.utils.BatchDocWriter
import com.datastax.astra.jetbrains.utils.editor.ui.EndpointCollection
import com.datastax.astra.stargate_document_v2.models.DocumentResponseWrapper
import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.stream.JsonReader
import com.intellij.util.containers.toArray
import com.jetbrains.rd.util.first
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import okhttp3.MediaType.Companion.get
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.*
import retrofit2.Response
import java.io.*

import java.util.*

//TODO:
// Make sure client sends proper requests for with a JSON field ID and without (success)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DocBatchUploadIntegrationTest {
    private lateinit var database: Database
    private lateinit var endpoint: EndpointCollection
    private lateinit var batchWriter: BatchDocWriter

    private val testJsonFilePath = this::class.java.getResource("/it_files/batchUpload.json").path
    private var testBatch = mutableListOf<List<LinkedTreeMap<*, *>>>()
    val databaseId = "de2e26e0-e442-4316-b800-a5744a0cf48f"
    val keyspace = "itestbatchks"
    val collection = "itestcollection"
    private var testClient = TestClient
    val gson = GsonBuilder().create()

    @BeforeAll
    fun setup() {
        testClient.setToken("AstraCS:btyqoZkoxsmrumyGdBTZMskq:e25ab35a9bfd54dee86d83d08618c6351c99544054f8b74e32946d732bcf8216")

        runBlocking {
            val response = async {testClient.dbOperationsApi().getDatabase(databaseId)}.await()
            if (response.isSuccessful && !response.body()?.dataEndpointUrl.isNullOrEmpty() && response.body()?.status == StatusEnum.ACTIVE) {
                database = (response.body() as Database)
                val ksResponse = async {testClient.documentApiForDatabase(database).listCollections(
                    UUID.randomUUID(),
                    testClient.accessToken,
                    keyspace,)}.await()
                if(ksResponse.isSuccessful) {
                    if(ksResponse.body()?.data?.any { it.name == collection } == true){
                        async {  testClient.documentApiForDatabase(database).deleteCollection(
                            UUID.randomUUID(),
                            testClient.accessToken,
                            keyspace,
                            collection
                        )}.await()
                    }
                }
            }
            endpoint = EndpointCollection(database,keyspace,collection)
            batchWriter = BatchDocWriter("testJsonFilePath",endpoint)
            //Add the same document twice
            testBatch.add(buildBatch(testJsonFilePath))

        }



    }

    @AfterAll
    fun teardown() {

    }

    //Assumes this test starts by clearing all databases
    @Test
    fun canDoBatchUploadAndDownload() = runBlocking {
        var destinationJson = mutableListOf<LinkedTreeMap<*, *>>()

        val putResponse = batchWriter.sendBatchAndWait(testBatch,testClient).first()
        val getResponse: Response<DocumentResponseWrapper>
        if(putResponse.isSuccessful){
            getResponse = testClient.documentApiForDatabase(database).getCollection(
                UUID.randomUUID(),
                testClient.accessToken,
                endpoint.keyspace,
                endpoint.collection,
                pageSize = "5"
            )
            if(getResponse.isSuccessful){
                destinationJson.addAll((getResponse.body()?.data as LinkedTreeMap<*, *>).map { it.value as LinkedTreeMap<*, *>}.toList())
            }
        }

        Assertions.assertEquals(testBatch.first().size,destinationJson.size)
        Assertions.assertTrue(testBatch.first().containsAll(destinationJson) )
    }


}

fun buildBatch(filePath: String): List<LinkedTreeMap<*, *>> {
    //create JsonReader and keep the channel filled with documents until told to stop or
    var docArray = mutableListOf<LinkedTreeMap<*, *>>()
    try {
        JsonReader(InputStreamReader(FileInputStream(filePath), Charsets.UTF_8)).use { jsonReader ->
            val gson = GsonBuilder().create()
            jsonReader.beginArray() //start of json array
            while (jsonReader.hasNext()) { //next json array element
                val nextDoc = gson.fromJson(jsonReader, Any::class.java) as LinkedTreeMap<*, *>
                docArray.add(nextDoc)
                docArray.add(nextDoc)
            }
            jsonReader.endArray()
        }
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
    }
    return docArray
}