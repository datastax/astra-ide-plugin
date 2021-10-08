package com.datastax.astra.jetbrains.services.database.editor

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.TableSpeedSearch
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import java.beans.PropertyChangeListener
import javax.swing.JComponent
import javax.swing.JTable
import javax.swing.SortOrder

class TableEditor(tableVirtualFile: TableVirtualFile) : UserDataHolderBase(), FileEditor {

    val tableView: TableView<Map<String,String>>
    private val tableModel: ListTableModel<Map<String,String>>
    private val component: JComponent

    init {
        tableModel = ListTableModel<Map<String,String>>(
            tableVirtualFile.endpoint.table.columnDefinitions?.map {
                // TODO: Map each TypeDefinition to the appropriate column info
                AstraColumnInfo(it.name)
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

class AstraColumnInfo(name: String) : ColumnInfo<Map<String, String>, String>(name) {

    override fun valueOf(item: Map<String, String>?): String? {
        return item?.get(name)
    }
}
