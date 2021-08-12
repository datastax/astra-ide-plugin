package com.datastax.astra.jetbrains.utils.editor.ui

import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.AstraIcons
import com.datastax.astra.jetbrains.utils.getCoroutineUiContext
import com.intellij.json.psi.JsonArray
import com.intellij.json.psi.impl.JsonFileImpl
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.util.*

class InsertDocumentsAction(
    var editor: Editor,
    val cBoxes: ToolbarComboBoxes,
    var state: Long = 0L,
    text: String = message("collection.editor.upsert.title")
) :
    AnAction(text, null, AstraIcons.UI.InsertDoc),
    CoroutineScope by ApplicationThreadPoolScope("Credentials") {

    val edtContext = getCoroutineUiContext()

    override fun update(e: AnActionEvent) {
        val jsonObject = (e.getData(CommonDataKeys.PSI_FILE) as JsonFileImpl).topLevelValue
        val psiError =
            PsiTreeUtil.findChildOfType(jsonObject?.containingFile?.originalElement, PsiErrorElement::class.java)
        e.presentation.isEnabled = false
        if (psiError != null ||
            jsonObject!!::class == com.intellij.json.psi.impl.JsonStringLiteralImpl::class
        ) {
            e.presentation.text = "Insert Disabled: Invalid JSON Format"
        } else if (jsonObject!!::class != com.intellij.json.psi.impl.JsonArrayImpl::class) {
            e.presentation.text = "Insert Disabled: Not Array of JSON Docs"
        } else if (jsonObject.containingFile.modificationStamp == state) {
            e.presentation.text = "Insert Disabled: File Unmodified"
        } else if (cBoxes.anyNull()) {
            e.presentation.text = "Insert Disabled: Endpoint Unselected"
        } else {
            e.presentation.isEnabled = true
            e.presentation.text = "Insert Document(s)"
            //e.presentation.icon multiple icons
        }

    }

    override fun actionPerformed(e: AnActionEvent) {
        try {
            val docList =
                ((e.getData(CommonDataKeys.PSI_FILE) as JsonFileImpl).topLevelValue as JsonArray).valueList.map { it.text }
                    .toList()
            launch {

                    val responses = insertAndWait(docList, cBoxes.getSelected())
                    val success = responses.filter { it.isSuccessful }
                    val failed = responses.filter { !it.isSuccessful }
                //Do the rest in the UI context because we show some notifications and possibly generate dialog boxes from them
                withContext(edtContext) {
                    if (failed.isNotEmpty()) {
                        failedDocInsertNotification(failed.size)
                        // TODO: Add telemetry for this action
                        // TODO: Add more information about failed inserts
                    }
                    if (success.isNotEmpty()) {
                        successfulDocInsertNotification(success.size,cBoxes.collectionComboBox.selectedItem)
                        // TODO: Add telemetry for this action
                    }
                }
            }


        } catch (e: Exception) {
            //TODO: Cancel doing stuff here and tell user
            //TODO: Telemetry about failure
        }
    }

    suspend fun insertAndWait(docList: List<String>, selected: CBoxSelections): List<Response<Unit>> {
        var responses = runBlocking {
            docList.map { doc ->
                async {
                        AstraClient.documentApiForDatabase(selected.database).addDoc(
                            UUID.randomUUID(),
                            AstraClient.accessToken,
                            selected.keyspace,
                            selected.collection,
                            doc.trim().toRequestBody("text/plain".toMediaTypeOrNull()),
                        )
                }
            }
        }
        return responses.awaitAll()
    }
}
