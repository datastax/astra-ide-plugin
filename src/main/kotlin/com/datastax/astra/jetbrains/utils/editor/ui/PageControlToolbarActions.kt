package com.datastax.astra.jetbrains.utils.editor.ui

import com.datastax.astra.jetbrains.services.database.CollectionPagedVirtualFile
import com.datastax.astra.jetbrains.services.database.TablePagedVirtualFile
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.editor.PagedVirtualFile
import com.datastax.astra.jetbrains.utils.editor.reloadPsiFile
import com.datastax.astra.jetbrains.utils.getCoroutineUiContext
import com.intellij.icons.AllIcons
import com.intellij.json.psi.impl.JsonFileImpl
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.editor.impl.EditorHeaderComponent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.math.NumberUtils.toInt
import org.apache.tools.ant.taskdefs.Execute.launch
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.Action
import javax.swing.BorderFactory
import javax.swing.JPanel


class PageControlToolbarActions(
    val project: Project,
    val file: VirtualFile,
    val parentHeader: EditorHeaderComponent
){
    val pageField = JBTextField("1",2)
    val pageCountLabel = JBLabel("of 1")
    val pageSearchField = JBTextArea("1")
    val edtContext = getCoroutineUiContext()
    init {
        //pageField.border = BorderFactory.createEmptyBorder(0,0,0,0)
        //file.setRemoteLabels(pageField, pageCountLabel)
    }

    fun getActions(): DefaultActionGroup =
        when (file) {
            is CollectionPagedVirtualFile -> {
                DefaultActionGroup(
                    PreviousPageAction(file),
                    PageSelectorComboBoxAction(project,file),
                    NextPageAction(file),
                )
            }
            is TablePagedVirtualFile -> {
                DefaultActionGroup(
                    PreviousTableAction(file),
                    PageSelectorComboBoxAction(project,file),
                    NextTableAction(file),
                )
            }
            else -> {
                DefaultActionGroup()
            }
        }
        val nextPageAction = createToolbar(DefaultActionGroup(NextPageAction(file)),parentHeader,0,0)

        //pageControlActions.add(ChangePageActionField(file,newPageNumber,::updatePageNumber))
        //pageControlActions.add(ChangePageActionField(file,newPageNumber,::updatePageNumber))
        //pageControlActions.add(ChangeRowsActionBox(file))
        //val panel = JPanel(FlowLayout(FlowLayout.LEFT,2,0))
        //Disabled because it didn't seem to add anything and made things more busy
        //panel.add(JBLabel("Pg"))
        //panel.add(pageField)
        //panel.add(pageCountLabel)
        //panel.add(nextPageAction)


        //TODO: Add page selection
        //TODO: Add right button
        //TODO: Add page size ComboBox

        //return panel

}

class PreviousPageAction(val file: VirtualFile):
    AnAction("Previous Page", null, AllIcons.Actions.ArrowCollapse),
    CoroutineScope by ApplicationThreadPoolScope("FileEditorUIService") {

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = file is CollectionPagedVirtualFile
    }

    override fun actionPerformed(e: AnActionEvent) {
        //Gather information about the paged file
        val collectionFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        //Tell the file to change pages
        (collectionFile as CollectionPagedVirtualFile).prevPage(
            PsiTreeUtil.findChildOfType((psiFile as JsonFileImpl).topLevelValue?.containingFile?.originalElement, PsiErrorElement::class.java) != null )

        //Update psi with current file contents
        launch {
            reloadPsiFile(getCoroutineUiContext(), e.getRequiredData(CommonDataKeys.PROJECT),psiFile, "ChangePageNext")
        }
    }
}

class NextPageAction(val file: VirtualFile):
    AnAction("Next Page", null, AllIcons.Actions.ArrowExpand),
    CoroutineScope by ApplicationThreadPoolScope("FileEditorUIService") {

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = file is CollectionPagedVirtualFile
    }

    override fun actionPerformed(e: AnActionEvent) {
        val collectionFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        (collectionFile as CollectionPagedVirtualFile).nextPage(
            PsiTreeUtil.findChildOfType((psiFile as JsonFileImpl).topLevelValue?.containingFile?.originalElement, PsiErrorElement::class.java) != null)

        launch {
            reloadPsiFile(getCoroutineUiContext(), e.getRequiredData(CommonDataKeys.PROJECT),psiFile, "ChangePageNext")
        }
    }
}

class PreviousTableAction(val file: VirtualFile):
    AnAction("Previous Page", null, AllIcons.Actions.ArrowCollapse),
    CoroutineScope by ApplicationThreadPoolScope("FileEditorUIService") {

    override fun update(e: AnActionEvent) {
        if(!(file as TablePagedVirtualFile).tableView.isFocusable){
            e.presentation.isEnabled = false
        }
    }

    //TODO: Ask Garret about how to detect errors on a table
    override fun actionPerformed(e: AnActionEvent) {
        //Gather information about the paged file

        //Tell the file to change pages
        launch {
            (e.getData(CommonDataKeys.VIRTUAL_FILE) as TablePagedVirtualFile).prevPage(false)
        }
    }
}

class NextTableAction(val file: VirtualFile):
    AnAction("Next Page", null, AllIcons.Actions.ArrowExpand),
    CoroutineScope by ApplicationThreadPoolScope("FileEditorUIService") {

    override fun update(e: AnActionEvent) {
        if(!(file as TablePagedVirtualFile).tableView.isFocusable){
            e.presentation.isEnabled = false
        }

    }

    override fun actionPerformed(e: AnActionEvent) {
        //Tell the file to change pages
        launch {
            (e.getData(CommonDataKeys.VIRTUAL_FILE) as TablePagedVirtualFile).nextPage(false)
        }
    }
}
