package com.datastax.astra.jetbrains.utils.editor.ui

import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.devops_v2.models.DatabaseInfo
import com.datastax.astra.devops_v2.models.StatusEnum
import com.datastax.astra.jetbrains.utils.AstraIcons
import com.datastax.astra.stargate_document_v2.models.DocCollection
import com.datastax.astra.stargate_rest_v2.models.Keyspace
import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.util.messages.MessageBusConnection
import org.jdesktop.swingx.combobox.ListComboBoxModel
import java.awt.Component
import java.awt.GridLayout
import javax.swing.*
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener


class ToolbarComboBoxes(
    val project: Project,
    var selDatabaseId: String = "",
    var selKeyspace: String = "",
    var selCollection: String = "",
): Disposable, ProfileChangeEventListener {
    //Default instantiation. If all file[Value] given overwrite these below.
    //Ask Garrett if there's a better way to do this.
    var collectionComboBox = CollectionComboBox(mutableListOf(emptyDoc().name), selCollection)
    var keyspaceComboBox = KeyspaceComboBox(mutableListOf(emptySimpleKs()), selKeyspace, collectionComboBox)
    var databaseComboBox = DatabaseComboBox(mutableListOf(emptySimpleDb()), selDatabaseId, keyspaceComboBox)
    val wrapComboBoxList: List<ComboBox<Any>> = List(3) { ComboBox(ListComboBoxModel<Any>(emptyList())) }
    //val iconList = listOf(AstraIcons.IntelliJ.Dbms, AstraIcons.IntelliJ.ColBlueKeyIndex, AllIcons.Nodes.WebFolder)

    init {
        if (selDatabaseId != "") {
            databaseComboBox.reload(AstraFileEditorUIService.getService(project).enpoints())
        }

        val messageBusConnection: MessageBusConnection = project.getMessageBus().connect(this)
        // listen for configuration changes
        messageBusConnection.subscribe(ProfileChangeEventListener.TOPIC, this)
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

    fun setSelected(){
        selDatabaseId = databaseComboBox.selectedItem.database.id
        selKeyspace = keyspaceComboBox.selectedItem.keyspace.name
        selCollection = collectionComboBox.selectedItem
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

    override fun dispose() {

    }

    override fun reloadFileEditorUIResources(databaseMap: Map<String, SimpleDatabase>) {
        databaseComboBox.reload(databaseMap.map { it.value }.toMutableList())
    }

    override fun clearFileEditorUIResources() {
        databaseComboBox.reload(mutableListOf(emptySimpleDb()))
    }
}



class DatabaseComboBox(
    var list: MutableList<SimpleDatabase>,
    val activeDatabaseId: String,
    val keyspaceComboBox: KeyspaceComboBox,
) :
    ListComboBoxModel<SimpleDatabase>(list) {

    init {
        selectedItem=list.first()
        addListDataListener(object : ListDataListener {
            override fun intervalAdded(listDataEvent: ListDataEvent) {}
            override fun intervalRemoved(listDataEvent: ListDataEvent) {}
            override fun contentsChanged(listDataEvent: ListDataEvent) {
                if (selectedItem != null) {
                    keyspaceComboBox.reload(selectedItem.keyspaces.map { it.value }
                        .toMutableList())
                        // TODO: Indicate JSON is not part of the scheme for the selected collection
                        // TODO: Change Tooltip to have that thing
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

        data.clear()
        if(databases == null || databases.isEmpty()){
            data.add(emptySimpleDb())
            selectedItem = data[0]
        }
        else {
            data.addAll(databases)
            val selIndex = data.indexOfFirst{ it.database.id == activeDatabaseId }
            selectedItem =  if(selIndex >= 0){
                data[selIndex]
            } else {
                data[0]
            }
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
        selectedItem=list.first()
        addListDataListener(object : ListDataListener {
            override fun intervalAdded(listDataEvent: ListDataEvent) {}
            override fun intervalRemoved(listDataEvent: ListDataEvent) {}
            override fun contentsChanged(listDataEvent: ListDataEvent) {
                if (selectedItem != null) {
                    collectionComboBox.reload(selectedItem.collections.map{it.name}.toMutableList())
                }
            }
        })
    }

    fun reload(keyspaces: MutableList<SimpleKeyspace>) {
        data.clear()
        if(keyspaces == null || keyspaces.isEmpty()){
            data.add(emptySimpleKs())
            selectedItem = data[0]
        }
        else {
            data.addAll(sort(keyspaces))
            val selIndex = data.indexOfFirst{ it.keyspace.name == activeKeyspace }
            selectedItem =  if(selIndex >= 0){
                data[selIndex]
            } else {
                data[0]
                //Set the active keyspace to one with a collection
                //data.filter { it.collections.isNotEmpty() }.first()
            }
        }

        // we have to let components that bind to the model know that the model has been changed
        fireContentsChanged(this, -1, -1)
    }
    //Put the default keyspaces at the bottom so users don't have to look so far for them
    fun sort(keyspaces: MutableList<SimpleKeyspace>):MutableList<SimpleKeyspace> =
        (keyspaces.filter{!isAstraDefault(it.keyspace.name)} + (keyspaces.filter{isAstraDefault(it.keyspace.name)})).toMutableList()

    fun isAstraDefault(keyspaceName: String): Boolean=
        when(keyspaceName) {
            "data_endpoint_auth" -> true
            "datastax_sla" -> true
            "system_traces" -> true
            "system_auth" -> true
            "system_schema" -> true
            else -> false
        }
}

class CollectionComboBox(var list: MutableList<String>, val activeCollection: String) :
    ListComboBoxModel<String>(list) {

    init {
        selectedItem=list.first()
        addListDataListener(object : ListDataListener {
            override fun intervalAdded(listDataEvent: ListDataEvent) {}
            override fun intervalRemoved(listDataEvent: ListDataEvent) {}
            override fun contentsChanged(listDataEvent: ListDataEvent) {
                if (selectedItem != null) {
                    //TODO: Underline red if not equal to activeCollection
                }
            }
        })
    }

    fun reload(collections: MutableList<String>) {
        data.clear()
        if(collections == null || collections.isEmpty()){
            data.add(emptyDoc().name)
            selectedItem = data[0]
        }
        else {
            data.addAll(collections)
            val selIndex = data.indexOfFirst{ it == activeCollection }
            selectedItem = if(selIndex >= 0){
                data[selIndex]
            } else {
                data[0]
            }
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

fun emptySimpleDb() = SimpleDatabase(Database("", "", "", DatabaseInfo("<No Databases>"), StatusEnum.ACTIVE), mutableMapOf("<No Keyspaces>" to emptySimpleKs()))

fun emptySimpleKs() = SimpleKeyspace( Keyspace("<No Keyspaces>", emptyList()),mutableListOf(emptyDoc()))

fun emptyDoc() = DocCollection("<No Collections>")

data class CBoxSelections(
    var database: Database,
    var keyspace: String,
    var collection: String,
)

