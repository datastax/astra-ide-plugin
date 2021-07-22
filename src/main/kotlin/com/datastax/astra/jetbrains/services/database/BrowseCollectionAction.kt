package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.explorer.CollectionNode
import com.datastax.astra.jetbrains.explorer.ExplorerDataKeys
import com.datastax.astra.jetbrains.telemetry.CrudEnum
import com.datastax.astra.jetbrains.telemetry.TelemetryManager.trackStargateCrud
import com.intellij.json.json5.Json5FileType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.testFramework.LightVirtualFile
import kotlinx.coroutines.runBlocking

class BrowseCollectionAction : DumbAwareAction(message("collection.open.title"), null, null) {
    override fun actionPerformed(e: AnActionEvent) {
        e.getData(ExplorerDataKeys.SELECTED_NODES)?.map { it as? CollectionNode }?.singleOrNull()?.run {
            var node = this
            runBlocking {
                showCollection(node.nodeProject, node.collection, node.keyspace, node.database)
            }
                trackStargateCrud("Collection", this.collection.name!!, CrudEnum.READ, true)
            }
        }
    }

