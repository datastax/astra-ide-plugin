package com.datastax.astra.jetbrains.services.database.editor

import com.datastax.astra.devops_v2.models.Database
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.testFramework.LightVirtualFile

class CollectionVirtualFile(
    val database: Database,
    val keyspaceName: String,
    val collectionName: String,
    val collectionJson: String,
) :
    LightVirtualFile(
        collectionName,
        FileTypeManager.getInstance().getFileTypeByExtension("JSON"),
        collectionJson
    ) {
    override fun equals(other: Any?): Boolean {
        if (other !is CollectionVirtualFile) {
            return false
        }
        return other.database == this.database &&
                other.keyspaceName == this.keyspaceName &&
                other.collectionName == this.collectionName

    }
}
