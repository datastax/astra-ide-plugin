package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.utils.editor.PagedVirtualFile
import com.datastax.astra.jetbrains.utils.editor.VirtualFilePage
import com.datastax.astra.jetbrains.utils.editor.ui.EndpointTable
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.TableSpeedSearch
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ListTableModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.swing.JComponent
import javax.swing.JTable

class TableVirtualFile(val endpointTable: EndpointTable, model: ListTableModel<Map<String, String>>,) :
    PagedVirtualFile(endpointTable.table.name.toString(), null) {
    var allRows = mutableListOf<Any>()
    override var pages = mutableListOf<VirtualTablePage>()
    val component: JComponent
    val tableView: TableView<*>

    init {
        lock()
        pageSize = 20
        tableView = TableView<Map<String, String>>(model).apply {
            autoscrolls = true
            autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
            cellSelectionEnabled = true
            isFocusable = false
            setPaintBusy(true)
        }
        TableSpeedSearch(tableView)
        component = ScrollPaneFactory.createScrollPane(tableView)
    }

    override fun getSize() = allRows.size

    // TODO: Make this a list of a custom object or something to keep returned values in order
    override fun addData(responseMap: Any) {
        allRows.addAll((responseMap as ArrayList<*>))
    }

    override fun buildPagesAndSet() {
        launch {
            pages.clear()
            var buildingIndex = 0

            // If there are less rows than fit on a table this gets skipped
            while (pages.size < allRows.size / pageSize) {
                pages.add(buildingIndex, VirtualTablePage(allRows.subList(buildingIndex * pageSize, ((buildingIndex + 1) * pageSize) - 1), false))
                buildingIndex++
            }
            val remaining = (allRows.size % pageSize)
            if (remaining != 0) {
                pages.add(buildingIndex, VirtualTablePage(allRows.subList(buildingIndex * pageSize, (buildingIndex * pageSize) + remaining - 1), false))
            }
            pageIndex = 0
            withContext(edtContext) {
                tableView.stopEditing()
                tableView.listTableModel.items = pages[0].data
                tableView.tableViewModel.fireTableDataChanged()
                tableView.isFocusable = true
                tableView.setPaintBusy(false)
            }
        }
    }

    override fun setPage(currentPageStatus: Boolean, nextIndex: Int) {
        launch {
            pages[pageIndex].let { lastPage ->
                lastPage.data = tableView.items
                lastPage.hasError = currentPageStatus
            }
            pageIndex =
                if (nextIndex < 0) {
                    0
                } else if (nextIndex >= pages.size) {
                    pages.size - 1
                } else {
                    nextIndex
                }
            // Set contents of page to next page
            withContext(edtContext) {
                tableView.setPaintBusy(true)
                tableView.listTableModel.items = pages[pageIndex].data
                tableView.tableViewModel.fireTableDataChanged()
                tableView.setPaintBusy(false)
            }
        }
    }
}

data class VirtualTablePage(
    override var data: List<Any>,
    override var hasError: Boolean
) : VirtualFilePage(data, hasError)
