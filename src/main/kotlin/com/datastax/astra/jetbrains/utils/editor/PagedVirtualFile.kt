package com.datastax.astra.jetbrains.utils.editor

import com.datastax.astra.stargate_document_v2.infrastructure.Serializer
import com.google.gson.internal.LinkedTreeMap
import com.intellij.testFramework.LightVirtualFile
import com.jetbrains.rd.util.put
import java.util.*
import kotlin.math.abs

abstract class PagedVirtualFile(var pageSize: Int = 20) :
    LightVirtualFile(){
    var pageList = mutableListOf<VirtualPage>()
    var pageCount = 0
    var pageIndex = 0


    val gson = Serializer.gsonBuilder
        .setPrettyPrinting()
        .disableHtmlEscaping()
        //.enableComplexMapKeySerialization()
        .create()

    init {


    }

    //TODO: Make this a list of a custom object or something to keep returned values in order
    fun addData(responseMap: Any) {
       // (responseMap as LinkedTreeMap<String, Any>).forEach { jsonDocs.put(it.key,it.value) }
    }

    fun buildFilesAndSet(){

    }

    fun prevPage(){

    }

    fun nextPage(errorOnCurrentPage: Boolean){
        //forward-cycle through pages
        gotToPage(errorOnCurrentPage,(pageIndex+1)%pageCount)
    }

    fun prevPage(errorOnCurrentPage: Boolean){
        //back-cycle through pages
        gotToPage(errorOnCurrentPage,(abs(pageIndex-1))%pageCount)
    }

    fun gotToPage(errorOnCurrentPage: Boolean, pageNumber: Int) {
        //Save the current page state
        pageList[pageIndex].let { lastPage ->
            lastPage.data = content
            lastPage.hasError = errorOnCurrentPage
        }
        pageIndex =
            if (pageNumber < 1) {
                0
            } else if (pageNumber > pageCount) {
                pageCount - 1
            } else {
                pageNumber - 1
            }
        //Set contents of page to next page
        setContent(null, pageList[pageIndex].data, true)
    }

    fun noErrors(): Boolean = pageList.none{ it.hasError }

}

class JsonDoc (
    val docId: String,
    val docJson: String
    ){
    override fun toString(): String {
        return "\"$docId\":$docJson"
    }
}

data class VirtualPage(
    var data: CharSequence,
    var hasError: Boolean
)