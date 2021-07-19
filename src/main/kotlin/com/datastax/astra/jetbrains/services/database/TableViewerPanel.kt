package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.stargate_rest_v2.models.Table
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.AppUIExecutor
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.impl.coroutineDispatchingContext
import com.intellij.openapi.project.Project
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.TableSpeedSearch
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.swing.JComponent
import javax.swing.JTable
import javax.swing.SortOrder

class TableViewerPanel(
    disposable: Disposable,
    val project: Project,
    val table: Table,
    val database: Database
) : CoroutineScope by ApplicationThreadPoolScope("Table"), Disposable {
    val component: JComponent
    val tableView: TableView<*>

    protected val edtContext = getCoroutineUiContext(disposable = this)

    init {

        val model = ListTableModel(
            table.columnDefinitions?.map {
                // TODO: Map each TypeDefinition to the appropriate column info
                AstraColumnInfo(it.name)
            }.orEmpty().toTypedArray(),
            mutableListOf<Map<String, String>>(),
            -1,
            SortOrder.UNSORTED
        )

        tableView = TableView<Map<String, String>>(model).apply {
            autoscrolls = true
            autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
            cellSelectionEnabled = true
            setPaintBusy(true)
        }

        TableSpeedSearch(tableView)

        component = ScrollPaneFactory.createScrollPane(tableView)
        launch {
            loadInitialTableData()
        }
    }

    private suspend fun loadInitialTableData() {
        try {
            withContext(edtContext) {
                tableView.setPaintBusy(true)
            }
            val response = AstraClient.dataApiForDatabase(database).getRows(
                AstraClient.accessToken,
                table.keyspace.orEmpty(),
                table.name.orEmpty(),
                "rows" // table?.primaryKey?.partitionKey?.joinToString("/").orEmpty()
            )
            if (response.isSuccessful) {
                val rows = response.body()?.data
                if (rows != null) {
                    withContext(edtContext) {
                        tableView.listTableModel.items = rows
                    }
                }
            }
        } finally {
            withContext(edtContext) {
                tableView.tableViewModel.fireTableDataChanged()
                tableView.setPaintBusy(false)
            }
        }
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
