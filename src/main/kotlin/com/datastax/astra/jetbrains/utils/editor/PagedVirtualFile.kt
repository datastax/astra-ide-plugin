package com.datastax.astra.jetbrains.utils.editor

import com.intellij.openapi.fileTypes.FileType
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import kotlin.math.abs

abstract class PagedVirtualFile(val fileName: String, val pagedFileType: FileType, var pageSize: Int = 20) :
    LightVirtualFile(fileName,pagedFileType,""){
    var pages = mutableListOf<VirtualFilePage>()
    var pageIndex = 0
    var pageIndexComponent: JBTextField? = null
    var pageCountComponent: JBLabel? = null


    abstract fun addData(responseMap: Any)

    abstract fun buildPagesAndSet()

    fun updatePageCount(){
        if(pageCountComponent != null){
            pageCountComponent!!.text = "of ${pages.size}"
        }
    }

    fun nextPage(errorOnCurrentPage: Boolean){
        //forward-cycle through pages
        setPage(errorOnCurrentPage,(pageIndex+1)%pages.size)
    }

    fun prevPage(errorOnCurrentPage: Boolean){
        //back-cycle through pages
        setPage(errorOnCurrentPage,((pageIndex-1)+pages.size)%pages.size)
    }

    fun setPage(errorOnCurrentPage: Boolean, nextIndex: Int) {
        //Save the current page state
        pages[pageIndex].let { lastPage ->
            lastPage.data = content
            lastPage.hasError = errorOnCurrentPage
        }
        pageIndex =
            if (nextIndex < 0) {
                0
            } else if (nextIndex >= pages.size) {
                pages.size - 1
            } else {
                nextIndex
            }
        //Set contents of page to next page
        setContent(null, pages[pageIndex].data, true)

        if(pageIndexComponent != null){
            pageIndexComponent!!.text = "${pageIndex+1}"
        }
    }

    fun setRemoteLabels(indexLabel: JBTextField, countLabel: JBLabel){
        pageIndexComponent = indexLabel
        pageCountComponent = countLabel
    }

    fun noErrors(): Boolean = pages.none{ it.hasError }
}
data class VirtualFilePage(
    var data: CharSequence,
    var hasError: Boolean
)