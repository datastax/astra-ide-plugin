package com.datastax.astra.jetbrains.services.database.editor

import com.datastax.astra.jetbrains.AstraClient
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
        //Set up UI

        toolbar = EditorHeaderComponent()
        updateButton = JButton(AstraIcons.UI.InsertDoc)
        updateButton.isEnabled = false //Only enable when first valid modification occurs
        toolbar.add(updateButton)
        updateButton.addActionListener {
            coroutineScope.launch {
                updateAstraDocument()
            }
        }

        // TODO: Insert the breadcrumbs to the left of this button
        //TODO: Include a refresh button?
        FileEditorManager.getInstance(project).addTopComponent(fileEditor, toolbar)

        myFile = PsiManager.getInstance(project).findFile(virtualFile)
        //TODO: Need a disposable here so the listener will get removed
        // Disposable for what object?
        PsiManager.getInstance(project).addPsiTreeChangeListener(this)

    }

    private suspend fun updateAstraDocument() {
        try {
            (fileEditor.component as JBLoadingPanel).startLoading()
            updateButton.isEnabled = false
            withContext(bg) {
                val json = (myFile as? JsonFile)?.topLevelValue?.text
                val response = AstraClient.documentApiForDatabase(virtualFile.database).updatePartOfDoc(
                    UUID.randomUUID(),
                    AstraClient.accessToken,
                    virtualFile.keyspaceName,
                    virtualFile.collectionName,
                    virtualFile.documentId,
                    json.orEmpty().toRequestBody("application/json".toMediaTypeOrNull())
                )
                if (!response.isSuccessful) {
                    //TODO: Notification
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
