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
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import okhttp3.MediaType.Companion.get
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.net.URI
import java.util.*

//TODO:
// Make sure client sends proper requests for with a JSON field ID and without (success)

class MockClientTests {
    private lateinit var database: Database
    private lateinit var endpoint: EndpointCollection
    private lateinit var batchWriter: BatchDocWriter

    private var testBatch = listOf<List<LinkedTreeMap<*, *>>>()
    private val testJsonFilePath = "/it_files/batchUpload.json"
    val keyspace = "itestbatchks"
    val collection = "itestcollection"
    private var testClient = TestClient
    val gson = GsonBuilder().create()

    @BeforeAll
    suspend fun setup() {
        testClient.setToken("AstraCS:btyqoZkoxsmrumyGdBTZMskq:e25ab35a9bfd54dee86d83d08618c6351c99544054f8b74e32946d732bcf8216")

        database = testClient.dbOperationsApi().getDatabase("de2e26e0-e442-4316-b800-a5744a0cf48f").let {
            if(it.isSuccessful && it.body()?.dataEndpointUrl.isNullOrEmpty() && it.body()?.status== StatusEnum.ACTIVE){
                it.body()
            }
            else {
                //end test or retry
            }
        } as Database
        endpoint = EndpointCollection(database,keyspace,collection)
        batchWriter = BatchDocWriter("testJsonFilePath",endpoint)

        //Add the same document twice
        testBatch.add()
        testBatch.add()


    }

    @AfterEach
    suspend fun teardown() {
        //Delete Documents?
        /*testClient.documentApiForDatabase(database).deleteDoc(
            UUID.randomUUID(),
            testClient.accessToken,
            endpoint.keyspace,
            endpoint.collection,
            gson.toJson(batch).toRequestBody("text/plain".toMediaTypeOrNull()),
            jsonFieldName.ifEmpty { null },
        )*/
        //Delete Documents?
    }

    //Assumes this test starts by clearing all databases
    @Test
    fun canDoBatchUploadAndDownload() = runBlocking {
        val sourceJson: String

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
                sourceJson = gson.toJson(getResponse.body()?.data)
            }
        }


        Assertions.assertEquals(0, response.body()?.size)
        Assertions.assertTrue(response.body().toString() == "[]")
    }
}
class TransferUtilsIntegrationTest {
    val testClient = TestClient
    testClient.setToken("AstraCS:btyqoZkoxsmrumyGdBTZMskq:e25ab35a9bfd54dee86d83d08618c6351c99544054f8b74e32946d732bcf8216")


    val response = testClient.dbOperationsApi().getDatabase("de2e26e0-e442-4316-b800-a5744a0cf48f")
    val database = response.body()!!
    val sourceFile
    TestBatchFunctions(database,testClient)


    private val s3Client = S3Client.builder()
        .httpClient(ApacheHttpClient.builder().build())
        .region(Region.US_WEST_2)
        .serviceConfiguration { it.pathStyleAccessEnabled(true) }
        .build()
    //Build Client
    @JvmField
    @Rule
    val folder = TemporaryFolder()
    //Create/Load JSON file
    @JvmField
    @Rule
    val projectRule = ProjectRule()
    //Define rules
    @JvmField
    @Rule
    val bucketRule = S3TemporaryBucketRule(s3Client)

    @Test
    fun canDoUploadAndDownload() {
        val bucket = bucketRule.createBucket()
        val bigString = "hello world".repeat(1000)
        //Create Collection
        val sourceFile = folder.newFile()
        sourceFile.writeText(bigString)
        //Upload JSON
        s3Client.upload(projectRule.project, sourceFile.toPath(), bucket, "file", message = "uploading").value

        //Check Collection for JSON
        val destinationFile = folder.newFile()
        s3Client.download(projectRule.project, bucket, "file", null, destinationFile.toPath(), message = "downloading").value

        //Assert download JSON is the same as the uploaded (do necessary GSON transformations)
        assertThat(destinationFile).hasSameTextualContentAs(sourceFile)
    }
}

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
    val response = testClient.dbOperationsApi().getDatabase("de2e26e0-e442-4316-b800-a5744a0cf48f")
    val database = response.body()!!

    TestBatchFunctions(database,testClient)

}

data class WindowPoint(
    val dbOps: Int,
    val time: Long,
)

fun buildBatchAndSerialize(filePath: String): String{
    //create JsonReader and keep the channel filled with documents until told to stop or
    try {
        JsonReader( InputStreamReader(FileInputStream(filePath), Charsets.UTF_8) ).use { jsonReader ->
            val gson = GsonBuilder().create()
            jsonReader.beginArray() //start of json array
            var docArray = mutableListOf<LinkedTreeMap<*,*>>()
            // This won't always work because you could get several large docs in a row that causes overrate
            //var docsPerRequest = 10

            while (jsonReader.hasNext()) { //next json array element
                val nextDoc = gson.fromJson(jsonReader, Any::class.java) as LinkedTreeMap<*, *>
                docArray.add(nextDoc)
                docArray.add(nextDoc)
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