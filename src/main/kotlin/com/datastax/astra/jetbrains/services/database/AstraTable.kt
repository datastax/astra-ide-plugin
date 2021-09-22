package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.MessagesBundle
import com.datastax.astra.jetbrains.explorer.ExplorerDataKeys
import com.datastax.astra.jetbrains.explorer.TableEndpoint
import com.datastax.astra.jetbrains.explorer.TableNode
import com.datastax.astra.jetbrains.telemetry.CrudEnum
import com.datastax.astra.jetbrains.telemetry.TelemetryManager
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.SearchTextField
import com.intellij.ui.TableSpeedSearch
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ListTableModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jdesktop.swingx.combobox.ListComboBoxModel
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

class OpenTableAction : DumbAwareAction(MessagesBundle.message("table.open.title"), null, null) {
    override fun actionPerformed(e: AnActionEvent) {
        e.getData(ExplorerDataKeys.SELECTED_NODES)?.map { it as? TableNode }?.singleOrNull()?.run {
            openEditor(this.nodeProject, this.endpoint)
            TelemetryManager.trackStargateCrud("Table", this.endpoint.table.name!!, CrudEnum.READ, true)
        }
    }
}

class TableManager(
    val project: Project,
    val endpoint: TableEndpoint,
): CoroutineScope by ApplicationThreadPoolScope("Table") {
    protected val edtContext = getCoroutineUiContext()
    val tableUI = TableUI(endpoint,::changePage,::changePageSize,::changeWhereField)
    var pageSize = 10
    var whereFieldText = "{}"
    private val previousPages = mutableListOf<String>()
    private var nextPage = ""
    private var currentPage = ""

    init {

        launch {
            tableUI.setBusy()
            nextPage = fetchPageAndSet(AstraClient,currentPage)
                .also { tableUI.hasNext(it.isNotEmpty()) }
            tableUI.setBusy(false)
        }
    }

    fun changePage(page: Page){
        launch {
            tableUI.setBusy()
            when (page) {
                Page.NEXT -> {
                    previousPages.add(currentPage)
                    currentPage = nextPage
                    nextPage = fetchPageAndSet(AstraClient,currentPage,whereFieldText)
                        .also { tableUI.hasNext(it.isNotEmpty()) }
                }
                Page.PREVIOUS -> {
                    if(previousPages.isNotEmpty()) {
                        currentPage = previousPages.removeLast()
                        nextPage = fetchPageAndSet(AstraClient,currentPage,whereFieldText)
                            .also { tableUI.hasNext(it.isNotEmpty()) }
                    }
                    else{
                        //TODO: Ask garrett if this check should be here
                    }
                }
                //Page.CURRENT -> Refresh current page
            }
            tableUI.hasPrev(previousPages.isNotEmpty())
            tableUI.setBusy(false)
        }
    }

    fun changePageSize(newPageSize: Int){
        launch {
            tableUI.setBusy(true)
            previousPages.clear()
            tableUI.hasPrev(false)
            pageSize = newPageSize
            currentPage = ""
            nextPage = fetchPageAndSet(AstraClient,currentPage,whereFieldText)
                .also { tableUI.hasNext(it.isNotEmpty()) }
            tableUI.setBusy(false)
        }
    }

    fun changeWhereField(whereQuery: String){
        launch {
            tableUI.setBusy(true)
            previousPages.clear()
            tableUI.hasPrev(false)
            whereFieldText = whereQuery
            currentPage = ""
            nextPage = fetchPageAndSet(AstraClient,currentPage,whereFieldText)
                .also { tableUI.hasNext(it.isNotEmpty()) }
            tableUI.setBusy(false)
        }
    }

    suspend fun fetchPageAndSet(client: AstraClient, pageToken: String,whereQuery: String = ""): String {
        val response = client.dataApiForDatabase(endpoint.database).searchTable(
            client.accessToken,
            endpoint.table.keyspace.orEmpty(),
            endpoint.table.name.orEmpty(),
            pageState = pageToken.ifEmpty { null },
            pageSize = this.pageSize,
            where = whereQuery.ifEmpty { "{}" }
        )

        when (response.code()) {
            200 -> {
                if (response.body()?.data != null) {
                    setRows(response.body()?.data!!)
                    //prevPageState = response.body()?.pageState.orEmpty()
                }
            }
            400 -> { // Notify user: 400 Error and something not scary but useful, "Failed to load a page", etc
            }
            401 -> { // Notify user: 401 Error. Not authorized
            }
            else -> { // Notify user: Error Code. Error during retrieval, "Failed to load a page", etc
            }
        }

        return response.body()?.pageState.orEmpty()
    }



    suspend fun setRows(newRows: List<Map<String, String>>){
        tableUI.tableView.listTableModel.items = newRows
        withContext(edtContext) {
            tableUI.tableView.tableViewModel.fireTableDataChanged()
        }
    }
}

class TableUI(
    endpoint: TableEndpoint,
    changePage: (Page) -> Unit,
    changePageSize: (Int) -> Unit,
    changeWhereQuery: (String) -> Unit
){
    protected val edtContext = getCoroutineUiContext()
    val component: JComponent
    var prevAvailable = false
    var nextAvailable = false

    val tableView: TableView<*>
    val model = ListTableModel(
        endpoint.table.columnDefinitions?.map {
            // TODO: Map each TypeDefinition to the appropriate column info
            AstraColumnInfo(it.name)
        }.orEmpty().toTypedArray(),
        mutableListOf<Map<String, String>>(),
        -1,
        SortOrder.UNSORTED
    )

    val toolbar = JPanel(FlowLayout(FlowLayout.LEFT))

    val whereField = SearchTextField()
    val prevButton = JButton(AllIcons.Actions.ArrowCollapse)
    val nextButton = JButton(AllIcons.Actions.ArrowExpand)
    val pageSizeComboBox = ComboBox(PageSizeComboBox(changePageSize))
    val toolbarComponents: List<JComponent> = listOf(whereField,prevButton,nextButton,pageSizeComboBox)
    init {
        setUpWhereField(changeWhereQuery)
        //Set Up Table View
        //TODO: Paint for busy on creation?
        tableView = TableView<Map<String, String>>(model).apply {
            autoscrolls = true
            autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
            cellSelectionEnabled = true
            isFocusable = false
            setPaintBusy(true)
        }
        TableSpeedSearch(tableView)

        //Set Up Toolbar
        prevButton.addActionListener { changePage(Page.PREVIOUS) }
        nextButton.addActionListener { changePage(Page.NEXT) }
        toolbarComponents.forEach {
            toolbar.add(it)
        }

        component = JPanel(BorderLayout())
        component.add(ScrollPaneFactory.createScrollPane(tableView),BorderLayout.CENTER,)
        component.add(toolbar,BorderLayout.NORTH)
    }

    private fun setUpWhereField(changeWhereQuery: (String) -> Unit) {
        whereField.preferredSize= Dimension(300,whereField.preferredSize.height)

        //Do something in this state?
        //whereField.onEmpty {

        whereField
        whereField.onEnter {
            // If it is not empty do a search
            if (whereField.text.isNotEmpty()) {
                changeWhereQuery(whereField.text)
            }
            else {
                changeWhereQuery("{}")
            }
        }
    }


    suspend fun setBusy(busy: Boolean = true){
        if(busy){
            withContext(edtContext) {
                tableView.setPaintBusy(true)

                toolbar.isEnabled=false
                toolbarComponents.forEach {
                    it.isEnabled=false
                }
            }
        }
        else {
            withContext(edtContext) {
                tableView.setPaintBusy(false)

                whereField.isEnabled=true
                toolbar.isEnabled=true
                pageSizeComboBox.isEnabled=true
                prevButton.isEnabled=prevAvailable
                nextButton.isEnabled=nextAvailable
            }
        }
    }

    fun hasPrev(prevPageAvailable: Boolean){
        prevAvailable = prevPageAvailable
    }
    fun hasNext(nextPageAvailable: Boolean){
        nextAvailable = nextPageAvailable
    }

}

fun SearchTextField.onEnter(block: () -> Unit) {
    textEditor.addActionListener(
        object : ActionListener {
            private var lastText = ""
            override fun actionPerformed(e: ActionEvent?) {
                val searchFieldText = text.trim()
                if (searchFieldText == lastText) {
                    return
                }
                lastText = searchFieldText
                block()
            }
        }
    )
}

//TODO:
// Ask Garrett if these numbers are good, should we have a custom option?
// It can go up to
class PageSizeComboBox(
    changePageSize: (Int) -> Unit,
    val list: List<Int> = listOf(10,20,50,100,200)
) : ListComboBoxModel<Int>(list) {

    init {
        selectedItem = list.first()
        addListDataListener(object : ListDataListener {
            override fun intervalAdded(listDataEvent: ListDataEvent) {}
            override fun intervalRemoved(listDataEvent: ListDataEvent) {}
            override fun contentsChanged(listDataEvent: ListDataEvent) {
                if (selectedItem != null) {
                    changePageSize(selectedItem)
                }
            }
        })
    }
}


enum class Page(val nextPage: String) {
    PREVIOUS("Previous"),
    NEXT("Next"),
    CURRENT("Current")
}

