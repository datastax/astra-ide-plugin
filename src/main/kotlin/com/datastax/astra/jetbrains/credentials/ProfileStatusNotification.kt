package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.utils.createNotificationExpiringAction
import com.datastax.astra.jetbrains.utils.createShowMoreInfoDialogAction
import com.datastax.astra.jetbrains.utils.notifyInfo
import com.intellij.openapi.actionSystem.ActionManager

// TODO:Set up plugin settings to hide these notifications if user wants to
fun invalidProfilesNotification(invalidProfiles: Map<String, Exception>) {
    var message = ""
    invalidProfiles.forEach { message += "Profile:${it.key}  Error:${it.value.message}\n" }
    val errorDialogTitle = message("credentials.profile.invalid.title")
    notifyInfo(
        title = message("credentials.profile.invalid.title"),
        content = message("credentials.profile.invalid.count_message", invalidProfiles.size),
        notificationActions = listOf(
            createNotificationExpiringAction(
                ActionManager.getInstance().getAction("credentials.upsert")
            ),
            // TODO:Enable this once hiding added
            // createNotificationExpiringAction(NeverShowAgain()),
            createShowMoreInfoDialogAction(
                message("credentials.profile.invalid.more_info"),
                errorDialogTitle,
                message("credentials.profile.invalid.message"),
                message
            )
        )
    )
}

fun noProfilesFileNotification() {
    notifyInfo(
        title = message("credentials.file.load_failed.title"),
        content = message("credentials.file.not_found"),
        notificationActions = listOf(
            createNotificationExpiringAction(GetTokenAction()),
            createNotificationExpiringAction(
                ActionManager.getInstance().getAction("credentials.upsert")
            )
        )
    )
}

fun wrongProfilesFormatNotification() {
    notifyInfo(
        title = message("credentials.file.load_failed.title"),
        content = message("credentials.file.invalid_format"),
        notificationActions = listOf(
            createNotificationExpiringAction(
                ActionManager.getInstance().getAction("credentials.upsert")
            )
            // createNotificationExpiringAction(NeverShowAgain()),
        )
    )
}

fun profileFileModifiedNotification() {
    notifyInfo(
        title = message("credentials.file.modified.title"),
        content = message("credentials.file.modified.message"),
        notificationActions = listOf(
            createNotificationExpiringAction(ReloadProfilesAction()),
            // createNotificationExpiringAction(NeverShowAgain()),
        )
    )
}

fun generateTokenFailure() {
    notifyInfo(
        title = message("credentials.get_token.failed.title"),
        content = message("credentials.get_token.failed.body"),
        notificationActions = listOf(
            createNotificationExpiringAction(GetTokenAction()),
            createNotificationExpiringAction(
                ActionManager.getInstance().getAction("credentials.upsert")
            )
            // createNotificationExpiringAction(NeverShowAgain()),
        )
    )
}

// TODO: Notify if format of file is wrong
// TODO: Notify if no tokens matching format
// TODO: Notify if failed to authenticate
