package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.stargate_document_v2.infrastructure.Serializer
import com.datastax.astra.stargate_document_v2.models.DocCollection
import com.datastax.astra.stargate_rest_v2.models.Keyspace
import com.datastax.astra.stargate_rest_v2.models.Table
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.testFramework.LightVirtualFile
import com.intellij.util.castSafelyTo
import com.jetbrains.rd.util.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import java.util.*

abstract class PagedVirtualFile :
    LightVirtualFile(){
    var jsonDocs = LinkedTreeMap<String, Any>()
    var pageFiles = mutableListOf<CharSequence>()
    var pageSize = 5
    var curPage = 0

    val gson = Serializer.gsonBuilder
        .setPrettyPrinting()
        .disableHtmlEscaping()
        //.enableComplexMapKeySerialization()
        .create()

    init {


    }

    //TODO: Make this a list of a custom object or something to keep returned values in order
    fun addData(responseMap: Any) {
        (responseMap as LinkedTreeMap<String, Any>).forEach { jsonDocs.put(it.key,it.value) }
    }


    fun buildFilesAndSet(){
        var nextMap = LinkedTreeMap<String, Any>()
        var index = 0
        for (jsonDoc in jsonDocs) {
            nextMap.put(jsonDoc)
            if(nextMap.size >= pageSize){
                pageFiles.add(index,
                        gson.toJson(nextMap)
                )

                nextMap.clear()
            }

        }
        setContent(this,pageFiles[0],true)
    }

    fun nextPage(){
        pageFiles[curPage++] = content
        setContent(null, pageFiles[curPage], true)
    }

}

class JsonDoc (
    val docId: String,
    val docJson: String
    ){
    override fun toString(): String {
        return "\"$docId\":$docJson"
    }
}
