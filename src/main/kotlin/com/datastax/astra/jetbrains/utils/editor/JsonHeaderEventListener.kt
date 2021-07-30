package com.datastax.astra.jetbrains.utils.editor

import com.intellij.util.messages.Topic
import java.util.*

/**
 * Events relating to accessible astra resources changing
 */
interface JsonHeaderEventListener : EventListener {
    companion object {
        var TOPIC = Topic<JsonHeaderEventListener>(
            "Accesible Resources Change Events",
            JsonHeaderEventListener::class.java,
            Topic.BroadcastDirection.TO_PARENT
        )
    }

    //Te
    fun reloadFileEditorUIResources(databaseMap: Map<String,SimpleDatabase>)
}