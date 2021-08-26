package com.datastax.astra.jetbrains.utils.editor.ui

import com.datastax.astra.jetbrains.actions.ComputableActionGroup
import com.datastax.astra.jetbrains.services.database.CollectionVirtualFile
import com.datastax.astra.jetbrains.services.database.TableVirtualFile
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.editor.PagedVirtualFile
import com.intellij.json.psi.impl.JsonFileImpl
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.PsiTreeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.swing.JComponent

private class SelectPageActionGroup(project: Project, val file: VirtualFile) : ComputableActionGroup(), DumbAware {

    override fun createChildrenProvider(actionManager: ActionManager?): CachedValueProvider<Array<AnAction>> = CachedValueProvider {
        val actions = mutableListOf<AnAction>()
        actions.add(Separator.create("Page"))
        //Can check this inside the for loop but that would check the file type each loop
        when (file) {
            is CollectionVirtualFile -> {
                for(pageNumber in 1 .. file.pages.size){
                    actions.add(SetPageCollectionAction(file,pageNumber))
                }
            }
            is TableVirtualFile -> {
                for(pageNumber in 1 .. file.pages.size){
                    actions.add(SetPageTableAction(file,pageNumber))
                }
            }
        }
        CachedValueProvider.Result.create(actions.toTypedArray(),file)
    }
}

internal class SetPageCollectionAction(val file: CollectionVirtualFile, val pageNumber: Int):
    ToggleAction("$pageNumber"),
    CoroutineScope by ApplicationThreadPoolScope("FileEditorUIService"),
    DumbAware {
        override fun isSelected(e: AnActionEvent): Boolean = file.pageIndex == pageNumber-1

        override fun setSelected(e: AnActionEvent, state: Boolean) {
            if(state) {
                val psiFile = e.getData(CommonDataKeys.PSI_FILE)
                //Tell the file to change pages
                (e.getData(CommonDataKeys.VIRTUAL_FILE) as CollectionVirtualFile).setPage(
                    PsiTreeUtil.findChildOfType(
                        (psiFile as JsonFileImpl).topLevelValue?.containingFile?.originalElement,
                        PsiErrorElement::class.java
                    ) != null, pageNumber - 1
                )
            }
        }
    }

internal class SetPageTableAction(val file: TableVirtualFile, val pageNumber: Int):
    ToggleAction("$pageNumber"),
    CoroutineScope by ApplicationThreadPoolScope("FileEditorUIService"),
    DumbAware {
    override fun isSelected(e: AnActionEvent): Boolean = file.pageIndex == pageNumber-1

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        if(state) {
            launch {
                (e.getData(CommonDataKeys.VIRTUAL_FILE) as TableVirtualFile).setPage(false,pageNumber-1)
            }
        }
    }
}

class SelectPageComboBoxAction(
    private val project: Project,
    private val virtualFile: PagedVirtualFile,
) : ComboBoxAction(), DumbAware {

    init {

        updatePresentation(templatePresentation)
    }

    override fun update(e: AnActionEvent) {
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        if(virtualFile is PagedVirtualFile){
            if(virtualFile.isLocked()) {
                loadingPresentation(e.presentation)
            }
            else{
                e.presentation.isEnabled = true
                updatePresentation(e.presentation)
            }
        }
        else{
            e.presentation.isEnabled = false
        }

    }

    override fun useSmallerFontForTextInToolbar() = true

    override fun getMaxRows(): Int {
        return 7
    }

    override fun createPopupActionGroup(button: JComponent?): DefaultActionGroup {
        return DefaultActionGroup(SelectPageActionGroup(project,virtualFile))
    }

    private fun loadingPresentation(presentation: Presentation){
        presentation.isEnabled = false
        presentation.text = "Loading..."
        presentation.description = "Loading pages..."
    }

    private fun updatePresentation(presentation: Presentation) {
        presentation.text =
            when(virtualFile){
                is PagedVirtualFile -> {
                    "Pg ${virtualFile.pageIndex+1} of ${virtualFile.pages.size}"
                }
                else -> {
                    ""
                }
        }
        //TODO: Ask if there's something better to set as the description?
        presentation.description = "Page: ${virtualFile.pageIndex+1} of ${virtualFile.pages.size}"
    }
}

