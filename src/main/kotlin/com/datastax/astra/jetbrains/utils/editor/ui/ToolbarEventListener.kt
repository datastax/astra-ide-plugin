package com.datastax.astra.jetbrains.utils.editor.ui

import com.intellij.util.messages.Topic
import java.util.*

/**
 * Events relating to accessible astra resources changing
 */
interface ToolbarEventListener : EventListener {
    companion object {
        var TOPIC = Topic<ToolbarEventListener>(
            "Accesible Resources Change Events",
            ToolbarEventListener::class.java,
            Topic.BroadcastDirection.TO_PARENT
        )
    }

    // TODO: Subscribe all JsonEditorComboBoxes objects to this event topic. Rebuild the databaseMap then pass it to listeners if tree invalidated
    fun reloadFileEditorUIResources(databaseMap: Map<String, SimpleDatabase>)
}