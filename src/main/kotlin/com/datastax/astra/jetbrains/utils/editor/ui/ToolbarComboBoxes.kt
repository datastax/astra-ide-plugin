package com.datastax.astra.jetbrains.utils.editor.ui

import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.devops_v2.models.DatabaseInfo
import com.datastax.astra.devops_v2.models.StatusEnum
import com.datastax.astra.jetbrains.utils.AstraIcons
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import com.intellij.openapi.ui.ComboBox
import org.jdesktop.swingx.combobox.ListComboBoxModel
import java.awt.Component
import java.awt.GridLayout
import javax.swing.*
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener


class ToolbarComboBoxes(
    val databaseList: Map<String, SimpleDatabase>,
    var fileDatabaseId: String = "",
    var fileKeyspace: String = "",
    var fileCollection: String = "",
) {
    //Default instantiation. If all file[Value] given overwrite these below.
    //Ask Garrett if there's a better way to do this.
    var collectionComboBox = CollectionComboBox(mutableListOf(), "")
    var keyspaceComboBox = KeyspaceComboBox(mutableListOf(), "", collectionComboBox)
    var databaseComboBox = DatabaseComboBox(databaseList.values.toMutableList(), "", keyspaceComboBox)
    val wrapComboBoxList: List<ComboBox<Any>> = List(3) { ComboBox(ListComboBoxModel<Any>(emptyList())) }
    val iconList = listOf(AstraIcons.IntelliJ.Dbms, AstraIcons.IntelliJ.ColBlueKeyIndex, AllIcons.Nodes.WebFolder)
    val emptyDatabase = SimpleDatabase(Database("","","", DatabaseInfo("<Select Database>"),StatusEnum.ACTIVE,), mutableMapOf<String,SimpleKeyspace>())

    //TODO: Redo it so empty lists are drawn properly
    //TODO: Handle null for mutable list?
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


        }
    }


    //0: database, 1: keyspace, 2: collection
    fun getPanel(): JPanel {
        val jsonEditorComboBoxesPanel = JPanel(GridLayout())
        jsonEditorComboBoxesPanel.border = BorderFactory.createEmptyBorder(1, 2, 2, 2)
        wrapComboBoxList.forEachIndexed { i, cBox ->
            cBox.setMinimumAndPreferredWidth(160)
            when (i) {
                0 -> {
                    cBox.model = this.databaseComboBox;cBox.toolTipText = "Database"
                }
                1 -> {
                    cBox.model = this.keyspaceComboBox;cBox.toolTipText = "Keyspace"
                }
                2 -> {
                    cBox.model = this.collectionComboBox;cBox.toolTipText = "Collection"
                }
            }
            jsonEditorComboBoxesPanel.add(cBox, i)
        }
        return jsonEditorComboBoxesPanel
    }

    fun getSelected(): CBoxSelections {
        return CBoxSelections(
            databaseComboBox.selectedItem.database,
            keyspaceComboBox.selectedItem.keyspace.name,
            collectionComboBox.selectedItem
        )
    }
    fun anyNull(): Boolean{
        return if (databaseComboBox.selectedItem==null || keyspaceComboBox.selectedItem==null || collectionComboBox.selectedItem==null) true else false
    }
}



class DatabaseComboBox(
    var list: MutableList<SimpleDatabase>,
    val activeDatabase: String,
    val keyspaceComboBox: KeyspaceComboBox,
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
                    keyspaceComboBox.reload(list[list.indexOf(selectedItem)].keyspaces.map { it.value }
                        .toMutableList())
                    if (selectedItem.database.id != activeDatabase) {
                        // TODO: Indicate JSON is not part of the scheme for the selected collection
                        // TODO: Change Tooltip to have that thing
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
        } else {
            null
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
        } else {
            null
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
        } else {
            null
        }

        // we have to let components that bind to the model know that the model has been changed
        fireContentsChanged(this, -1, -1)
    }
}

internal class IconListRenderer(icons: Map<Any, Icon>?) :
    DefaultListCellRenderer() {
    private val icons: Map<Any, Icon>? = null
    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        val label = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus) as JLabel

        // Get icon to use for the list item value
        var icon: Icon? = icons!![value]
        //if (value.toString() != NONE_STR) {
        //    icon = icons[INFO_STR]
        //}

        // Set icon to display for value
        label.icon = icon
        return label
    }

    companion object {
        private const val serialVersionUID = 1L
    }

    init {
        //this.icons = icons
    }
}

data class CBoxSelections(
    var database: Database,
    var keyspace: String,
    var collection: String,
)
