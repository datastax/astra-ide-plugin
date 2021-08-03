import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.credentials.CredentialsClient
import com.datastax.astra.stargate_document_v2.infrastructure.Serializer
import com.intellij.json.psi.JsonObject
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID.randomUUID

class myTest {




}
fun main() {
    val gson = Serializer.gsonBuilder.create()
    runBlocking {
        val database =
            CredentialsClient.operationsApi("AstraCS:JmUQIhbKDwCYxEXPqtENZqoI:481932c00dfa7cba987a7ddd68839870a2db953f8ac9fe7936d9f4c6efc1a7f9")
                .getDatabase("8174cdc1-5fea-4529-b531-6564d34fbbce").body()
        val response = AstraClient.documentApiForDatabase(database!!).replaceDoc(
            randomUUID(),
            "AstraCS:JmUQIhbKDwCYxEXPqtENZqoI:481932c00dfa7cba987a7ddd68839870a2db953f8ac9fe7936d9f4c6efc1a7f9",
            "simpleKS",
            "hello_docs",
            "myDoc3",
            """{"another field":"Docs don't need to follow a set structure","other":"All in all youre just another doc in the hall","title":"another doc123"}""".toRequestBody()
        )

        println(response.message())
        println(response.toString())
        println(response.getErrorResponse<Any?>().toString())
    }
}
