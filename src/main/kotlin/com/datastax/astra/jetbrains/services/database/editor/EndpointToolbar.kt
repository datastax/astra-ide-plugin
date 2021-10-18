package com.datastax.astra.jetbrains.services.database

import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.impl.EditorHeaderComponent
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.SearchTextField
import com.intellij.ui.components.breadcrumbs.Breadcrumbs
import com.intellij.ui.components.breadcrumbs.Crumb
import org.jdesktop.swingx.combobox.ListComboBoxModel
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

class EndpointToolbar(handler: ToolbarHandler, pageSizes: List<Int>, breadcrumbs: Breadcrumbs) : EditorHeaderComponent(){
    val whereField = SearchTextField()
    val prevButton = JButton(AllIcons.Actions.ArrowCollapse)
    val pageLabel = JLabel("1")
    val nextButton = JButton(AllIcons.Actions.ArrowExpand)
    val pageSizeComboBox = ComboBox(PageSizeComboBox(handler::changePageSize, pageSizes))

    init{
        setTooltips()
        pageLabel.border = BorderFactory.createEmptyBorder(0, 1, 0, 2)
        super.setLayout(FlowLayout(FlowLayout.LEFT,1, 0))
        //breadcrumbs.maximumSize=Dimension(160  ,28)
        setUpWhereField(handler::changeWhereQuery)
        prevButton.addActionListener { handler.changePage(Page.PREVIOUS) }

        nextButton.addActionListener { handler.changePage(Page.NEXT) }


        add(breadcrumbs)
        add(whereField)
        add(prevButton)
        add(pageLabel)
        add(nextButton)
        add(pageSizeComboBox)


    }

    private fun setTooltips(){
        whereField.textEditor.toolTipText = "Search with Where Query"
        prevButton.toolTipText = "Previous Page"
        pageLabel.toolTipText = "Current Page"
        nextButton.toolTipText = "Next Page"
        pageSizeComboBox.toolTipText = "Change Page Size"
    }

    private fun setUpWhereField(changeWhereQuery: (String) -> Unit) {
        whereField.preferredSize= Dimension(250,whereField.preferredSize.height)

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
    val pageSizes: List<Int>
) : ListComboBoxModel<Int>(pageSizes) {

    init {
        selectedItem = pageSizes.first()
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
}
