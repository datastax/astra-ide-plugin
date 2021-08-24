package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.utils.editor.ui.EndpointTable
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.TableSpeedSearch
import java.util.*
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ListTableModel
import kotlinx.coroutines.withContext
import javax.swing.JComponent
import javax.swing.JTable

class TablePagedVirtualFile(val endpointTable: EndpointTable, model: ListTableModel<Map<String, String>>,) :
    LightVirtualFile(endpointTable.table.name.toString()){

    var pageSize = 10
    var allRows = mutableListOf<Any>()
    var pages = mutableListOf<VirtualTablePage>()
    var pageIndex = 0
    val component: JComponent
    val tableView: TableView<*>
    protected val edtContext = getCoroutineUiContext()

    init {
        tableView = TableView<Map<String, String>>(model).apply {
            autoscrolls = true
            autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
            cellSelectionEnabled = true
            isFocusable=false
            setPaintBusy(true)
        }
        TableSpeedSearch(tableView)
        component = ScrollPaneFactory.createScrollPane(tableView)
    }

    //TODO: Make this a list of a custom object or something to keep returned values in order
    fun addData(responseList: Any) {
        allRows.addAll((responseList as ArrayList<*>))
    }

    suspend fun buildPagesAndSet(){
        pages.clear()
        var pageIndex = 0

        //If there are less rows than fit on a table this gets skipped
        while (pages.size < allRows.size/pageSize) {
            pages.add(pageIndex,VirtualTablePage(allRows.subList(pageIndex*pageSize,((pageIndex+1)*pageSize)-1),false))
            pageIndex++
        }
        val remaining = (allRows.size%pageSize)
        if (remaining != 0) {
            pages.add(pageIndex,VirtualTablePage(allRows.subList(pageIndex*pageSize,(pageIndex*pageSize)+remaining-1),false))

        }
        this.pageIndex = 0


        withContext(edtContext) {
            tableView.stopEditing()
            tableView.listTableModel.items = pages[0].data
            tableView.tableViewModel.fireTableDataChanged()
            tableView.isFocusable=true
            tableView.setPaintBusy(false)
        }

    }

    suspend fun nextPage(errorOnCurrentPage: Boolean){
        //forward-cycle through pages
        setPage(errorOnCurrentPage,(pageIndex+1)%pages.size)
    }

    suspend fun prevPage(errorOnCurrentPage: Boolean){
        //back-cycle through pages
        setPage(errorOnCurrentPage,((pageIndex-1)+pages.size)%pages.size)
    }

    suspend fun setPage(errorOnCurrentPage: Boolean, nextIndex: Int) {
        pages[pageIndex].let { lastPage ->
            lastPage.data = tableView.items
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
        withContext(edtContext) {
            tableView.setPaintBusy(true)
            tableView.listTableModel.items = pages[pageIndex].data
            tableView.tableViewModel.fireTableDataChanged()
            tableView.setPaintBusy(false)
        }
    }


}

data class VirtualTablePage(
    var data: List<Any>,
    var hasError: Boolean
)