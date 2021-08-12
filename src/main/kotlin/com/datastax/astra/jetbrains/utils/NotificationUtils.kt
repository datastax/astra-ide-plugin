package com.datastax.astra.jetbrains.utils

import com.datastax.astra.jetbrains.MessagesBundle.message
import com.intellij.notification.*
import com.intellij.notification.Notifications.Bus.notify
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.ui.ScrollPaneFactory
import javax.swing.JLabel
import javax.swing.JTextArea

// This was const, but we know our message bundle should be constant (unless changing languages a lot?) so I removed it to play nice with the message bundle
private val GROUP_DISPLAY_ID = message("utilities.notification.group_display_id")

private fun notify(type: NotificationType, title: String, content: String = "", project: Project? = null, notificationActions: Collection<AnAction>) {
    val notification = Notification(GROUP_DISPLAY_ID, title, content, type)
    notificationActions.forEach {
        notification.addAction(if (it !is NotificationAction) createNotificationExpiringAction(it) else it)
    }
    notify(notification, project)
}

fun notifyInfo(title: String, content: String = "", project: Project? = null, notificationActions: Collection<AnAction>) =
    notify(NotificationType.INFORMATION, title, content, project, notificationActions)

fun notifyWarn(title: String, content: String = "", project: Project? = null, notificationActions: Collection<AnAction>) =
    notify(NotificationType.WARNING, title, content, project, notificationActions)

/**
 * Creates a Notification Action that will expire a notification after performing some AnAction
 */
fun createNotificationExpiringAction(action: AnAction): NotificationAction = NotificationAction.create(
    action.templatePresentation.text
) { actionEvent, notification ->
    action.actionPerformed(actionEvent)
}

fun createShowMoreInfoDialogAction(actionName: String?, title: String?, message: String?, moreInfo: String?) =
    object : AnAction(actionName) {
        override fun isDumbAware() = true

        override fun actionPerformed(e: AnActionEvent) {
            val dialogTitle = title ?: ""

            val textArea = JTextArea(moreInfo).apply {
                columns = 56
                rows = 5
                lineWrap = true
                wrapStyleWord = true
                isEditable = false
            }

            val dialogBuilder = DialogBuilder().apply {
                setTitle(dialogTitle)
                setNorthPanel(JLabel(message))
                setCenterPanel(ScrollPaneFactory.createScrollPane(textArea))
                setPreferredFocusComponent(textArea)
                removeAllActions()
                addOkAction()
            }
            dialogBuilder.show()
        }
    }
