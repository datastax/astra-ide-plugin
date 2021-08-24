package com.datastax.astra.jetbrains.utils.editor.ui

import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.actions.ComputableActionGroup
import com.datastax.astra.jetbrains.services.database.CollectionPagedVirtualFile
import com.datastax.astra.jetbrains.services.database.TablePagedVirtualFile
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.editor.reloadPsiFile
import com.datastax.astra.jetbrains.utils.getCoroutineUiContext
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

class ChangePageActionGroup(project: Project, val virtualFile: VirtualFile) : ComputableActionGroup(), DumbAware {


    private val pageActions = PageActions(project,virtualFile)

    override fun createChildrenProvider(actionManager: ActionManager?): CachedValueProvider<Array<AnAction>> = CachedValueProvider {
        val actions = mutableListOf<AnAction>()
        //actions.add(GoToPageAction())
        actions.add(pageActions)
        actions.add(Separator.create("Page:"))
        //TODO: IF longer than 7 pages put at the end too
        //actions.add(Separator.create
        //actions.add(GoToPageAction())

        //actions.add(Separator.create())
        //actions.add(ReloadProfilesAction())

        CachedValueProvider.Result.create(actions.toTypedArray(), virtualFile)
    }
}

private class PageActions(project: Project,val file: VirtualFile) : ComputableActionGroup(), DumbAware {

    override fun createChildrenProvider(actionManager: ActionManager?): CachedValueProvider<Array<AnAction>> = CachedValueProvider {
        val actions = mutableListOf<AnAction>()
        when (file) {
            is CollectionPagedVirtualFile -> {
                for(pageNumber in 1 .. file.pages.size){
                    actions.add(ChangeCollectionPageAction(file,pageNumber))
                }
            }
            is TablePagedVirtualFile -> {
                for(pageNumber in 1 .. file.pages.size){
                    actions.add(ChangeTablePageAction(file,pageNumber))
                }
            }

        }
        CachedValueProvider.Result.create(actions.toTypedArray(),file)

    }
}

internal class ChangeCollectionPageAction(val file: CollectionPagedVirtualFile, val pageNumber: Int):
    ToggleAction("$pageNumber"),
    CoroutineScope by ApplicationThreadPoolScope("FileEditorUIService"),
    DumbAware {
        override fun isSelected(e: AnActionEvent): Boolean = file.pageIndex == pageNumber-1

        override fun setSelected(e: AnActionEvent, state: Boolean) {
            if(state) {
                val psiFile = e.getData(CommonDataKeys.PSI_FILE)
                //Tell the file to change pages
                (e.getData(CommonDataKeys.VIRTUAL_FILE) as CollectionPagedVirtualFile).setPage(
                    PsiTreeUtil.findChildOfType(
                        (psiFile as JsonFileImpl).topLevelValue?.containingFile?.originalElement,
                        PsiErrorElement::class.java
                    ) != null, pageNumber - 1
                )

                //Update psi with current file contents
                launch {
                    reloadPsiFile(
                        getCoroutineUiContext(),
                        e.getRequiredData(CommonDataKeys.PROJECT),
                        psiFile,
                        "ChangePageNext"
                    )
                }
            }
        }
    }

internal class ChangeTablePageAction(val file: TablePagedVirtualFile, val pageNumber: Int):
    ToggleAction("$pageNumber"),
    CoroutineScope by ApplicationThreadPoolScope("FileEditorUIService"),
    DumbAware {
    override fun isSelected(e: AnActionEvent): Boolean = file.pageIndex == pageNumber-1

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        if(state) {
            launch {
                (e.getData(CommonDataKeys.VIRTUAL_FILE) as TablePagedVirtualFile).setPage(false,pageNumber-1)
            }
        }
    }
}



class PageSelectorComboBoxAction(
    private val project: Project,
    private val virtualFile: VirtualFile,
) : ComboBoxAction(), DumbAware {

    init {

        updatePresentation(templatePresentation)
    }

    override fun update(e: AnActionEvent) {
        updatePresentation(e.presentation)
    }

    override fun useSmallerFontForTextInToolbar() = true

    override fun getMaxRows(): Int {
        return 7
    }

    override fun createPopupActionGroup(button: JComponent?) = DefaultActionGroup(ChangePageActionGroup(project,virtualFile))

    private fun updatePresentation(presentation: Presentation) {
        presentation.text =
            when(virtualFile){
                is CollectionPagedVirtualFile -> {
                    "Page ${virtualFile.pageIndex+1} of ${virtualFile.pages.size}"
                }
                is TablePagedVirtualFile -> {
                    "Page ${virtualFile.pageIndex+1} of ${virtualFile.pages.size}"
                }
                else -> {
                    ""
                }
        }
        //TODO: Ask if there's something better to set as the description?
        //presentation.description = "Type a page number to go to it"
        presentation.description = presentation.text
    }
}

