package com.datastax.astra.jetbrains.utils.editor.ui

import com.datastax.astra.jetbrains.services.database.CollectionPagedVirtualFile
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.icons.AllIcons
import com.intellij.json.psi.impl.JsonFileImpl
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction.writeCommandAction
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.UIBundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GoToPageAction(var file: VirtualFile, text: String = "Next Page"):
    AnAction(text, null, AllIcons.Actions.ArrowExpand),
    CoroutineScope by ApplicationThreadPoolScope("FileEditorUIService") {

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = file is CollectionPagedVirtualFile
    }

    override fun actionPerformed(e: AnActionEvent) {
        //Gather information about the paged file
        val collectionFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        val psiError =
            PsiTreeUtil.findChildOfType((psiFile as JsonFileImpl).topLevelValue?.containingFile?.originalElement, PsiErrorElement::class.java)

        //Tell the file to change pages
        (collectionFile as CollectionPagedVirtualFile).nextPage(psiError != null )

        //Update psi with current file contents
        writeCommandAction(e.project).withName("ChangePage")
            .shouldRecordActionForActiveDocument(false)
            .run<Exception>{
                psiFile?.manager?.reloadFromDisk(psiFile)
            }

    }
}
