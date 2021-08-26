package com.datastax.astra.jetbrains.utils.editor

import com.datastax.astra.jetbrains.services.database.getCoroutineUiContext
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.openapi.fileTypes.FileType
import com.intellij.testFramework.LightVirtualFile
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.atomic.AtomicBoolean

abstract class PagedVirtualFile(val fileName: String, val pagedFileType: FileType?, var pageSize: Int = 10) :
    LightVirtualFile(fileName, pagedFileType, ""), CoroutineScope by ApplicationThreadPoolScope("FileEditorUIService") {
    abstract val pages: List<VirtualFilePage>
    var pageIndex = 0
    var lockedForUpdate = AtomicBoolean(true)
    protected val edtContext = getCoroutineUiContext()

    fun unlock() {
        lockedForUpdate.set(false)
    }

    fun lock() {
        lockedForUpdate.set(true)
    }

    fun isLocked() = lockedForUpdate.get()

    abstract fun addData(responseMap: Any)

    abstract fun buildPagesAndSet()

    fun nextPage(currentPageStatus: Boolean) {
        // forward-cycle through pages
        setPage(currentPageStatus, (pageIndex + 1) % pages.size)
    }

    fun prevPage(currentPageStatus: Boolean) {
        // back-cycle through pages
        setPage(currentPageStatus, ((pageIndex - 1) + pages.size) % pages.size)
    }

    abstract fun setPage(currentPageStatus: Boolean, nextIndex: Int)

    fun setVirtualPageSize(newPageSize: Int) {
        pageSize = newPageSize
        buildPagesAndSet()
    }

    abstract fun getSize(): Int

    fun noErrors(): Boolean = pages.none { it.hasError }
}

abstract class VirtualFilePage(
    open val data: Any,
    open var hasError: Boolean
)
