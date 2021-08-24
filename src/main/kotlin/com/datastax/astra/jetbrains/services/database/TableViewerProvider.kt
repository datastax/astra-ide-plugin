package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.models.Database
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.project.PossiblyDumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.withContext
import java.awt.BorderLayout
import java.beans.PropertyChangeListener
import javax.swing.JComponent
import javax.swing.JPanel

class TableViewerEditorProvider : FileEditorProvider, PossiblyDumbAware {

    override fun isDumbAware() = true

    override fun accept(project: Project, file: VirtualFile) = file is TablePagedVirtualFile

    override fun createEditor(project: Project, file: VirtualFile): FileEditor =
        TableViewerEditor(project, file as TablePagedVirtualFile)

    override fun getEditorTypeId() = EDITOR_TYPE_ID

    override fun getPolicy() = FileEditorPolicy.HIDE_DEFAULT_EDITOR

    companion object {
        const val EDITOR_TYPE_ID = "Table Viewer"
    }
}

class TableViewerEditor(project: Project, val tableVirtualFile: TablePagedVirtualFile) : UserDataHolderBase(), FileEditor {
    val myPanel = JPanel(BorderLayout())
    var myHeaderPanel: JPanel = MyHeaderPanel(::getPermanentHeaderComponent)
    protected val edtContext = getCoroutineUiContext(disposable = this)
    private val PERMANENT_HEADER = Key.create<JComponent>("PERMANENT_HEADER")

    init {
        myPanel.layout = BorderLayout()
        myPanel.add(myHeaderPanel, BorderLayout.NORTH)
        myHeaderPanel.isEnabled = false
        myPanel.add(tableVirtualFile.component)
    }

    suspend fun setHeaderComponent(header: JComponent?) {
        withContext(edtContext) {

            var header = header
            myHeaderPanel.removeAll()
            header = header ?: getPermanentHeaderComponent()
            if (header != null) {
                myHeaderPanel.add(header)
            }
            myHeaderPanel.isEnabled = true
            myHeaderPanel.revalidate()
            myHeaderPanel.repaint()
            //myPanel.add(myHeader,BorderLayout.NORTH)
            //            myPanel.revalidate()
            //            myPanel.repaint()
        }
        //TODO: Ask Garrett about touchbar
    }

    fun getPermanentHeaderComponent(): JComponent? {
        return getUserData(PERMANENT_HEADER)
    }


    fun setPermanentHeaderComponent(component: JComponent?) {
        putUserData(PERMANENT_HEADER, component)
    }

    fun hasHeaderComponent(): Boolean = myHeaderPanel.isEnabled

    /**
     * @return a component set by [.setHeaderComponent] or `null` if no header currently installed.
     */
    fun getHeaderComponent(): JComponent? {
        return if (myHeaderPanel.componentCount > 0) {
            myHeaderPanel.getComponent(0) as JComponent
        } else null
    }

    override fun dispose() {}

    override fun getComponent() = myPanel

    override fun getPreferredFocusedComponent() = tableVirtualFile.component

    override fun getName() = "Table Panel"

    override fun setState(state: FileEditorState) {}

    override fun isModified(): Boolean = false

    override fun isValid(): Boolean = true

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {}

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {}

    override fun getCurrentLocation(): FileEditorLocation? = null

    class MyHeaderPanel(val getPermanentHeaderComponent: () -> JComponent?) : JPanel(BorderLayout()) {
        private var myOldHeight = 0
        override fun revalidate() {
            myOldHeight = height
            super.revalidate()
        }

        override fun validateTree() {
            var height = myOldHeight
            super.validateTree()
            height -= getHeight()
            if (height != 0 && !(myOldHeight == 0 && componentCount > 0 && getPermanentHeaderComponent() === getComponent(
                    0
                ))
            ) {
                //TODO: Use this if the scrollbar os acting funny
                //myVerticalScrollBar.setValue(myVerticalScrollBar.getValue() - height)
            }
            myOldHeight = getHeight()
        }
    }
}

fun openEditor(project: Project, table: com.datastax.astra.stargate_rest_v2.models.Table, database: Database): Editor? {
    return FileEditorManager.getInstance(project).openTextEditor(
        OpenFileDescriptor(
            project,
            TableVirtualFile(table, database)
        ),
        true
    )
}
