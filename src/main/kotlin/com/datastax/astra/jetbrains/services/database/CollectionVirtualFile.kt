package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.stargate_document_v2.models.DocCollection
import com.datastax.astra.stargate_rest_v2.models.Table
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.testFramework.LightVirtualFile
import com.intellij.util.castSafelyTo
import kotlinx.coroutines.CoroutineScope
import java.util.*

class CollectionVirtualFile(val collectionName: String, val collectionJson: String) :
    LightVirtualFile(
        collectionName,
        FileTypeManager.getInstance().getFileTypeByExtension("JSON"),
        collectionJson
    ),
    CoroutineScope by ApplicationThreadPoolScope("Table")
