package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.explorer.*
import com.datastax.astra.jetbrains.explorer.ExplorerDataKeys.SELECTED_NODES
import com.datastax.astra.jetbrains.telemetry.CrudEnum
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.editor.ui.ExplorerTreeChangeEventListener
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.util.ui.tree.TreeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.toRequestBody
import org.jetbrains.annotations.NotNull
import javax.swing.tree.DefaultMutableTreeNode

class CreateCollectionAction:
        DumbAwareAction("Create Collection", "Create collection in keyspace", AllIcons.Actions.NewFolder),
        CoroutineScope by ApplicationThreadPoolScope("Database") {

        override fun actionPerformed(e: AnActionEvent) {
            val project = e.getRequiredData(LangDataKeys.PROJECT)
            e.getData(SELECTED_NODES)?.map { it as? KeyspaceNode }?.singleOrNull()?.run {
                val dialog = CreateCollectionDialog(this, e.getRequiredData(LangDataKeys.PROJECT))
                if (dialog.showAndGet()) {
                    val collectionName = dialog.name
                    val node = this
                    launch {
                        val response = AstraClient.documentApiForDatabase(database).createCollection(
                            AstraClient.accessToken,
                            keyspace.name,
                            """{"name":"$collectionName"}""".toRequestBody()
                        )
                        if (response.isSuccessful) {
                            nodeProject.messageBus.syncPublisher(ExplorerTreeChangeEventListener.TOPIC).rebuildEndpointList()
                            val databaseParent =
                                TreeUtil.findNode(ExplorerToolWindow.getInstance(project).tree.model.root as @NotNull DefaultMutableTreeNode) {
                                    it.userObject is DatabaseParentNode
                                }?.userObject as DatabaseParentNode
                            databaseParent.clearCache()
                            project.refreshTree(databaseParent, true)
                            // This is disabled for now to reduce potential data leakage
                            // val databaseId = response.headers()["Location"]
                            notifyCreateCollectionSuccess(collectionName,node)
                            //TODO: Telemetry
                            println("Yay")
                        } else {
                            notifyCreateCollectionError(collectionName,node,Pair(response.toString(),response.getErrorResponse<Any?>().toString()))
                            //TODO: Telemetry
                            //trackDevOpsCrud("Database", name, CrudEnum.CREATE, false, mapOf("httpError" to response.getErrorResponse<Any?>().toString(), "httpResponse" to response.toString()))
                        }
                    }
                }
            }
        }

        // If DB is processing grey out access to creating a keyspace
        override fun update(e: AnActionEvent) {
            if (e.getData(SELECTED_NODES)?.map { it as? DatabaseNode }?.singleOrNull()?.database?.status?.isProcessing() == true) {
                e.presentation.setEnabled(false)
            }
        }
    }
