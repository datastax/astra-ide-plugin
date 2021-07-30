package com.datastax.astra.jetbrains.utils.editor

import com.intellij.openapi.ui.ComboBox
import com.jetbrains.rd.util.first
import org.jdesktop.swingx.combobox.ListComboBoxModel
import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

class JsonEditorComboBoxes(
    val databaseList: Map<String, SimpleDatabase>,
    val fileDatabaseId: String = "",
    val fileKeyspace: String = "",
    val fileCollection: String = ""
) {
    var collectionComboBox: CollectionComboBox
    var keyspaceComboBox: KeyspaceComboBox
    var databaseComboBox: DatabaseComboBox

    //TODO: pass the file or information about the file to init the variables here instead of passing 3 parameters
    init {
        if (fileDatabaseId != "" && fileKeyspace != "" && fileCollection != "") {
            collectionComboBox =
                CollectionComboBox(databaseList[fileDatabaseId]?.keyspaces!![fileKeyspace]?.collections?.map { it.name }
                    ?.toMutableList()!!, fileCollection)
            keyspaceComboBox = KeyspaceComboBox(
                databaseList[fileDatabaseId]?.keyspaces?.values?.toMutableList()!!,
                fileKeyspace,
                collectionComboBox
            )
            databaseComboBox = DatabaseComboBox(databaseList.values.toMutableList(), fileDatabaseId, keyspaceComboBox)
        } else {
            collectionComboBox = CollectionComboBox(mutableListOf(), "")
            keyspaceComboBox = KeyspaceComboBox(mutableListOf(), "", collectionComboBox)
            databaseComboBox = DatabaseComboBox(databaseList.values.toMutableList(), "", keyspaceComboBox)
        }
    }


    fun getPanel(): JPanel {
        val jsonEditorComboBoxesPanel = JPanel(GridLayout())
        jsonEditorComboBoxesPanel.border = BorderFactory.createEmptyBorder(1, 2, 2, 2)
        val wrapDatabaseComboBox = ComboBox(this.databaseComboBox)
        wrapDatabaseComboBox.toolTipText = "Database"
        wrapDatabaseComboBox.setMinimumAndPreferredWidth(150)
        val wrapKeyspaceComboBox = ComboBox(this.keyspaceComboBox)
        wrapKeyspaceComboBox.toolTipText = "Keyspace"
        wrapKeyspaceComboBox.setMinimumAndPreferredWidth(150)
        val wrapCollectionComboBox = ComboBox(this.collectionComboBox)
        wrapCollectionComboBox.toolTipText = "Collection"
        wrapCollectionComboBox.setMinimumAndPreferredWidth(150)
        jsonEditorComboBoxesPanel.add(wrapDatabaseComboBox, 0)
        jsonEditorComboBoxesPanel.add(wrapKeyspaceComboBox, 1)
        jsonEditorComboBoxesPanel.add(wrapCollectionComboBox, 2)
        return jsonEditorComboBoxesPanel
    }

}


class DatabaseComboBox(
    var list: MutableList<SimpleDatabase>,
    val activeDatabase: String,
    val keyspaceComboBox: KeyspaceComboBox
) :
    ListComboBoxModel<SimpleDatabase>(list) {

    init {
        if (!list.isEmpty()) {
            selectedItem = if (activeDatabase != "") {
                list[list.indexOf(list.find { it.database.id == activeDatabase })]
            } else {
                null
            }
        }
        addListDataListener(object : ListDataListener {
            override fun intervalAdded(listDataEvent: ListDataEvent) {}
            override fun intervalRemoved(listDataEvent: ListDataEvent) {}
            override fun contentsChanged(listDataEvent: ListDataEvent) {

                if (selectedItem != null) {
                    keyspaceComboBox.reload(list[list.indexOf(selectedItem)].keyspaces.map { it.value }.toMutableList())
                    if(selectedItem.database.id != activeDatabase){
                        // TODO: Indicate JSON is not part of the scheme for the selected collection
                    }
                }
            }
        })
    }


    fun reload(databases: MutableList<SimpleDatabase>) {
        data.clear()
        if (databases != null) {
            data.addAll(databases)
        }

        selectedItem = if (!data.isEmpty()) {
            data[0]
        }
        else { null
        }

        // we have to let components that bind to the model know that the model has been changed
        fireContentsChanged(this, -1, -1)
    }
}

class KeyspaceComboBox(
    var list: MutableList<SimpleKeyspace>,
    val activeKeyspace: String,
    val collectionComboBox: CollectionComboBox
) :
    ListComboBoxModel<SimpleKeyspace>(list) {

    init {
        if (!list.isEmpty()) {
            selectedItem = if (activeKeyspace != "") {
                list[list.indexOf(list.find { it.keyspace?.name == activeKeyspace })]
            } else {
                null
            }
        }
        addListDataListener(object : ListDataListener {
            override fun intervalAdded(listDataEvent: ListDataEvent) {}
            override fun intervalRemoved(listDataEvent: ListDataEvent) {}
            override fun contentsChanged(listDataEvent: ListDataEvent) {
                val selectedItem: SimpleKeyspace = selectedItem
                if (selectedItem != null) {
                    collectionComboBox.reload(list[list.indexOf(selectedItem)].collections.map { it.name }
                        .toMutableList())
                }
            }
        })
    }

    fun reload(keyspaces: MutableList<SimpleKeyspace>) {
        data.clear()
        if (keyspaces != null) {
            data.addAll(keyspaces)
        }
        selectedItem = if (!data.isEmpty()) {
            data[0]
        }
        else { null
        }

        // we have to let components that bind to the model know that the model has been changed
        fireContentsChanged(this, -1, -1)
    }
}

class CollectionComboBox(var list: MutableList<String>, val activeCollection: String) :
    ListComboBoxModel<String>(list) {

    init {
        if (!list.isEmpty()) {
            selectedItem = if (activeCollection != "") {
                list[list.indexOf(list.find { it == activeCollection })]
            } else {
                null
            }
        }
        addListDataListener(object : ListDataListener {
            override fun intervalAdded(listDataEvent: ListDataEvent) {}
            override fun intervalRemoved(listDataEvent: ListDataEvent) {}
            override fun contentsChanged(listDataEvent: ListDataEvent) {
                val selectedItem: String = selectedItem
                if (selectedItem != null) {
                    //TODO: Underline red if not equal to activeCollection
                }
            }
        })
    }

    fun reload(collections: MutableList<String>) {
        data.clear()
        if (collections != null) {
            data.addAll(collections)
        }
        selectedItem = if (!data.isEmpty()) {
                data[0]
            }
         else { null
        }

        // we have to let components that bind to the model know that the model has been changed
        fireContentsChanged(this, -1, -1)
    }
}