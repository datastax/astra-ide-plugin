package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.stargate_document_v2.models.DocCollection
import com.intellij.openapi.fileEditor.*
import java.util.UUID.randomUUID
import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.telemetry.CrudEnum
import com.datastax.astra.jetbrains.telemetry.TelemetryManager
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.stargate_document_v2.infrastructure.Serializer
import com.google.gson.internal.LinkedTreeMap
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CollectionPagedBrowserPanel(
    val project: Project,
    val collection: DocCollection,
    val keyspace: com.datastax.astra.stargate_rest_v2.models.Keyspace,
    val database: Database
) : CoroutineScope by ApplicationThreadPoolScope("Table"), Disposable {

    protected val edtContext = getCoroutineUiContext(disposable = this)
    val gson = Serializer.gsonBuilder
        .setPrettyPrinting()
        .disableHtmlEscaping()
        //.enableComplexMapKeySerialization()
        .create()

    init {
        launch {
            loadInitialCollectionData()
        }
    }

    private suspend fun loadInitialCollectionData(){
        val response = AstraClient.documentApiForDatabase(database).getCollection(
            randomUUID(),git 
            AstraClient.accessToken,
            keyspace.name,
            collection.name.orEmpty(),
            null,
            null,
            "20"
        )
        if (response.isSuccessful && response.body()?.data != null) {
            TelemetryManager.trackStargateCrud("Collection", collection.name.orEmpty(), CrudEnum.READ, true)
            val newCollectionVirtualFile =  CollectionPagedVirtualFile(database,keyspace.name,collection.name.orEmpty())
            try {
                newCollectionVirtualFile.addData(response?.body()?.data!!)
                newCollectionVirtualFile.buildFilesAndSet()
            }catch (exception: Exception){
                println("Caught: $exception")
            }

            withContext(edtContext) {
                FileEditorManager.getInstance(project).openTextEditor(
                    OpenFileDescriptor(
                        project,
                        newCollectionVirtualFile
                    ),
                    true
                )
            }
        }
        else{
            TelemetryManager.trackStargateCrud("Collection", collection.name.orEmpty(), CrudEnum.READ, false)
        }
    }

    override fun dispose() {}
}






