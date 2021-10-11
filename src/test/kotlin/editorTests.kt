
import com.datastax.astra.jetbrains.utils.getCoroutineUiContext
import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.stream.JsonReader
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.WindowManager
import com.intellij.testFramework.LightVirtualFile
import kotlinx.coroutines.*
import org.apache.batik.bridge.Window
import java.io.*
import java.util.*


class editorTests {


}

suspend fun main(){
    val edtContext = getCoroutineUiContext()
    var jsonString = getJson("/home/matthew/Downloads/listings_all.json")
    val projects = ProjectManager.getInstance().openProjects
    var activeProject: Project? = null
    for (project in projects) {
        val window: java.awt.Window? = WindowManager.getInstance().suggestParentWindow(project)
        if (window != null && window.isActive()) {
            activeProject = project
        }
    }
    withContext(edtContext) {

        FileEditorManager.getInstance(activeProject!!).openFile(
            LightVirtualFile(
                "FooBar",
                FileTypeManager.getInstance().getFileTypeByExtension("JSON"),
                jsonString
            ),
            true
        )

        println(jsonString)
    }
}

//TODO: Validate file path before passing it to this class


    fun getJson(filePath: String): String{
        //create JsonReader and keep the channel filled with documents until told to stop or
        var returnDoc= mutableListOf<LinkedTreeMap<*, *>>()
        val gson = GsonBuilder().create()
        try {
            JsonReader( InputStreamReader(FileInputStream(filePath), Charsets.UTF_8) ).use { jsonReader ->
                jsonReader.beginArray() //start of json array

                var docCount = 0
                while (docCount<1 && jsonReader.hasNext()) { //next json array element
                    returnDoc.add( gson.fromJson(jsonReader, Any::class.java) as LinkedTreeMap<*, *>)
                    docCount++
                }
                //jsonReader.endArray()
            }
        } catch (e: UnsupportedEncodingException) {

            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            // TODO: Handle this!

            e.printStackTrace()
        } catch (e: IOException) {

            e.printStackTrace()
        }
        finally {

        }
        return gson.toJson(returnDoc.first().orEmpty())
    }


