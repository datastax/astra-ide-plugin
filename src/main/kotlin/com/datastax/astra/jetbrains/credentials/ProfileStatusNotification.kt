package com.datastax.astra.jetbrains.credentials

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.project.Project
import com.datastax.astra.jetbrains.utils.createNotificationExpiringAction
import com.datastax.astra.jetbrains.utils.createShowMoreInfoDialogAction
import com.datastax.astra.jetbrains.utils.notifyInfo
import com.datastax.astra.jetbrains.utils.notifyWarn

class ProfileStatusNotification(private val project: Project) : ProfileStateChangeNotifier {
    private val actionManager = ActionManager.getInstance()
    override fun profileStateChanged(newState: ProfileState) {
        if (newState is ProfileState.InvalidProfile) {
            val title = "credentials.invalid.title"
            val message = "credentials.invalid.description"

            notifyWarn(
                project = project,
                title = title,
                content = message,
                notificationActions = listOf(
                    createShowMoreInfoDialogAction(
                        "credentials.invalid.more_info",
                        title,
                        message,
                        newState.displayMessage
                    ),
                    createNotificationExpiringAction(actionManager.getAction("aws.settings.upsertCredentials")),
                    //createNotificationExpiringAction(RefreshConnectionAction(message("settings.retry")))
                )
            )
        }
    }
}
fun invalidProfilesNotification(invalidProfiles: Map<String, Exception>) {
        val message = invalidProfiles.values.joinToString("\n") { it.message ?: it::class.java.name }

        val errorDialogTitle = "credentials.profile.failed_load"
        val numErrorMessage = "credentials.profile.refresh_errors"

        //TODO:Set up plugin settings to hide these notifications

            notifyInfo(
                title = "My title",
                content = "Foobar $numErrorMessage",
                notificationActions = listOf(
                    createNotificationExpiringAction(
                        ActionManager.getInstance().getAction("credentials.upsertCredentials")
                    ),
                    //createNotificationExpiringAction(NeverShowAgain()),
                    createShowMoreInfoDialogAction(
                        "credentials.invalid.more_info",
                        errorDialogTitle,
                        numErrorMessage,
                        message
                    )
                )
            )
}


//TODO: Notify if format of file is wrong
//TODO: Notify if no tokens matching format
//TODO: Notify if failed to authenticate
//TODO: Notify if no file