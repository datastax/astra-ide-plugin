package com.datastax.astra.jetbrains.telemetry

import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.credentials.CredentialsClient
import com.datastax.astra.jetbrains.credentials.ProfileManager
import com.intellij.openapi.application.ApplicationInfo
import com.segment.analytics.Analytics
import com.segment.analytics.messages.IdentifyMessage
import com.segment.analytics.messages.TrackMessage
import kotlinx.coroutines.runBlocking
import java.util.UUID.randomUUID

// TODO: Rewrite this as a service and not a static class
// Possibly expand this into more than one class at that time
object TelemetryManager {
    // TODO: Determine what all to track long term for:
    //  Crud Actions, External Actions, Changing Users (use aliases?)

    // TODO: Figure out safest way to store this
    var telClient = Analytics.builder(this::class.java.getResource("/telConfig").readText()).build()

    // Might not need to store a local version if written as a service
    var knownToken = ""
    var knownOrg = ""
    var anonMode = false

    // Make sure the "user" hasn't changed
    // TODO: Make this an active process that occurs as part of the credential change process
    fun checkCreds() {
        val activeToken = ProfileManager.getInstance(AstraClient.project).activeProfile?.token.toString()
        // If there were no tokens to use don't change the profile since we can't without an ID
        // This should be cleaner once moved into a service
        if (activeToken != knownToken && activeToken != "null") {
            knownToken = activeToken
            runBlocking {
                knownOrg = CredentialsClient.operationsApi(activeToken).getCurrentOrganization().body()!!.id
            }
            // TODO: Re-enable this with userID change
            /*if(activeOrg != knownOrg){
                knownOrg = activeOrg
                trackOrgChange()
            }*/
            anonMode = false
            trackProfileChange()
        }
        // Only track a user if there's not currently a userID
        else if (!anonMode && knownOrg == "") {
            knownOrg = randomUUID().toString()
            anonMode = true
            trackProfileChange()
        }
    }

    fun trackProfileChange() {
        telClient.enqueue(
            IdentifyMessage.builder()
                .userId(knownOrg)
                .traits(
                    mapOf(
                        "idea ver" to ApplicationInfo.getInstance().getStrictVersion(),
                        "anonymous" to anonMode.toString(),
                    )
                )
        )
    }

    fun trackAction(actionName: String, actionMetaData: Map<String, String>) {
        checkCreds()
        telClient.enqueue(
            TrackMessage.builder(actionName)
                .userId(knownOrg)
                .properties(actionMetaData)
        )
    }

    fun trackAction(actionName: String) {
        checkCreds()
        telClient.enqueue(
            TrackMessage.builder(actionName)
                .userId(knownOrg)
        )
    }

    fun trackDevOpsCrud(resourceType: String, resourceName: String, operation: CrudEnum, completed: Boolean,) {
        checkCreds()
        telClient.enqueue(
            TrackMessage.builder("DevOps CRUD")
                .userId(knownOrg)
                .properties(
                    mapOf(
                        "resource" to resourceType,
                        "name" to resourceName,
                        "operation" to operation.toString(),
                        "completed" to completed.toString(),
                    )
                )
        )
    }

    // Same as above but with an extra data load for kludging extra features
    fun trackDevOpsCrud(resourceType: String, resourceName: String, operation: CrudEnum, completed: Boolean, extraData: Map<String, String>,) {
        checkCreds()
        telClient.enqueue(
            TrackMessage.builder("DevOps CRUD")
                .userId(knownOrg)
                .properties(
                    mapOf(
                        "resource" to resourceType,
                        "name" to resourceName,
                        "operation" to operation.toString(),
                        "completed" to completed.toString(),
                    ) + extraData
                )
        )
    }

    fun trackStargateCrud(resourceType: String, resourceName: String, operation: CrudEnum, completed: Boolean,) {
        checkCreds()
        telClient.enqueue(
            TrackMessage.builder("Stargate CRUD")
                .userId(knownOrg)
                .properties(
                    mapOf(
                        "resource" to resourceType,
                        "name" to resourceName,
                        "operation" to operation.toString(),
                        "completed" to completed.toString(),
                    )
                )
        )
    }

    // Same as above but with an extra data load for kludging extra features
    fun trackStargateCrud(resourceType: String, resourceName: String, operation: CrudEnum, completed: Boolean, extraData: Map<String, String>) {
        checkCreds()
        telClient.enqueue(
            TrackMessage.builder("Stargate CRUD")
                .userId(knownOrg)
                .properties(
                    mapOf(
                        "resource" to resourceType,
                        "name" to resourceName,
                        "operation" to operation.toString(),
                        "completed" to completed.toString(),
                    ) + extraData
                )
        )
    }

    // For tracking behaviors outside the plugin
    fun trackClick(actionTarget: ClickTarget, actionBehavior: String) {
        checkCreds()
        telClient.enqueue(
            TrackMessage.builder("Click Action")
                .userId(knownOrg)
                .properties(
                    mapOf(
                        "target" to actionTarget.toString(),
                        "type" to actionBehavior,
                    )
                )
        )
    }

    fun trackClick(actionTarget: ClickTarget, actionBehavior: String, extraData: Map<String, String>) {
        checkCreds()
        telClient.enqueue(
            TrackMessage.builder("Click Action")
                .userId(knownOrg)
                .properties(
                    mapOf(
                        "target" to actionTarget.toString(),
                        "type" to actionBehavior,
                    ) + extraData
                )
        )
    }

    // TODO:Track orgID through groupID once a way to track users is identified
    // Possibly associate users through aliases
    /*fun trackOrgChange(){
        telClient.enqueue(
            GroupMessage.builder("some-group-id")
                .userId(activeOrg)
                .traits(mapOf(
                    "name" to "Org Name",
                    "size" to "Number of users")
                )
        )
    }*/
}
enum class CrudEnum {
    CREATE, READ, UPDATE, DELETE
}
enum class ClickTarget {
    BUTTON, LINK
}
