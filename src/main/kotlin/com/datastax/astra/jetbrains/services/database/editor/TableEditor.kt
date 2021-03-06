package com.datastax.astra.jetbrains.services.database.editor

import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.explorer.TableEndpoint
import com.datastax.astra.jetbrains.services.database.notifyUpdateRowError
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.AstraIcons
import com.datastax.astra.jetbrains.utils.getCoroutineUiContext
import com.datastax.astra.stargate_rest_v2.models.InlineResponse2004
import com.datastax.astra.stargate_rest_v2.models.Table
import com.intellij.icons.AllIcons
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.TableSpeedSearch
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.awt.Color
import java.awt.Component
import java.beans.PropertyChangeListener
import javax.swing.DefaultCellEditor
import javax.swing.JComponent
import javax.swing.JTable
import javax.swing.SortOrder
import javax.swing.border.BevelBorder
import javax.swing.border.Border
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

class TableEditor(private val project: Project, tableVirtualFile: TableVirtualFile) : UserDataHolderBase(), FileEditor {

    val tableView: TableView<Map<String, String>>
    private val tableModel: ListTableModel<Map<String, String>>
    private val component: JComponent

    init {
        tableModel = ListTableModel<Map<String, String>>(
            tableVirtualFile.endpoint.table.columnDefinitions?.map {
                AstraColumnInfo(project, it.name, tableVirtualFile.endpoint)
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

    override fun getFile(): VirtualFile {
        return FileEditor.FILE_KEY[this]
    }
}

class AstraColumnInfo(private val project: Project, name: String, val endpoint: TableEndpoint) : ColumnInfo<MutableMap<String, String>, String>(name),
    CoroutineScope by ApplicationThreadPoolScope("Table") {
    internal val edt = getCoroutineUiContext()

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
                    notifyUpdateRowError(endpoint.table.name.orEmpty(),keys,name,item[name].orEmpty(),value,Pair(response.toString(),response.getErrorResponse<Any?>().toString()))
                }
            }

        }
    }

    suspend fun updateRemoteTable(rowKey: String, columnName: String, newValue: String): Response<InlineResponse2004> {
        return AstraClient.getInstance(project).dataApiForDatabase(endpoint.database).updateRows(
            AstraClient.getInstance(project).accessToken,
            endpoint.table.keyspace.orEmpty(),
            endpoint.table.name.orEmpty(),
            rowKey,
            mapOf(columnName to newValue)
        )
    }

    override fun valueOf(item: MutableMap<String, String>?): String? {
        return item?.get(name)
    }

    override fun getRenderer(item: MutableMap<String, String>?): TableCellRenderer? {
        val renderer = super.getCustomizedRenderer(item,DefaultTableCellRenderer())
        if(isKeyColumn){
            (renderer as DefaultTableCellRenderer).let {
                //TODO: Ask Garrett is something else will replace this indicator
                it.disabledIcon = AstraIcons.IntelliJ.GoldKeyAlt
                it.isEnabled=false
            }
        }
        else{
            (renderer as DefaultTableCellRenderer).let {
                //TODO: Ask Garrett is something else will replace this indicator
                it.disabledIcon = AllIcons.Actions.Refresh
            }
        }
        return renderer
    }

}

fun getRowKeys(item: MutableMap<String, String>,table: Table): String {
    var compositeKey = ""
    if(table.primaryKey != null){
        //for(key in table.primaryKey.partitionKey){
        //    compositeKey += (item[key] + "/")
        //}
        compositeKey = item[table.primaryKey.partitionKey.first()].toString()
    }
    return compositeKey //.trimEnd('/')

}