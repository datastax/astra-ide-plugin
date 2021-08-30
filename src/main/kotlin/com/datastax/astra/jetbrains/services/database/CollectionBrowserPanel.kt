package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.telemetry.CrudEnum
import com.datastax.astra.jetbrains.telemetry.TelemetryManager
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.editor.ui.EndpointCollection
import com.datastax.astra.jetbrains.utils.getCoroutineUiContext
import com.datastax.astra.stargate_document_v2.infrastructure.Serializer
import com.datastax.astra.stargate_document_v2.models.DocCollection
import com.google.gson.internal.LinkedTreeMap
import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID.randomUUID

class CollectionBrowserPanel(
    val project: Project,
    val endpoint: EndpointCollection,
    val editOnOpen: Boolean = false,
    val whereSearchParam: String = "",
) : CoroutineScope by ApplicationThreadPoolScope("Collection"), Disposable {
    val collectionPagedFile = CollectionVirtualFile(endpoint,"")
    lateinit var openEditor: Editor
    protected val edtContext = getCoroutineUiContext(disposable = this)
    var prevPageState = ""

    val gson = Serializer.gsonBuilder
        .setPrettyPrinting()
        .disableHtmlEscaping()
        // .enableComplexMapKeySerialization()
        .create()

    init {
        launch {
            loadFirstPage()
        }
    }

    private suspend fun loadFirstPage() {
        val responseData = (loadPage() as? LinkedTreeMap<*, *>).orEmpty()

        // TODO: Revisit the null safety of assigning the editor
        if (responseData.isNotEmpty()) {
            TelemetryManager.trackStargateCrud("Collection", endpoint.collection, CrudEnum.READ, true)
            withContext(edtContext) {
                openEditor = FileEditorManager.getInstance(project).openTextEditor(
                    OpenFileDescriptor(
                        project,
                        collectionPagedFile
                    ),
                    true
                )!!
            }
            collectionPagedFile.whereSearchBox.text = whereSearchParam
            collectionPagedFile.setReloadEditor(openEditor)
            collectionPagedFile.addData(responseData)
            collectionPagedFile.buildPagesAndSet()
            // Make file read-only while we load the rest of it
            collectionPagedFile.isWritable = false
            loadRemainingPages()
        } else {
            TelemetryManager.trackStargateCrud("Collection", endpoint.collection, CrudEnum.READ, false)
        }
    }

    fun loadRemainingPages() {
        // Keep doing this until the previous page state is empty again. Indicating all pages have loaded.
        // TODO: Add a timeout in case the server keeps sending the same page-state back
        launch {
            while (prevPageState.isNotEmpty()) {
                collectionPagedFile.addData((loadPage() as? LinkedTreeMap<*, *>).orEmpty())
            }
            collectionPagedFile.isWritable = true
            collectionPagedFile.buildPagesAndSet()
            withContext(edtContext) {
                collectionPagedFile.unlock()
            }
        }
    }

    private suspend fun loadPage(): Any? {
        val response = AstraClient.documentApiForDatabase(endpoint.database).searchDoc(
            randomUUID(),
            AstraClient.accessToken,
            endpoint.keyspace,
            endpoint.collection,
            null,
            whereSearchParam.ifEmpty { null },
            pageSize = 5,
            // If page state is empty send null so query isn't sent.
            pageState = prevPageState.ifEmpty { null }

        )

        when (response.code()) {
            200 -> {
                if (response.body()?.data != null) {
                    prevPageState = response.body()?.pageState.orEmpty()
                }
            }
            400 -> {
                // TODO:
                // Telemetry
                // Notify user: 400 Error and something not scary but useful, "Failed to load a page", etc
            }
            401 -> {
                // TODO:
                // Telemetry
                // Notify user: 401 Error. Not authorized
            }
            else -> {
                // TODO:
                // Telemetry
                // Notify user: Error Code. Error during retrieval, "Failed to load a page", etc
            }
        }
        return response.body()?.data
    }

    override fun dispose() {}
}
