package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.explorer.DatabaseNode
import com.datastax.astra.jetbrains.explorer.ExplorerDataKeys.SELECTED_NODES
import com.datastax.astra.jetbrains.explorer.isProcessing
import com.datastax.astra.jetbrains.explorer.refreshTree
import com.datastax.astra.jetbrains.telemetry.CrudEnum
import com.datastax.astra.jetbrains.telemetry.TelemetryManager.trackDevOpsCrud
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.stream.JsonReader
import com.intellij.jsonpath.ui.JsonPathEvaluateManager
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.WindowManager
import com.intellij.testFramework.LightVirtualFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*

class FooAction :
    DumbAwareAction("FooBar", null, null),
    CoroutineScope by ApplicationThreadPoolScope("Database") {

    override fun actionPerformed(e: AnActionEvent) {
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
        launch {
            withContext(edtContext) {

                JsonPathEvaluateManager.getInstance(e.project!!).evaluateExpression(jsonString)
                println(jsonString)
            }
        }

    }

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

    // If DB is processing grey out access to creating a keyspace
    override fun update(e: AnActionEvent) {
        
    }
}
