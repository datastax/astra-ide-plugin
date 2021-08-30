package com.datastax.astra.jetbrains.utils.editor.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
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
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.util.*

class OpenFileAction(
    text: String = "Getfile",
) :
    AnAction(text, null, AllIcons.Ide.Gift),
    CoroutineScope by ApplicationThreadPoolScope("Credentials") {
    lateinit var file: String
    val edtContext = getCoroutineUiContext()

    override fun update(e: AnActionEvent) {

    }

    override fun actionPerformed(e: AnActionEvent) {
        launch {
            withContext(edtContext) {
                file = FileChooser.chooseFile(FileChooserDescriptor(true,false,false,false,false,false).withDescription("ChooseJson").withTitle("Browse Files"),e.project,null)!!.path.orEmpty()
                println(file)
            }
        }
    }


}
