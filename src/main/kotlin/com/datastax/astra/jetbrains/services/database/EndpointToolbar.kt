package com.datastax.astra.jetbrains.services.database

import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.impl.EditorHeaderComponent
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.SearchTextField
import org.jdesktop.swingx.combobox.ListComboBoxModel
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

class EndpointToolbar(
    changePage: (Page) -> Unit,
    changePageSize: (Int) -> Unit,
    changeWhereQuery: (String) -> Unit,
) : EditorHeaderComponent(){
    val whereField = SearchTextField()
    val prevButton = JButton(AllIcons.Actions.ArrowCollapse)
    val pageLabel = JLabel("1")
    val nextButton = JButton(AllIcons.Actions.ArrowExpand)
    val pageSizeComboBox = ComboBox(PageSizeComboBox(changePageSize))

    init{
        super.setLayout(FlowLayout(FlowLayout.LEFT,0, 0))
        setUpWhereField(changeWhereQuery)
        prevButton.addActionListener { changePage(Page.PREVIOUS) }
        nextButton.addActionListener { changePage(Page.NEXT) }
        add(whereField)
        add(prevButton)
        add(pageLabel)
        add(nextButton)
        add(pageSizeComboBox)


    }

    private fun setUpWhereField(changeWhereQuery: (String) -> Unit) {
        whereField.preferredSize= Dimension(300,whereField.preferredSize.height)

        whereField.onEmpty {
            //whereFieldInvalid(false)
        }

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

    fun SearchTextField.onEmpty(block: () -> Unit) {
        textEditor.addPropertyChangeListener(
            object : PropertyChangeListener {
                private var lastText = ""
                override fun propertyChange(evt: PropertyChangeEvent?) {
                    val searchFieldText = text.trim()
                    if (searchFieldText == lastText) {
                        return
                    }
                    lastText = searchFieldText
                    if (text.isEmpty()) {
                        block()
                    }
                }
            }
        )
    }

}
class PageSizeComboBox(
    changePageSize: (Int) -> Unit,
    val list: List<Int> = listOf(10, 20, 50, 100, 200),
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