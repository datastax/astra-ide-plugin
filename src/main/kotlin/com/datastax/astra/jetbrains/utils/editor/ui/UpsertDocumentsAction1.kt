package com.datastax.astra.jetbrains.utils.editor.ui

import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.AstraIcons
import com.intellij.icons.AllIcons
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.impl.JsonFileImpl
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.LayeredIcon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.util.UUID.randomUUID

class UpsertDocumentsAction1(var editor: Editor, val cBoxes: ToolbarComboBoxes, var state: Long=0L, text: String = message("collection.editor.upsert.title")):
    AnAction(text, null, AstraIcons.UI.UpsertDoc),
    CoroutineScope by ApplicationThreadPoolScope("Credentials") {

    override fun update(e: AnActionEvent) {
        val file = (e.getData(CommonDataKeys.PSI_FILE) as JsonFileImpl)
        val psiError = PsiTreeUtil.findChildOfType(file.originalElement,PsiErrorElement::class.java)
        e.presentation.isEnabled = false
        if(cBoxes.anyNull() ){
            e.presentation.text ="Upsert Disabled: Endpoint Unselected"
        }
        else if (file.modificationStamp==state ){
            e.presentation.text ="Upsert Disabled: Document(s) Unmodified"
        }
        else if( psiError != null ||
            file.topLevelValue!!::class == com.intellij.json.psi.impl.JsonStringLiteralImpl::class){
            e.presentation.text ="Upsert Disabled: Invalid JSON Format"
        }
        else{
            e.presentation.isEnabled = true
            e.presentation.text =message("collection.editor.upsert.title")
        }

        //TODO:
        // When schema is unset for current file grey this out
    }

    override fun actionPerformed(e: AnActionEvent) {
        val jsonObject = (e.getData(CommonDataKeys.PSI_FILE) as JsonFileImpl).topLevelValue



            val selected = cBoxes.getSelected()

            val failedResponses = mutableMapOf<String, Response<Unit>>()

            //TODO: Properly handle/await the responses in a coroutine

            //state = jsonObject.containingFile.modificationStamp

    }
}
