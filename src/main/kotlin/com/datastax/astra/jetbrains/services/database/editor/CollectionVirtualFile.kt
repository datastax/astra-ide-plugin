package com.datastax.astra.jetbrains.services.database.editor

import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.testFramework.LightVirtualFile
import kotlinx.coroutines.CoroutineScope
import java.util.*

class CollectionVirtualFile(val database: Database, val keyspaceName: String, val collectionName: String, val collectionJson: String) :
    LightVirtualFile(
        collectionName,
        FileTypeManager.getInstance().getFileTypeByExtension("JSON"),
        collectionJson
    )
