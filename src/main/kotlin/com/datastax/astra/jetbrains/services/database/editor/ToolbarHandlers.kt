package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.explorer.CollectionNode
import com.datastax.astra.jetbrains.explorer.ExplorerNode
import com.datastax.astra.jetbrains.explorer.TableNode
import com.datastax.astra.jetbrains.services.database.editor.BreadcrumbsEx
import com.datastax.astra.jetbrains.services.database.editor.TableEditor
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.AstraIcons
import com.datastax.astra.jetbrains.utils.getCoroutineUiContext
import com.datastax.astra.stargate_document_v2.infrastructure.Serializer
import com.intellij.icons.AllIcons
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiManager
import com.intellij.ui.components.breadcrumbs.Breadcrumbs
import com.intellij.ui.components.breadcrumbs.Crumb
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayDeque

interface ToolbarHandler {
    fun changePage(page: Page)
    fun changePageSize(size: Int)
    fun changeWhereQuery(query: String)
}

abstract class ToolbarHandlerBase(private val fileEditor: FileEditor, node: ExplorerNode<String>) : ToolbarHandler {

    private val coroutineScope = ApplicationThreadPoolScope("ToolbarHandler")
    internal val edt = getCoroutineUiContext()

    private val cursors = ArrayDeque<String>()
    private val currentCursor: String?
        get() {
            return cursors.lastOrNull()
        }
    private var nextCursor: String? = null
    protected val pageNumber: Int
        get() {
            return cursors.size + 1
        }
    private var pageSize: Int
    abstract val pageSizes: List<Int>
    private var where = ""

    private val breadcrumbs =
        when(node) {
            is CollectionNode -> BreadcrumbsEx(
                node.database.info.name.orEmpty().ifEmpty { "[unnamed_db]" },
                node.keyspace.name,
                null,
                node.collection.name
            )
            is TableNode -> BreadcrumbsEx(
                node.endpoint.database.info.name.orEmpty().ifEmpty { "[unnamed_db]" },
                node.endpoint.keyspace.name,
                node.endpoint.table.name
            )
            else -> Breadcrumbs()
        }
    private val endpointToolbar by lazy { EndpointToolbar(this, pageSizes, breadcrumbs) }

    init {
        FileEditorManager.getInstance(node.nodeProject).addTopComponent(fileEditor, endpointToolbar)
        pageSize = endpointToolbar.pageSizeComboBox.selectedItem as Int
        coroutineScope.launch {
            nextCursor = fetch(pageSize, currentCursor, where)
            updatePaginatorUi()
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
            where = query
            nextCursor = fetch(pageSize, currentCursor, where)
            updatePaginatorUi()
        }
    }

    fun updatePaginatorUi() {
        endpointToolbar.apply {
            nextButton.isEnabled = (nextCursor != null)
            prevButton.isEnabled = (pageNumber > 1)
            pageLabel.text = pageNumber.toString()
        }
    }

    abstract suspend fun fetch(pageSize: Int, pageState: String?, where: String): String?

}

class CollectionHandler(val project: Project, val fileEditor: FileEditor, val node: CollectionNode) : ToolbarHandlerBase(fileEditor, node) {

    val gson = Serializer.gsonBuilder
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()

    override val pageSizes: List<Int>
        get() = listOf<Int>(1, 2, 5, 20)

    override suspend fun fetch(pageSize: Int, pageState: String?, where: String): String? {
        val response = AstraClient.getInstance(project).documentApiForDatabase(node.database).searchDoc(
            UUID.randomUUID(),
            AstraClient.getInstance(project).accessToken,
            node.keyspace.name,
            node.value.orEmpty(),
            pageSize = pageSize,
            pageState = pageState,
            where = where.ifEmpty { "{}" }
        )

        when (response.code()) {
            200 -> {
                fileEditor.file?.let {
                    withContext(edt) {
                        PsiManager.getInstance(node.nodeProject).findFile(it)?.let {
                            PsiDocumentManager.getInstance(node.nodeProject).getDocument(it)?.let {
                                runWriteAction { it.setText(gson.toJson(response.body()?.data)) }
                            }
                        }
                    }

                }
            }
            else -> {
                notifyLoadDocsError(node.database.info.name.orEmpty(),node.keyspace.name,node.collection.name,pageNumber,pageState.orEmpty(),where,Pair(response.toString(),response.getErrorResponse<Any?>().toString()))
            }
        }
        return response.body()?.pageState
    }
}

class TableHandler(val project: Project, private val tableEditor: TableEditor, val node: TableNode) : ToolbarHandlerBase(tableEditor, node) {

    override val pageSizes: List<Int>
        get() = listOf<Int>(20, 50, 100)

    override suspend fun fetch(pageSize: Int, pageState: String?, where: String): String? {

        val response = AstraClient.getInstance(project).dataApiForDatabase(node.endpoint.database).searchTable(
            AstraClient.getInstance(project).accessToken,
            node.endpoint.keyspace.name,
            node.endpoint.table.name.orEmpty(),
            pageState = pageState,
            pageSize = pageSize,
            where = where.ifEmpty { "{}" }
        )

        when (response.code()) {
            200 -> {
                response.body()?.data?.let {
                    withContext(edt) {
                        tableEditor.tableView.listTableModel.items = it
                    }
                }
            }
            else -> {
                notifyLoadRowsError(node.endpoint,pageNumber,pageState.orEmpty(),where,Pair(response.toString(),response.getErrorResponse<Any?>().toString()))
            }
        }
        return response.body()?.pageState
    }
}

/*
    suspend fun whereFieldInvalid(invalid: Boolean = true){
        withContext(edtContext) {
            toolbar.whereField.textEditor.border = if(invalid){
                BorderFactory.createLineBorder(Color.red,2)
            } else {
                BorderFactory.createEmptyBorder()
            }


        }
    }
 */
