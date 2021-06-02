package com.datastax.astra.jetbrains.credentials

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.project.Project
import com.datastax.astra.jetbrains.utils.createNotificationExpiringAction
import com.datastax.astra.jetbrains.utils.createShowMoreInfoDialogAction
import com.datastax.astra.jetbrains.utils.notifyInfo
import com.datastax.astra.jetbrains.utils.notifyWarn
import com.intellij.ide.BrowserUtil
import com.intellij.ui.components.ActionLink

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

//TODO:Set up plugin settings to hide these notifications if use wants to
fun invalidProfilesNotification(invalidProfiles: Map<String, Exception>) {
        val message = invalidProfiles.keys.joinToString("\n") { it ?: it::class.java.name }
        val errorDialogTitle = "Invalid Profiles Found"
            notifyInfo(
                title = "Invalid DataStax Astra profile(s)",
                content = "${invalidProfiles.size} invalid profile(s) found. " +
                        "Edit the file or select 'More Info' to see list of invalid profiles.",
                notificationActions = listOf(
                    createNotificationExpiringAction(
                        ActionManager.getInstance().getAction("credentials.upsertCredentials")
                    ),
                    //TODO:Enable this once hiding added
                    //createNotificationExpiringAction(NeverShowAgain()),
                    createShowMoreInfoDialogAction(
                        "More Info",
                        errorDialogTitle,
                        "The following profiles were found to be invalid:",
                        message
                    )
                )
            )
}

fun noProfilesFileNotification() {
    notifyInfo(
        title = "Failed to load DataStax Astra profiles",
        content = "Profiles file not found! Select register below if you need an account.\nOtherwise select edit to insert credentials.",
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
        title = "Failed to load DataStax Astra profiles",
        content = "Profiles file contains a format error. Edit the file below.",
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
