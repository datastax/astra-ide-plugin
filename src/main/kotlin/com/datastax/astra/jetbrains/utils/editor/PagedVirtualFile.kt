package com.datastax.astra.jetbrains.utils.editor

import com.intellij.openapi.fileTypes.FileType

import com.intellij.testFramework.LightVirtualFile
import kotlin.math.abs

abstract class PagedVirtualFile(val fileName: String, val pagedFileType: FileType, var pageSize: Int = 20) :
    LightVirtualFile(fileName,pagedFileType,""){
    var pages = mutableListOf<VirtualFilePage>()
    var pageIndex = 0

    abstract fun addData(responseMap: Any)

    abstract fun buildFilesAndSet()

     fun nextPage(errorOnCurrentPage: Boolean){
        //forward-cycle through pages
        gotToPage(errorOnCurrentPage,(pageIndex+1)%pages.size)
    }

    fun prevPage(errorOnCurrentPage: Boolean){
        //back-cycle through pages
        gotToPage(errorOnCurrentPage,(abs(pageIndex-1))%pages.size)
    }

    fun gotToPage(errorOnCurrentPage: Boolean, nextIndex: Int) {
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
    }

    fun noErrors(): Boolean = pages.none{ it.hasError }

}
data class VirtualFilePage(
    var data: CharSequence,
    var hasError: Boolean
)