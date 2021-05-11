package com.datastax.astra.jetbrains.credentials

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.project.Project
import com.datastax.astra.jetbrains.utils.createNotificationExpiringAction
import com.datastax.astra.jetbrains.utils.createShowMoreInfoDialogAction
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
                    //createNotificationExpiringAction(actionManager.getAction("aws.settings.upsertCredentials")),
                    //createNotificationExpiringAction(RefreshConnectionAction(message("settings.retry")))
                )
            )
        }
    }
}
