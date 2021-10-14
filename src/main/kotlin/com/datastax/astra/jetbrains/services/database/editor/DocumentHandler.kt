package com.datastax.astra.jetbrains.services.database.editor

import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.services.database.notifyUpdateDocError
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.AstraIcons
import com.datastax.astra.jetbrains.utils.getCoroutineBgContext
import com.intellij.json.psi.JsonFile
import com.intellij.openapi.editor.impl.EditorHeaderComponent
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiTreeAnyChangeAbstractAdapter
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.components.JBLoadingPanel
import com.intellij.util.ui.AsyncProcessIcon
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.awt.FlowLayout
import java.util.*
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.SwingUtilities

class DocumentHandler(
    private val fileEditor: FileEditor,
    private val project: Project,
    private val virtualFile: DocumentVirtualFile,
) :
    PsiTreeAnyChangeAbstractAdapter() {

    val myFile: PsiFile?
    val toolbar: JPanel
    val updateButton: JButton
    var myBusy = false
    val myBusyIcon = AsyncProcessIcon(toString())

    private val coroutineScope = ApplicationThreadPoolScope("DocumentHandler")
    private val bg = getCoroutineBgContext()

    init {
        myFile = PsiManager.getInstance(project).findFile(virtualFile)
        //TODO: Need a disposable here so the listener will get removed
        PsiManager.getInstance(project).addPsiTreeChangeListener(this)

        //Set up UI

        toolbar = EditorHeaderComponent()
        toolbar.layout= FlowLayout(FlowLayout.LEFT,1, 0)
        updateButton = JButton(AstraIcons.UI.InsertDoc)
        updateButton.isEnabled = false //Only enable when first valid modification occurs
        virtualFile.let {
            toolbar.add(BreadcrumbsEx(it.database.info.name.orEmpty(),it.keyspaceName,it.collectionName,null,it.documentId))
        }

        toolbar.add(updateButton)
        updateButton.addActionListener {
            val json = (myFile as? JsonFile)?.topLevelValue?.text
            coroutineScope.launch {
                updateAstraDocument(json.orEmpty())
            }
        }

        //TODO: Include a refresh button?
        FileEditorManager.getInstance(project).addTopComponent(fileEditor, toolbar)
    }

    private suspend fun updateAstraDocument(json: String) {
        try {
            (fileEditor.component as JBLoadingPanel).startLoading()
            updateButton.isEnabled = false
            withContext(bg) {
                val response = AstraClient.documentApiForDatabase(virtualFile.database).updatePartOfDoc(
                    UUID.randomUUID(),
                    AstraClient.accessToken,
                    virtualFile.keyspaceName,
                    virtualFile.collectionName,
                    virtualFile.documentId,
                    json.orEmpty().toRequestBody("application/json".toMediaTypeOrNull())
                )
                if (!response.isSuccessful) {
                    virtualFile.let{
                        notifyUpdateDocError(it.database.info.name.orEmpty(),it.keyspaceName,it.collectionName,it.documentId,Pair(response.toString(),response.getErrorResponse<Any?>().toString()))
                    }
                    updateButton.isEnabled = true
                }
            }
        } finally {
            (fileEditor.component as JBLoadingPanel).stopLoading()
            updateButton.isEnabled = true
        }

    }

    //@Override
    //public Color getSelectionBackground() {
    //  return isEnabled() ? super.getSelectionBackground() : UIUtil.getTableSelectionBackground(false);
    //}

    override fun onChange(file: PsiFile?) {
        if (file?.equals(myFile) == true) {
            val errorElement = PsiTreeUtil.findChildOfType(file, PsiErrorElement::class.java)
            if (errorElement != null && updateButton.isEnabled) {
                updateButton.isEnabled = false
            } else if (errorElement == null && !updateButton.isEnabled){
                updateButton.isEnabled = true
            }
        }
    }
}
