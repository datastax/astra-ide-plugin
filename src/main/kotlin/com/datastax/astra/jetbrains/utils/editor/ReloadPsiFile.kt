package com.datastax.astra.jetbrains.utils.editor

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import kotlinx.coroutines.withContext
import kotlin.coroutines.ContinuationInterceptor

suspend fun reloadPsiFile(edtContext: ContinuationInterceptor, editor: Editor, name: String) {
    withContext(edtContext) {
        val psiFile = PsiManager.getInstance(editor.project!!).findFile((editor as EditorEx).virtualFile)
        WriteCommandAction.writeCommandAction(editor.project!!).withName(name)
            .shouldRecordActionForActiveDocument(false)
            .run<Exception> {
                psiFile?.manager?.reloadFromDisk(psiFile)
            }
    }
}

suspend fun reloadPsiFile(edtContext: ContinuationInterceptor, project: Project, psiFile: PsiFile, name: String) {
    withContext(edtContext) {
        WriteCommandAction.writeCommandAction(project).withName(name)
            .shouldRecordActionForActiveDocument(false)
            .run<Exception> {
                psiFile?.manager?.reloadFromDisk(psiFile)
            }
    }
}
