package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.jetbrains.MessagesBundle
import com.datastax.astra.jetbrains.explorer.TableEndpoint
import com.datastax.astra.jetbrains.utils.createShowMoreInfoDialogAction
import com.datastax.astra.jetbrains.utils.notifyError
import com.datastax.astra.jetbrains.utils.notifyInfo
import com.datastax.astra.jetbrains.utils.notifyWarn
import retrofit2.Response


fun notifyUpdateRowError(
    tableName: String,
    rowId: String,
    columnName: String,
    oldValue: String,
    newValue: String,
    errors: Pair<String, String>,
) {
    notifyError(
        title = "Failed to update remote table ${tableName.ifEmpty { "[unnamed_table]" }}",
        content = "Failed to change data on row '${rowId}' in column '${columnName}'. Attempted to replace '$oldValue' with '$newValue'. Click below to see more info",
        notificationActions = listOf(
            createShowMoreInfoDialogAction(
                "More Info",
                "Failed to update row in remote table '${tableName.ifEmpty { "[unnamed_table]" }}'",
                "Error in response to changes on row '$rowId' in column '$columnName'",
                "HTTP Response:\n${errors.first}\n\nError Body:\n${errors.second}"
            )
        )
    )
}

fun notifyUpdateDocError(
    tableName: String,
    rowId: String,
    columnName: String,
    oldValue: String,
    newValue: String,
    errors: Pair<String, String>,
) {
    notifyError(
        title = "Failed to update remote table: '${tableName.ifEmpty { "[unnamed_table]" }}'",
        content = "Failed to change data on row: '${rowId}' in column: '${columnName}'. Attempted to replace: '$oldValue' with '$newValue'. Click below to see error info",
        notificationActions = listOf(
            createShowMoreInfoDialogAction(
                "More Info",
                "Failed to update row in remote table: '${tableName.ifEmpty { "[unnamed_table]" }}'",
                "Error in response to changes on row: '$rowId' in column: '$columnName'",
                "HTTP Response:\n${errors.first}\n\nError Body:\n${errors.second}"
            )
        )
    )
}

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
                "Error loading rows from remote table: '$tName'",
                "Error in response to loading page: '$pageNumber' with pageState: '${pageHash.ifEmpty { "[none]" }}' and where query: '${whereQuery.ifEmpty { "[none]" }}'",
                "HTTP Response:\n${errors.first}\n\nError Body:\n${errors.second}"
            )
        )
    )
}

fun notifyLoadDocsError(
    tableName: String,
    rowId: String,
    columnName: String,
    oldValue: String,
    newValue: String,
    errors: Pair<String, String>,
) {
    notifyWarn(
        title = "Failed to update remote table ${tableName.ifEmpty { "[unnamed_table]" }}",
        content = "Failed to change data on row '${rowId}' in column '${columnName}'. Attempted to replace '$oldValue' with '$newValue'. Click below to see more info",
        notificationActions = listOf(
            createShowMoreInfoDialogAction(
                "Error Info",
                "Failed to update row in remote table '${tableName.ifEmpty { "[unnamed_table]" }}'",
                "Error in response to changes on row '$rowId' in column '$columnName'",
                "HTTP Response:\n${errors.first}\n\nError Body:\n${errors.second}"
            )
        )
    )
}