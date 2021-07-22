package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.stargate_document_v2.models.DocCollection
import com.datastax.astra.stargate_document_v2.models.Keyspace
import com.datastax.astra.stargate_rest_v2.models.Table
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.AppUIExecutor
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.impl.coroutineDispatchingContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.PossiblyDumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFile
import com.intellij.util.castSafelyTo
import kotlinx.coroutines.CoroutineScope
import java.beans.PropertyChangeListener
import java.util.UUID.randomUUID



    suspend fun showCollection(project: Project, collection: DocCollection, keyspace: com.datastax.astra.stargate_rest_v2.models.Keyspace, database: Database): Editor? {
        return FileEditorManager.getInstance(project).openTextEditor(
            OpenFileDescriptor(
                project,
                LightVirtualFile(
                    collection?.name.orEmpty(),
                    FileTypeManager.getInstance().getFileTypeByExtension("JSON"),
                    AstraClient.documentApiForDatabase(database).getCollection(
                        randomUUID(),
                        AstraClient.accessToken,
                        keyspace.name,
                        collection.name,
                        true
                    ).body()?.data.castSafelyTo<CharSequence>()!!
                )
            ),
            true
        )

    }

