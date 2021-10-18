package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.jetbrains.MessagesBundle
import com.datastax.astra.jetbrains.explorer.KeyspaceNode
import com.datastax.astra.jetbrains.explorer.TableEndpoint
import com.datastax.astra.jetbrains.utils.createShowMoreInfoDialogAction
import com.datastax.astra.jetbrains.utils.notifyError
import com.datastax.astra.jetbrains.utils.notifyInfo
import com.datastax.astra.jetbrains.utils.notifyWarn
import retrofit2.Response

/*
ERROR NOTIFICATIONS
 */
fun notifyLoadRowsError(
    endpoint: TableEndpoint,
    pageNumber: Int,
    pageHash: String,
    whereQuery: String,
    errors: Pair<String, String>,
) {
    val tName = endpoint.table.name.orEmpty().ifEmpty { "[unnamed_table]" }
    notifyError(
        title = "Failed to load rows from remote table: '$tName'",
        content = "Error loading rows from remote table: '$tName' in keyspace: '${endpoint.keyspace.name}' in database: '${endpoint.database.info.name}'. Click below to see error info",
        notificationActions = listOf(
            createShowMoreInfoDialogAction(
                "Error Info",
                "Failed to load rows from remote table: '$tName'",
                "Error in response to loading page: '$pageNumber' with pageState: '${pageHash.ifEmpty { "[none]" }}' and where query: '${whereQuery.ifEmpty { "[none]" }}'",
                "HTTP Response:\n${errors.first}\n\nError Body:\n${errors.second}"
            )
        )
    )
}

fun notifyUpdateRowError(
    tableName: String,
    rowId: String,
    columnName: String,
    oldValue: String,
    newValue: String,
    errors: Pair<String, String>,
) {
    notifyError(
        title = "Failed to update remote table: ${tableName.ifEmpty { "[unnamed_table]" }}",
        content = "Error changing data on row: '${rowId}' in column: '${columnName}'. Attempted to replace: '$oldValue' with '$newValue'. Click below to see error info",
        notificationActions = listOf(
            createShowMoreInfoDialogAction(
                "Error Info",
                "Failed to update remote table: '${tableName.ifEmpty { "[unnamed_table]" }}'",
                "Error in response to changes on row: '$rowId' in column: '$columnName'",
                "HTTP Response:\n${errors.first}\n\nError Body:\n${errors.second}"
            )
        )
    )
}



fun notifyLoadDocsError(
    databaseName: String,
    keyspaceName: String,
    collectionName: String,

    pageNumber: Int,
    pageHash: String,
    whereQuery: String,
    errors: Pair<String, String>,
) {
    val cName = collectionName.ifEmpty { "[unnamed_table]" }
    notifyError(
        title = "Failed to load docs from remote collection: '$cName'",
        content = "Error loading documents from remote collection: '$cName' in keyspace: '$keyspaceName' in database: '$databaseName'. Click below to see error info",
        notificationActions = listOf(
            createShowMoreInfoDialogAction(
                "Error Info",
                "Failed to load docs from remote collection: '$cName'",
                "Error in response to loading page: '$pageNumber' with pageState: '${pageHash.ifEmpty { "[none]" }}' and where query: '${whereQuery.ifEmpty { "[none]" }}'",
                "HTTP Response:\n${errors.first}\n\nError Body:\n${errors.second}"
            )
        )
    )
}

fun notifyUpdateDocError(
    databaseName: String,
    keyspaceName: String,
    collectionName: String,
    documentId: String,
    errors: Pair<String, String>,
) {
    val cName = collectionName.ifEmpty { "[unnamed_table]" }
    notifyError(
        title = "Failed to update remote collection: '$cName'",
        content = "Error changing data on document: '$documentId' in collection: '$cName' in keyspace: '$keyspaceName' in database: '$databaseName'. Click below to see error info",
        notificationActions = listOf(
            createShowMoreInfoDialogAction(
                "Error Info",
                "Failed to update document in remote collection '$cName'",
                "Error in response to changes on document '$documentId'",
                "HTTP Response:\n${errors.first}\n\nError Body:\n${errors.second}"
            )
        )
    )
}

fun notifyCreateCollectionError(
    collectionName: String,
    node: KeyspaceNode,
    errors: Pair<String, String>,
) {
    notifyError(
        title = "Failed to create collection: $collectionName",
        content = "Error creating collection: $collectionName in keyspace: ${node.keyspace.name} for database ${node.database}. Click below to see error info",
        notificationActions = listOf(
            createShowMoreInfoDialogAction(
                "Error Info",
                "Failed to create collection: $collectionName",
                "Error in response to creating new collection named: '$collectionName' in keyspace: ${node.keyspace.name}",
                "HTTP Response:\n${errors.first}\n\nError Body:\n${errors.second}"
            )
        )
    )
}

/*
INFO NOTIFICATIONS
 */

fun notifyCreateCollectionSuccess(
    collectionName: String,
    node: KeyspaceNode,
) {
    notifyInfo(
        title = "Created new collection: $collectionName",
        content = "Successfully created collection: $collectionName in keyspace: ${node.keyspace.name} for database ${node.database.info.name}. Now refreshing explorer list to reflect change.",
        notificationActions = listOf()
    )
}