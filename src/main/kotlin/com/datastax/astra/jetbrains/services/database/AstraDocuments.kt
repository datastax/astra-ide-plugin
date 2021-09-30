package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.explorer.CollectionNode
import com.datastax.astra.jetbrains.explorer.TableEndpoint
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayDeque

interface ToolbarHandler {
    fun changePage(page: Page)
    fun changePageSize(size: Int)
    fun changeWhereQuery(query: String)
}

abstract class ToolbarHandlerBase : ToolbarHandler {

    private val coroutineScope = ApplicationThreadPoolScope("ToolbarHandler")
    private val cursors = ArrayDeque<String>()
    private val currentCursor: String?
        get() {
            return cursors.lastOrNull()
        }
    private var nextCursor: String? = null
    private val pageNumber: Int
        get() {
            return cursors.size + 1
        }
    private var pageSize = 20
    private var where = ""

    init {
        /*
        TODO: UI Stuff
            Disable page buttons
            Clear where query search box
            Set combobox for page size

         */
        coroutineScope.launch {
            nextCursor = fetch(pageSize, currentCursor, where)
            //Reconcile next
        }
    }

    override fun changePage(page: Page) {
        coroutineScope.launch {
            when (page) {
                Page.PREVIOUS -> {
                    val dirtyCursor = cursors.removeLast()
                    try {
                        nextCursor = fetch(pageSize, currentCursor, where)
                        updatePaginatorUi()
                    } catch (e: Exception) {
                        //Didn't go back. Stayed on same page
                        cursors.add(dirtyCursor)
                        throw e
                    }
                }
                Page.NEXT -> {
                    try {
                        cursors.add(nextCursor!!)
                        nextCursor = fetch(pageSize, currentCursor, where)
                        updatePaginatorUi()

                    } catch (e: Exception) {
                        //Didn't advance. Stayed on same page
                        nextCursor = cursors.removeLast()
                        throw e
                    }
                }
            }
        }
    }

    override fun changePageSize(size: Int) {
        coroutineScope.launch {
            cursors.clear()
            nextCursor = null
            pageSize = size
            nextCursor = fetch(pageSize, currentCursor, where)
            updatePaginatorUi()
        }
    }

    override fun changeWhereQuery(query: String) {
        coroutineScope.launch {
            cursors.clear()
            nextCursor = null
            where = where
            nextCursor = fetch(pageSize, currentCursor, where)
            updatePaginatorUi()
        }
    }

    fun updatePaginatorUi() {
        if (nextCursor != null) {
            //TODO: Enable Next Button
        } else {
            //TODO: Disable Next Button
        }
        if (pageNumber > 1) {
            //TODO: Enable Previous Button
        } else {
            //TODO: Disable Previous Button
        }
        //TODO: paginatorUI.setPageNumber(pageNumber.toString())
    }

    abstract suspend fun fetch(pageSize: Int, pageState: String?, where: Any?): String?

}

class DocumentHandler(val collectionNode: CollectionNode) : ToolbarHandlerBase() {

    override suspend fun fetch(pageSize: Int, pageState: String?, where: Any?): String? {
        val response = AstraClient.documentApiForDatabase(collectionNode.database).searchDoc(
            UUID.randomUUID(),
            AstraClient.accessToken,
            collectionNode.keyspace.name,
            collectionNode.name.orEmpty(),
            pageSize = pageSize,
            pageState = pageState,
            where = where
        )

        response.body()?.data //Any? Need to check json array size to derive count
        return response.body()?.pageState //String?
    }
}

class TableHandler(val tableEndpoint: TableEndpoint) : ToolbarHandlerBase() {

    override suspend fun fetch(pageSize: Int, pageState: String?, where: Any?): String? {
        val response = AstraClient.dataApiForDatabase(tableEndpoint.database).searchTable(
            AstraClient.accessToken,
            tableEndpoint.keyspace.name,
            tableEndpoint.table.name.orEmpty(),
            pageState = pageState,
            pageSize = pageSize,
            where = where
        )

        response.body()?.count //Int?

        response.body()?.data //List<Map<String,String>>?
        return response.body()?.pageState //String?
    }
}
