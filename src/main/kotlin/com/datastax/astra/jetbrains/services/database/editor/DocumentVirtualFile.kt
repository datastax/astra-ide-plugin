package com.datastax.astra.jetbrains.services.database.editor

import com.datastax.astra.devops_v2.models.Database
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.testFramework.LightVirtualFile

class DocumentVirtualFile(
    val database: Database,
    val keyspaceName: String,
    val collectionName: String,
    val documentId: String,
    val documentJson: String
) :
    LightVirtualFile(
        documentId + ".json",
        FileTypeManager.getInstance().getFileTypeByExtension("JSON"),
        documentJson
    )

