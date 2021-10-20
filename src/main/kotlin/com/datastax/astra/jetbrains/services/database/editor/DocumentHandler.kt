package com.datastax.astra.jetbrains.services.database.editor

import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.services.database.notifyReloadDocError
import com.datastax.astra.jetbrains.services.database.notifyUpdateDocError
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.AstraIcons
import com.datastax.astra.jetbrains.utils.getCoroutineBgContext
import com.datastax.astra.jetbrains.utils.getCoroutineUiContext
import com.datastax.astra.stargate_document_v2.infrastructure.Serializer
import com.intellij.icons.AllIcons
import com.intellij.json.psi.JsonFile
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.editor.impl.EditorHeaderComponent
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.*
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
    val refreshButton: JButton

    val gson = Serializer.gsonBuilder
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()

    private val coroutineScope = ApplicationThreadPoolScope("DocumentHandler")
    private val bg = getCoroutineBgContext()
    internal val edt = getCoroutineUiContext()

    init {

        myFile = PsiManager.getInstance(project).findFile(virtualFile)
        //TODO: Need a disposable here so the listener will get removed
        PsiManager.getInstance(project).addPsiTreeChangeListener(this)

        //Set up UI

        toolbar = EditorHeaderComponent()
        toolbar.layout= FlowLayout(FlowLayout.LEFT,1, 0)
        virtualFile.let {
            toolbar.add(BreadcrumbsEx(it.database.info.name.orEmpty(),it.keyspaceName,null,it.collectionName,it.documentId))
        }
        updateButton = JButton(AstraIcons.UI.InsertDoc)
        updateButton.isEnabled = false //Only enable when first valid modification occurs
        toolbar.add(updateButton)
        refreshButton = JButton(AllIcons.Actions.Refresh)
        refreshButton.isEnabled = true
        toolbar.add(refreshButton)

        updateButton.addActionListener {
            val json = (myFile as? JsonFile)?.topLevelValue?.text
            coroutineScope.launch {
                updateAstraDocument(json.orEmpty())
            }
        }

        refreshButton.addActionListener {
            coroutineScope.launch {
                reloadAstraDocument()
            }
        }

        setTooltips()

        //TODO: Include a refresh button?
        FileEditorManager.getInstance(project).addTopComponent(fileEditor, toolbar)
    }

    private suspend fun updateAstraDocument(json: String) {
        try {
            (fileEditor.component as JBLoadingPanel).startLoading()
            updateButton.isEnabled = false
            refreshButton.isEnabled = false
            withContext(bg) {
                val response = AstraClient.getInstance(project).documentApiForDatabase(virtualFile.database).updatePartOfDoc(
                    UUID.randomUUID(),
                    AstraClient.getInstance(project).accessToken,
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
            refreshButton.isEnabled = true
        }

    }

    private suspend fun reloadAstraDocument() {
        try {
            val changesExist = updateButton.isEnabled
            (fileEditor.component as JBLoadingPanel).startLoading()
            refreshButton.isEnabled = false
            withContext(bg) {
                val response = AstraClient.getInstance(project).documentApiForDatabase(virtualFile.database).getDocById(
                    UUID.randomUUID(),
                    AstraClient.getInstance(project).accessToken,
                    virtualFile.keyspaceName,
                    virtualFile.collectionName,
                    virtualFile.documentId
                )
                if (!response.isSuccessful) {
                    virtualFile.let{
                        notifyReloadDocError(it.database.info.name.orEmpty(),it.keyspaceName,it.collectionName,it.documentId,Pair(response.toString(),response.getErrorResponse<Any?>().toString()))
                    }
                    updateButton.isEnabled = changesExist
                } else{

                    fileEditor.file?.let {
                        withContext(edt) {
                            PsiManager.getInstance(project).findFile(it)?.let {
                                PsiDocumentManager.getInstance(project).getDocument(it)?.let {
                                    runWriteAction { it.setText(gson.toJson(response.body()?.data)) }
                                }
                            }
                            delayedUpdateDisable()
                        }
                    }


                }
            }
        } finally {
            (fileEditor.component as JBLoadingPanel).stopLoading()
            refreshButton.isEnabled = true

        }

    }

    fun setTooltips(){
        //TODO: Add tooltips for other buttons on this toolbar in other branches

        updateButton.toolTipText = "Update Remote Doc"
    }
    //@Override
    //public Color getSelectionBackground() {
    //  return isEnabled() ? super.getSelectionBackground() : UIUtil.getTableSelectionBackground(false);
    //}

    //Tried other solutions but the onChange can get called many times so a one time flag won't work
    suspend fun delayedUpdateDisable(){
        withContext(bg){
            launch {
                Thread.sleep(400)
                updateButton.isEnabled = false
            }
        }
    }

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
