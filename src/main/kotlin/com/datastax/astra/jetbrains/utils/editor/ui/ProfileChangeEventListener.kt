package com.datastax.astra.jetbrains.utils.editor.ui

import com.intellij.util.messages.Topic
import java.util.*

/**
 * Events relating to accessible astra resources changing
 */
interface ProfileChangeEventListener : EventListener {
    companion object {
        var TOPIC = Topic<ProfileChangeEventListener>(
            "Accesible Resources Change Events",
            ProfileChangeEventListener::class.java,
            Topic.BroadcastDirection.TO_PARENT
        )
    }

    // TODO: Subscribe all JsonEditorComboBoxes objects to this event topic. Rebuild the databaseMap then pass it to listeners if tree invalidated
    fun reloadFileEditorUIResources(databaseList: List<SimpleDatabase>)

    fun clearFileEditorUIResources()
}
/**
 * Events relating to accessible astra resources changing
 */
interface ExplorerTreeChangeEventListener : EventListener {
    companion object {
        var TOPIC = Topic<ExplorerTreeChangeEventListener>(
            "Tree Resources Change Events",
            ExplorerTreeChangeEventListener::class.java,
            Topic.BroadcastDirection.TO_PARENT
        )
    }

    // TODO: Subscribe all JsonEditorComboBoxes objects to this event topic. Rebuild the databaseMap then pass it to listeners if tree invalidated
    fun rebuildEndpointList()

}
