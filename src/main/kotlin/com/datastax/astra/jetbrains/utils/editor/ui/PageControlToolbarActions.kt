package com.datastax.astra.jetbrains.utils.editor.ui

import com.datastax.astra.jetbrains.services.database.CollectionVirtualFile
import com.datastax.astra.jetbrains.services.database.TableVirtualFile
import com.datastax.astra.jetbrains.utils.editor.PagedVirtualFile
import com.datastax.astra.jetbrains.utils.getCoroutineUiContext
import com.intellij.icons.AllIcons
import com.intellij.json.psi.impl.JsonFileImpl
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil

class PageControlToolbarActions(
    val project: Project,
    val file: VirtualFile,
) {
    val edtContext = getCoroutineUiContext()
    init {
        // pageField.border = BorderFactory.createEmptyBorder(0,0,0,0)
        // file.setRemoteLabels(pageField, pageCountLabel)
    }

    fun getActions(): DefaultActionGroup =
        when (file) {
            is CollectionVirtualFile -> {
                DefaultActionGroup(
                    PreviousPageAction(file),
                    SelectPageComboBoxAction(project, file),
                    NextPageAction(file),
                    SetPageSizeComboBoxAction(project, file),
                )
            }
            is TableVirtualFile -> {
                DefaultActionGroup(
                    PreviousTableAction(file),
                    SelectPageComboBoxAction(project, file),
                    NextTableAction(file),
                    SetPageSizeComboBoxAction(project, file),
                )
            }
            else -> {
                DefaultActionGroup()
            }
        }
    // val nextPageAction = createToolbar(DefaultActionGroup(NextPageAction(file)),parentHeader,0,0)

    // pageControlActions.add(ChangePageActionField(file,newPageNumber,::updatePageNumber))
    // pageControlActions.add(ChangePageActionField(file,newPageNumber,::updatePageNumber))
    // pageControlActions.add(ChangeRowsActionBox(file))
    // val panel = JPanel(FlowLayout(FlowLayout.LEFT,2,0))
    // Disabled because it didn't seem to add anything and made things more busy
    // panel.add(JBLabel("Pg"))
    // panel.add(pageField)
    // panel.add(pageCountLabel)
    // panel.add(nextPageAction)

    // TODO: Add page selection
    // TODO: Add right button
    // TODO: Add page size ComboBox

    // return panel
}

class PreviousPageAction(val file: PagedVirtualFile) :
    AnAction("Previous Page", null, AllIcons.Actions.ArrowCollapse) {

    override fun update(e: AnActionEvent) {
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        if (virtualFile is PagedVirtualFile) {
            if (virtualFile.isLocked()) {
                e.presentation.isEnabled = false
                e.presentation.text = "Loading..."
            } else {
                e.presentation.isEnabled = true
                e.presentation.text = "Previous Page"
            }
        } else {
            e.presentation.isEnabled = false
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        // Gather information about the paged file
        val collectionFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        // Tell the file to change pages
        (collectionFile as CollectionVirtualFile).prevPage(
            PsiTreeUtil.findChildOfType((psiFile as JsonFileImpl).topLevelValue?.containingFile?.originalElement, PsiErrorElement::class.java) != null
        )
    }
}

class NextPageAction(val file: PagedVirtualFile) :
    AnAction("Next Page", null, AllIcons.Actions.ArrowExpand) {

    override fun update(e: AnActionEvent) {
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        if (virtualFile is PagedVirtualFile) {
            if (virtualFile.isLocked()) {
                e.presentation.isEnabled = false
                e.presentation.text = "Loading..."
            } else {
                e.presentation.isEnabled = true
                e.presentation.text = "Next Page"
            }
        } else {
            e.presentation.isEnabled = false
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val collectionFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        (collectionFile as CollectionVirtualFile).nextPage(
            PsiTreeUtil.findChildOfType((psiFile as JsonFileImpl).topLevelValue?.containingFile?.originalElement, PsiErrorElement::class.java) != null
        )
    }
}

class PreviousTableAction(val file: PagedVirtualFile) :
    AnAction("Previous Page", null, AllIcons.Actions.ArrowCollapse) {

    override fun update(e: AnActionEvent) {
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        if (virtualFile is PagedVirtualFile) {
            if (virtualFile.isLocked()) {
                e.presentation.isEnabled = false
                e.presentation.text = "Loading..."
            } else {
                e.presentation.isEnabled = true
                e.presentation.text = "Previous Page"
            }
        } else {
            e.presentation.isEnabled = false
        }
    }

    // TODO: Ask Garret about how to detect errors on a table
    override fun actionPerformed(e: AnActionEvent) {
        // Gather information about the paged file

        // Tell the file to change pages

        (e.getData(CommonDataKeys.VIRTUAL_FILE) as TableVirtualFile).prevPage(false)
    }
}

class NextTableAction(val file: PagedVirtualFile) :
    AnAction("Next Page", null, AllIcons.Actions.ArrowExpand) {

    override fun update(e: AnActionEvent) {
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        if (virtualFile is PagedVirtualFile) {
            if (virtualFile.isLocked()) {
                e.presentation.isEnabled = false
                e.presentation.text = "Loading..."
            } else {
                e.presentation.isEnabled = true
                e.presentation.text = "Next Page"
            }
        } else {
            e.presentation.isEnabled = false
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        // Tell the file to change pages
        (e.getData(CommonDataKeys.VIRTUAL_FILE) as TableVirtualFile).nextPage(false)
    }
}
