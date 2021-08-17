package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.editor.PagedVirtualFile
import com.datastax.astra.jetbrains.utils.editor.VirtualFilePage
import com.datastax.astra.jetbrains.utils.editor.ui.EndpointInfo
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

class CollectionPagedVirtualFile(var collections: LinkedTreeMap<String, Any>, var endpointInfo: EndpointInfo, var setPageSize: Int = 10) :
    PagedVirtualFile(endpointInfo.collection,FileTypeManager.getInstance().getFileTypeByExtension("JSON"),setPageSize){
    var jsonDocs = LinkedTreeMap<String, Any>()

    val gson = Serializer.gsonBuilder
        .setPrettyPrinting()
        .disableHtmlEscaping()
        //.enableComplexMapKeySerialization()
        .create()

    init {
        buildFilesAndSet()
    }

    //TODO: Make this a list of a custom object or something to keep returned values in order
    override fun addData(responseMap: Any) {
        (responseMap as LinkedTreeMap<String, Any>).forEach { jsonDocs.put(it.key,it.value) }
    }

    override fun buildFilesAndSet(){
        var nextMap = LinkedTreeMap<String, Any>()
        var index = 0
        for (jsonDoc in collections) {
            nextMap.put(jsonDoc)
            if(nextMap.size >= pageSize){
                pages.add(index++,
                        VirtualFilePage(gson.toJson(nextMap),false)
                )
                nextMap.clear()
            }

        }
        setContent(this,pages[0].data,true)
    }

}