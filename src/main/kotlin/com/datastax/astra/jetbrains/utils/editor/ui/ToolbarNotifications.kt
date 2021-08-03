package com.datastax.astra.jetbrains.utils.editor.ui

import com.datastax.astra.devops_v2.infrastructure.getErrorResponse
import com.datastax.astra.jetbrains.MessagesBundle
import com.datastax.astra.jetbrains.utils.createNotificationExpiringAction
import com.datastax.astra.jetbrains.utils.createShowMoreInfoDialogAction
import com.datastax.astra.jetbrains.utils.notifyInfo
import com.datastax.astra.jetbrains.utils.notifyWarn
import com.intellij.openapi.actionSystem.ActionManager
import retrofit2.Response

fun failedDocUpsertNotification(invalidUpserts: Map<String, Response<Unit>>) {
    var message = ""
    invalidUpserts.forEach { message += "Document:${it.key} Code:${it.value.code()} Msg:${it.value.message()} \n Description:${it.value.getErrorResponse<Any>()}\n" }
    val errorDialogTitle = MessagesBundle.message("collection.editor.upsert.failed.title")
    notifyInfo(
        title = MessagesBundle.message("collection.editor.upsert.failed.title"),
        content = MessagesBundle.message("collection.editor.upsert.failed.count_message", invalidUpserts.size),
        notificationActions = listOf(
            // TODO:Enable this once hiding added
            // createNotificationExpiringAction(NeverShowAgain()),
            createShowMoreInfoDialogAction(
                MessagesBundle.message("collection.editor.upsert.failed.more_info"),
                errorDialogTitle,
                MessagesBundle.message("collection.editor.upsert.failed.message"),
                message
            )
        )
    )
}

fun wrongJsonFormatNotification() {
    notifyWarn(
        title = MessagesBundle.message("collection.editor.upsert.invalid.title"),
        content = MessagesBundle.message("collection.editor.upsert.invalid.message"),
        notificationActions = emptyList()
            // createNotificationExpiringAction(NeverShowAgain()),
    )

}

fun noEndpointSelected(){
    notifyWarn(
        title = "Upsert Endpoint Invalid",
        content = "An endpoint must be selected to upsert documents into a collection",
        notificationActions = emptyList(),

            // createNotificationExpiringAction(NeverShowAgain()),
    )

}

