package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.editor.ui.EndpointTable
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.AppUIExecutor
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.impl.coroutineDispatchingContext
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.swing.SortOrder

class TableViewerPanel(
    val project: Project,
    val endpointTable: EndpointTable,
) : CoroutineScope by ApplicationThreadPoolScope("Table"), Disposable {
    lateinit var tableVirtualFile: TableVirtualFile
    protected val edtContext = getCoroutineUiContext(disposable = this)
    var prevPageState = ""

    init {

        val model = ListTableModel(
            endpointTable.table.columnDefinitions?.map {
                // TODO: Map each TypeDefinition to the appropriate column info
                AstraColumnInfo(it.name)
            }.orEmpty().toTypedArray(),
            mutableListOf<Map<String, String>>(),
            -1,
            SortOrder.UNSORTED
        )
        launch {
            tableVirtualFile = TableVirtualFile(endpointTable, model)
            loadFirstPage()
        }
    }

    private suspend fun loadFirstPage() {
        val responseData = (loadPage() as? ArrayList<*>).orEmpty()

        // TODO: Revisit the null safety of assigning the editor
        if (responseData.isNotEmpty()) {
            tableVirtualFile.addData(responseData)
            tableVirtualFile.buildPagesAndSet()

            // Make file read-only while we load the rest of it
            withContext(edtContext) {
                FileEditorManager.getInstance(project).openTextEditor(
                    OpenFileDescriptor(
                        project,
                        tableVirtualFile
                    ),
                    false
                )
                tableVirtualFile.tableView.isFocusable = false
                tableVirtualFile.tableView.setPaintBusy(true)
                // Use isFocusable as a flag to disable change table buttons,
                // Also makes it impossible to click table while the rest of it loads.
            }
            loadRemainingPages()
        } else {
            // TelemetryManager.trackStargateCrud("Collection", collection.name, CrudEnum.READ, false)
        }
    }

    fun loadRemainingPages() {
        // Keep doing this until the previous page state is empty again. Indicating all pages have loaded.
        // TODO: Add a timeout in case the server keeps sending the same page-state back
        launch {
            while (prevPageState.isNotEmpty()) {
                tableVirtualFile.addData((loadPage() as? ArrayList<*>).orEmpty())
            }
            tableVirtualFile.buildPagesAndSet()
            withContext(edtContext) {
                tableVirtualFile.unlock()
            }
        }
    }

    private suspend fun loadPage(): Any? {
        val response = AstraClient.dataApiForDatabase(endpointTable.database).searchTable(
            AstraClient.accessToken,
            endpointTable.table.keyspace.orEmpty(),
            endpointTable.table.name.orEmpty(),
            pageState = prevPageState.ifEmpty { null },
            where = "{}" // table?.primaryKey?.partitionKey?.joinToString("/").orEmpty()

        )

        when (response.code()) {
            200 -> {
                if (response.body()?.data != null) {
                    prevPageState = response.body()?.pageState.orEmpty()
                }
            }
            400 -> {
                // TODO:
                // Telemetry
                // Notify user: 400 Error and something not scary but useful, "Failed to load a page", etc
            }
            401 -> {}
            // TODO:
            // Telemetry
            // Notify user: 401 Error. Not authorized
            else -> {
                // TODO:
                // Telemetry
                // Notify user: Error Code. Error during retrieval, "Failed to load a page", etc
            }
        }
        return response.body()?.data
    }

    override fun dispose() {}
}

// TODO: How to show each type of data
class AstraColumnInfo(name: String) : ColumnInfo<Map<String, String>, String>(name) {

    override fun valueOf(item: Map<String, String>?): String? {
        return item?.get(name)
    }
}

fun getCoroutineUiContext(
    modalityState: ModalityState = ModalityState.defaultModalityState(),
    disposable: Disposable? = null
) = AppUIExecutor.onUiThread(modalityState).let {
    if (disposable == null) {
        it
    } else {
        // This is not actually scheduled for removal in 2019.3
        it.expireWith(disposable)
    }
}.coroutineDispatchingContext()
