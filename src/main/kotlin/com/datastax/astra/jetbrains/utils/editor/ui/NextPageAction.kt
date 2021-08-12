package com.datastax.astra.jetbrains.utils.editor.ui

import com.datastax.astra.jetbrains.services.database.CollectionPagedVirtualFile
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.getCoroutineUiContext
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction.writeCommandAction
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.ui.UIBundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NextPageAction(var file: VirtualFile, text: String = "Next Page"):
    AnAction(text, null, AllIcons.Actions.ArrowExpand),
    CoroutineScope by ApplicationThreadPoolScope("Credentials") {

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = file is CollectionPagedVirtualFile
    }

    override fun actionPerformed(e: AnActionEvent) {
        val collectionFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        (collectionFile as CollectionPagedVirtualFile).nextPage()
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)

        writeCommandAction(e.project).withName("ChangePage")
            .shouldRecordActionForActiveDocument(false)
            .run<Exception>{
                psiFile?.manager?.reloadFromDisk(psiFile)
            }

    }
}
