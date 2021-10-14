package com.datastax.astra.jetbrains.services.database.editor

import com.datastax.astra.jetbrains.explorer.TableEndpoint
import com.intellij.testFramework.LightVirtualFile

class TableVirtualFile(val endpoint: TableEndpoint) :
    LightVirtualFile(endpoint.table?.name.orEmpty()) {
    override fun equals(other: Any?): Boolean {
        if (other !is TableVirtualFile) {
            return false
        }
        return (other as? TableVirtualFile)?.endpoint == endpoint
    }
}
