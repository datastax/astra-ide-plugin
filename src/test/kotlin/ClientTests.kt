import com.datastax.astra.devops_v2.models.DatabaseInfoCreate
import com.datastax.astra.jetbrains.AstraClient
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.DataConstants
import com.intellij.openapi.project.Project
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class ClientTests {
    var dataContext = DataManager.getInstance().dataContext


    @Test
    fun testCreateDatabase() = runBlocking {
        AstraClient.project = dataContext.getData(DataConstants.PROJECT) as Project
        val databaseInfoCreate =
            DatabaseInfoCreate(
                "gmc_retro",
                "ks1",
                DatabaseInfoCreate.CloudProvider.AWS,
                DatabaseInfoCreate.Tier.SERVERLESS,
                1,
                "us-west-2"
            )
        val response = AstraClient.operationsApi().createDatabase(databaseInfoCreate)
        val databaseId = response.headers()["Location"]
        println(response.isSuccessful)
        println(databaseId)

        assertEquals(0, 0)
    }

    @Test
    fun testListDatabases() = runBlocking {
        AstraClient.project = dataContext.getData(DataConstants.PROJECT) as Project
        val foo = AstraClient.operationsApi().listDatabases(null, null, null, null)
        println(foo.isSuccessful)
        foo.body()?.forEach {
            println(it.dataEndpointUrl)
        }
        assertEquals(0, 0)
    }
}

