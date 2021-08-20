package com.datastax.astra.jetbrains.utils.editor.ui

import com.datastax.astra.jetbrains.services.database.CollectionPagedVirtualFile
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.editor.PagedVirtualFile
import com.datastax.astra.jetbrains.utils.editor.reloadPsiFile
import com.datastax.astra.jetbrains.utils.getCoroutineUiContext
import com.intellij.icons.AllIcons
import com.intellij.json.psi.impl.JsonFileImpl
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.impl.EditorHeaderComponent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.JBMenuItem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.border.CustomLineBorder
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.*
import javax.swing.border.MatteBorder

class PageControlToolbar(
    val project: Project,
    val file: PagedVirtualFile,
    val parentHeader: EditorHeaderComponent
){
    val pageField = JBTextField("1",2)
    val pageCountLabel = JBLabel("of 1")

    init {
        pageField.border = BorderFactory.createEmptyBorder(0,-1,0,0)
        file.setRemoteLabels(pageField,pageCountLabel)
    }

    fun getPanel(): JPanel {
        //Add the previous page action to the other toolbar section to more easily add separator
        val nextPageActionComponent = createToolbar(DefaultActionGroup(NextPageAction(file)),parentHeader,1,0)

        //pageControlActions.add(ChangePageActionField(file,newPageNumber,::updatePageNumber))
        //pageControlActions.add(ChangePageActionField(file,newPageNumber,::updatePageNumber))
        //pageControlActions.add(ChangeRowsActionBox(file))
        val panel = JPanel(FlowLayout(FlowLayout.LEFT,2,0))
        //Disabled because it didn't seem to add anything and made things more busy
        //panel.add(JBLabel("Pg"))
        panel.add(pageField)
        panel.add(pageCountLabel)
        panel.add(nextPageActionComponent)


        //TODO: Add page selection
        //TODO: Add right button
        //TODO: Add page size ComboBox

        return panel
    }
}

class BlankAction():
    DumbAwareAction("", null, null){

    override fun actionPerformed(e: AnActionEvent) {
    }
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

class ChangePageAction(val file: VirtualFile, val pageNumber: Int):
    AnAction("Page Number", null, AllIcons.Actions.ArrowExpand),
    CoroutineScope by ApplicationThreadPoolScope("FileEditorUIService") {

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = file is CollectionPagedVirtualFile
    }

    override fun actionPerformed(e: AnActionEvent) {
        val collectionFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        (collectionFile as CollectionPagedVirtualFile).setPage(
            PsiTreeUtil.findChildOfType(
                (psiFile as JsonFileImpl).topLevelValue?.containingFile?.originalElement,
                PsiErrorElement::class.java
            ) != null, pageNumber
        )

        launch {
            reloadPsiFile(getCoroutineUiContext(), e.getRequiredData(CommonDataKeys.PROJECT), psiFile, "ChangePageNext")
        }
    }
}
