package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.jetbrains.MessagesBundle
import com.datastax.astra.jetbrains.utils.createShowMoreInfoDialogAction
import com.datastax.astra.jetbrains.utils.notifyInfo
import com.datastax.astra.jetbrains.utils.notifyWarn
import retrofit2.Response


    fun failedRowUpdateNotification(
        tableName: String,
        rowId: String,
        columnName: String,
        oldValue: String,
        newValue: String,
        errors: Pair<String, String>
    ) {
        notifyWarn(
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
