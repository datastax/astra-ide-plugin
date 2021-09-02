package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.utils.editor.PagedVirtualFile
import com.datastax.astra.jetbrains.utils.editor.VirtualFilePage
import com.datastax.astra.jetbrains.utils.editor.ui.EndpointCollection
import com.datastax.astra.stargate_document_v2.infrastructure.Serializer
import com.google.gson.internal.LinkedTreeMap
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.psi.PsiManager
import com.intellij.ui.components.JBPanelWithEmptyText
import com.intellij.ui.components.JBTextField
import com.jetbrains.rd.util.put
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CollectionVirtualFile(var endpointInfo: EndpointCollection, initialWhereParams: String = "",) :
    PagedVirtualFile(
        endpointInfo.collection,
        FileTypeManager.getInstance().getFileTypeByExtension("JSON"),
    ) {
    var jsonDocs = LinkedTreeMap<String, Any>()
    lateinit var editor: Editor
    var whereSearchBox: JBTextField = JBTextField("initialWhereParams")
    override var pages = mutableListOf<VirtualCollectionPage>()
    val gson = Serializer.gsonBuilder
        .setPrettyPrinting()
        .disableHtmlEscaping()
        // .enableComplexMapKeySerialization()
        .create()


    init {
        pageSize = 5
        lock()
    }

    override fun getSize() = jsonDocs.size

    // TODO: Make this a list of a custom object or something to keep returned values in order
    override fun addData(responseMap: Any) {
        try {
            (responseMap as LinkedTreeMap<*, *>).forEach { jsonDocs[it.key as String] = it.value }
        } catch (exception: Exception) {
            // TODO: Ask Garrett what to do about this or if doing it unsafely is actually 'safe'
        }
    }

    override fun buildPagesAndSet() {
        pages.clear()
        val nextMap = LinkedTreeMap<String, Any>()
        var buildingIndex = 0
        // Either have to index it, or transform it and then iterate, or add the incomplete page after for loop
        var loopIndex = 0
        for (jsonDoc in jsonDocs) {
            nextMap.put(jsonDoc)
            if (nextMap.size >= pageSize || loopIndex == (jsonDocs.size - 1)) {
                pages.add(buildingIndex++, VirtualCollectionPage(gson.toJson(nextMap), false))
                nextMap.clear()
            }
            loopIndex++
        }
        pageIndex = 0
        setContent(this, pages[0].data, true)
        reloadPsiFile()
    }

    override fun setPage(currentPageStatus: Boolean, nextIndex: Int) {
        // Save the current page state
        pages[pageIndex].let { lastPage ->
            lastPage.data = content
            lastPage.hasError = currentPageStatus
        }
        pageIndex =
            if (nextIndex < 0) {
                0
            } else if (nextIndex >= pages.size) {
                pages.size - 1
            } else {
                nextIndex
            }
        // Set contents of page to next page
        setContent(null, pages[pageIndex].data, true)
        reloadPsiFile()
    }

    fun setReloadEditor(fileEditor: Editor) {
        editor = fileEditor
    }

    fun reloadPsiFile() {
        launch {
            withContext(edtContext) {
                val psiFile = PsiManager.getInstance(editor.project!!).findFile((editor as EditorEx).virtualFile)
                WriteCommandAction.writeCommandAction(editor.project!!).withName("Reload Paged Collection $fileName")
                    .shouldRecordActionForActiveDocument(false)
                    .run<Exception> {
                        psiFile?.manager?.reloadFromDisk(psiFile)
                    }
            }
        }
    }
}

data class VirtualCollectionPage(
    override var data: CharSequence,
    override var hasError: Boolean,
) : VirtualFilePage(data, hasError)
