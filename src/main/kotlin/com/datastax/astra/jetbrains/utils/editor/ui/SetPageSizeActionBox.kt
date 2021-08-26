package com.datastax.astra.jetbrains.utils.editor.ui

import com.datastax.astra.jetbrains.actions.ComputableActionGroup
import com.datastax.astra.jetbrains.services.database.CollectionVirtualFile
import com.datastax.astra.jetbrains.services.database.TableVirtualFile
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.editor.PagedVirtualFile
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.util.CachedValueProvider
import kotlinx.coroutines.CoroutineScope
import javax.swing.JComponent

private class SetPageSizeActionGroup(project: Project, val file: PagedVirtualFile) : ComputableActionGroup(), DumbAware {

    override fun createChildrenProvider(actionManager: ActionManager?): CachedValueProvider<Array<AnAction>> = CachedValueProvider {
        val collectionPageSizeOptions = intArrayOf(1,5,10,20,50)
        val tablePageSizeOptions = intArrayOf(10,20,50,100,200)
        val actions = mutableListOf<AnAction>()
        //Can check this inside the for loop but that would check the file type each loop
        actions.add(Separator.create("Page Size"))
        when (file) {
            is CollectionVirtualFile -> {
                for(pageSize in collectionPageSizeOptions){
                    actions.add(SetPageSizeAction(file,pageSize))
                }
            }
            is TableVirtualFile -> {
                for(pageSize in tablePageSizeOptions){
                    actions.add(SetPageSizeAction(file,pageSize))
                }
            }
        }
        actions.add(SetPageSizeAllAction(file))
        CachedValueProvider.Result.create(actions.toTypedArray(),file)

    }
}

internal class SetPageSizeAction(val file: PagedVirtualFile, val pageSize: Int):
    ToggleAction("$pageSize"),
    DumbAware {
        override fun isSelected(e: AnActionEvent): Boolean = file.pageSize == pageSize

        override fun setSelected(e: AnActionEvent, state: Boolean) {
            file.setVirtualPageSize(pageSize)
        }
    }

internal class SetPageSizeAllAction(val file: PagedVirtualFile):
    ToggleAction("All"),
    CoroutineScope by ApplicationThreadPoolScope("FileEditorUIService"),
    DumbAware {
    override fun isSelected(e: AnActionEvent): Boolean = file.pageSize == file.getSize()

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        file.setVirtualPageSize(file.getSize())
    }
}

class SetPageSizeComboBoxAction(
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
        return DefaultActionGroup(SetPageSizeActionGroup(project,virtualFile))
    }

    private fun loadingPresentation(presentation: Presentation){
        presentation.isEnabled = false
        presentation.description = "Loading pages..."
    }

    private fun updatePresentation(presentation: Presentation) {
            when(virtualFile){
                is CollectionVirtualFile -> {
                    presentation.text = if(virtualFile.pageSize > 1){
                        "${virtualFile.pageSize} docs"
                    } else {
                        "${virtualFile.pageSize} doc"
                    }
                    presentation.description = "Page Size: ${presentation.text}"
                }
                is TableVirtualFile -> {
                    presentation.text = if(virtualFile.pageSize > 1){
                        "${virtualFile.pageSize} rows"
                    } else {
                        "${virtualFile.pageSize} row"
                    }
                    presentation.description = "Page Size: ${presentation.text}"
                }
                else -> {
                    ""
                }
        }
        //TODO: Ask if there's something better to set as the description?

    }
}

