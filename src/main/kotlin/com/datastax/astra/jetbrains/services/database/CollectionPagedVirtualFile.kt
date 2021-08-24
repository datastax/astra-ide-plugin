package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.utils.editor.PagedVirtualFile
import com.datastax.astra.jetbrains.utils.editor.VirtualFilePage
import com.datastax.astra.jetbrains.utils.editor.ui.EndpointCollection
import com.datastax.astra.stargate_document_v2.infrastructure.Serializer
import com.google.gson.internal.LinkedTreeMap
import com.intellij.openapi.fileTypes.FileTypeManager
import com.jetbrains.rd.util.put

class CollectionPagedVirtualFile(var endpointInfo: EndpointCollection, var setPageSize: Int = 10) :
    PagedVirtualFile(endpointInfo.collection,FileTypeManager.getInstance().getFileTypeByExtension("JSON"),setPageSize){
    var jsonDocs = LinkedTreeMap<String, Any>()

    val gson = Serializer.gsonBuilder
        .setPrettyPrinting()
        .disableHtmlEscaping()
        //.enableComplexMapKeySerialization()
        .create()

    init {
    }

    //TODO: Make this a list of a custom object or something to keep returned values in order
    override fun addData(responseMap: Any) {
        try {
            (responseMap as LinkedTreeMap<*, *>).forEach { jsonDocs[it.key as String] = it.value }
        }
        catch (exception: Exception){
            //TODO: Ask Garrett what to do about this or if doing it unsafely is actually 'safe'
        }
    }

    override fun buildPagesAndSet(){
        pages.clear()
        var nextMap = LinkedTreeMap<String, Any>()
        var pageIndex = 0
        //Either have to index it, or transform it and then iterate, or add the incomplete page after for loop
        var loopIndex = 0
        for (jsonDoc in jsonDocs) {
            nextMap.put(jsonDoc)
            if(nextMap.size >= pageSize || loopIndex==(jsonDocs.size-1)){
                pages.add(pageIndex++, VirtualFilePage(gson.toJson(nextMap),false))
                nextMap.clear()
            }
            loopIndex++
        }

        setContent(this,pages[0].data,true)
        updatePageCount()
    }

}