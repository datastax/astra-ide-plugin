package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.MessagesBundle.message
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.project.Project
import com.datastax.astra.jetbrains.utils.createNotificationExpiringAction
import com.datastax.astra.jetbrains.utils.createShowMoreInfoDialogAction
import com.datastax.astra.jetbrains.utils.notifyInfo
import com.datastax.astra.jetbrains.utils.notifyWarn

//TODO: Remove this class because I don't think its needed for profile manager function
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

//TODO:Set up plugin settings to hide these notifications if user wants to
fun invalidProfilesNotification(invalidProfiles: Map<String, Exception>) {
        val message = invalidProfiles.keys.joinToString("\n") { it ?: it::class.java.name }
        val errorDialogTitle = message("credentials.profile.invalid.title")
            notifyInfo(
                title = message("credentials.profile.invalid.title"),
                content = message("credentials.profile.invalid.count_message",invalidProfiles.size),
                notificationActions = listOf(
                    createNotificationExpiringAction(
                        ActionManager.getInstance().getAction("credentials.upsertCredentials")
                    ),
                    //TODO:Enable this once hiding added
                    //createNotificationExpiringAction(NeverShowAgain()),
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
            createNotificationExpiringAction( UserRegisterAction()),
            createNotificationExpiringAction(
                ActionManager.getInstance().getAction("credentials.upsertCredentials")
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
                ActionManager.getInstance().getAction("credentials.upsertCredentials")
            )
            //createNotificationExpiringAction(NeverShowAgain()),
        )
    )
}

//TODO: Notify if format of file is wrong
//TODO: Notify if no tokens matching format
//TODO: Notify if failed to authenticate
