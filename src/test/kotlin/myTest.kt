import java.io.File
import kotlin.reflect.*
import org.openapitools.client.infrastructure.*
import org.openapi.example.api.*
import org.openapi.example.model.*

fun main() {
    test_listDatabases()

}
//Stub for testing adding a DB
fun test_putDatabase(){

}

//Test listing all DBs
fun test_listDatabases(){
    ApiClient.accessToken = "AstraCS:nsISvKavXHEpZLTIAEsoNxaC:f265ecb8677dd8bbfccb2bb332a31b3a3ed08155a43caa3de5dcba8718078a83"
    var dbHolderType:String = ""
    var dbHolderObjType:String = ""

    try{
        //Set up file location to write http response
        //The response will overflow the console otherwise
        val path = System.getProperty("user.dir")
        val myfile = File("$path/src/test/kotlin/testOutput.txt")
        //Request a DB from the server
        val dbCollection: List<Database> = OperationsApi().listDatabases(null,null,null,null)
        //Write result to a file
        myfile.bufferedWriter().use { out ->
            out.write(dbCollection.toString()+"\n")
        }
        dbHolderType = dbCollection.javaClass.kotlin.toString()
        dbHolderObjType = dbCollection.component1().javaClass.kotlin.toString()
        //Try to get a single db
        val dbSingle = dbCollection.get(0)
        myfile.bufferedWriter().use { out ->
            out.write(dbSingle.toString()+"\n")
        }
        //Make this more verbose and/or add capture of other exception types
    } catch(e: ClassCastException){
        println("test_listDatabases: Failed!")
        print("Exception: ")
        println(e)
    }
    finally {
        var checkdbHolderType = false
        var checkdbHolderObjType = false
        //Check holder type
        if (dbHolderType == "class java.util.ArrayList") checkdbHolderType = true
        else{
            println("listDatabases type test failed! Wrong dbHolderType.")
            println("Expected: class java.util.ArrayList")
            println("Returned: $dbHolderType")
        }
        //Check holder type
        if (dbHolderObjType == "class org.openapitools.client.models.Database") checkdbHolderType = true
        else{
            println("listDatabases type test failed! Wrong dbHolderObjType.")
            println("Expected: class org.openapitools.client.models.Database")
            println("Returned: $dbHolderObjType")
        }

        //Add more conditions for passing (Check against recently created DB)
        if ((checkdbHolderType && checkdbHolderObjType)) {
            println("listDatabases and obj type tests passed!")
        }
    }
}
//Stub for testing adding a DB
fun test_removeDatabase(){

}

