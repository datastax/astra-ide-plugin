package com.datastax.astra.jetbrains.services.database.editor

import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.explorer.TableEndpoint
import com.datastax.astra.jetbrains.services.database.failedRowUpdateNotification
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.stargate_rest_v2.models.InlineResponse2004
import com.datastax.astra.stargate_rest_v2.models.Table
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.TableSpeedSearch
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Response
import java.beans.PropertyChangeListener
import javax.swing.JComponent
import javax.swing.JTable
import javax.swing.SortOrder

class TableEditor(tableVirtualFile: TableVirtualFile) : UserDataHolderBase(), FileEditor {

    val tableView: TableView<Map<String, String>>
    private val tableModel: ListTableModel<Map<String, String>>
    private val component: JComponent

    init {
        tableModel = ListTableModel<Map<String, String>>(
            tableVirtualFile.endpoint.table.columnDefinitions?.map {
                // TODO: Map each TypeDefinition to the appropriate column info
                AstraColumnInfo(it.name, tableVirtualFile.endpoint)
            }.orEmpty().toTypedArray(),
            listOf<MutableMap<String, String>>(),
            -1,
            SortOrder.UNSORTED
        )
        tableView = TableView(tableModel).apply {
            autoscrolls = true
            autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
            cellSelectionEnabled = true
            isFocusable = false
        }
        TableSpeedSearch(tableView)

        /*component = JPanel(BorderLayout())
        component.add(ScrollPaneFactory.createScrollPane(tableView), BorderLayout.CENTER)
         */
        component = ScrollPaneFactory.createScrollPane(tableView)
    }

    override fun dispose() {}

    override fun getComponent() = component

    override fun getPreferredFocusedComponent() = component

    override fun getName() = "Table Panel"

    override fun setState(state: FileEditorState) {}

    override fun isModified(): Boolean = false

    override fun isValid(): Boolean = true

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {}

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {}

    override fun getCurrentLocation(): FileEditorLocation? = null
}

class AstraColumnInfo(name: String, val endpoint: TableEndpoint) : ColumnInfo<MutableMap<String, String>, String>(name),
    CoroutineScope by ApplicationThreadPoolScope("Mine") {

    //Don't allow editing any column until we're sure it's not in that list.
    val isKeyColumn: Boolean

    init {
        if (endpoint.table.primaryKey == null) {
            isKeyColumn = false
        } else {
            endpoint.table.primaryKey.let {
                isKeyColumn = if (it.partitionKey.contains(name)) {
                    true
                } else it.clusteringKey != null && it.clusteringKey.contains(name)
            }
        }
    }

    override fun isCellEditable(item: MutableMap<String, String>): Boolean {
        return !isKeyColumn
    }

    override fun setValue(item: MutableMap<String, String>, value: String) {
        if (item[name] != value) {
            launch {
                val keys = getRowKeys(item,endpoint.table)
                val response = updateRemoteTable(keys, name, value)
                if (response.isSuccessful) {
                    item[name] = value
                }else{
                    failedRowUpdateNotification(endpoint.table.name.orEmpty(),keys,name,item[name].orEmpty(),value,Pair(response.toString(),response.getErrorResponse<Any?>().toString()))
                }
            }

        }
    }

    suspend fun updateRemoteTable(rowKey: String, columnName: String, newValue: String): Response<InlineResponse2004> {
        return AstraClient.dataApiForDatabase(endpoint.database).updateRows(
            AstraClient.accessToken,
            endpoint.table.keyspace.orEmpty(),
            endpoint.table.name.orEmpty(),
            rowKey,
            mapOf(columnName to newValue)
        )
    }




    override fun valueOf(item: MutableMap<String, String>?): String? {
        return item?.get(name)
    }
}

fun getRowKeys(item: MutableMap<String, String>,table: Table): String {
    val primaryKey = table.primaryKey!!.partitionKey.first()
    return item[primaryKey].orEmpty()
}
