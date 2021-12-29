package com.datastax.astra.jetbrains.telemetry

import com.datastax.astra.jetbrains.credentials.CredentialsClient
import com.datastax.astra.jetbrains.credentials.ProfileListener
import com.datastax.astra.jetbrains.credentials.ProfileManager
import com.datastax.astra.jetbrains.credentials.ProfileToken
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.WindowManager
import com.segment.analytics.Analytics
import com.segment.analytics.messages.IdentifyMessage
import com.segment.analytics.messages.TrackMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID.randomUUID

class TelemetryService(private val project: Project) : Disposable, ProfileListener {

    var writeKey = this::class.java.getResource("/telConfig").readText().trim()
    var telClient = Analytics.builder(writeKey).build()

    //TODO: Store a persistent identifier
    var randomOrg = randomUUID().toString()

    var anonMode = false
    var org = ""

    var profileJob: Job? = null

    init {
        project.service<ProfileManager>().addListener(this)
        updateTrackedIdentity(project.service<ProfileManager>().activeProfile)
    }

    /**
     * Usually not invoked directly, see class javadoc.
     */
    override fun dispose() {
    }

    override fun onActiveProfileChanged(profile: ProfileToken?) {
        updateTrackedIdentity(profile)
    }

    private fun updateTrackedIdentity(profile: ProfileToken?) {
        runBlocking {
            if (profileJob?.isActive == true) {
                profileJob?.cancel()
            }
            profileJob = launch {
                if (profile == null) {
                    anonMode = true
                    org = randomOrg
                } else {
                    try {
                        org = CredentialsClient.operationsApi(profile.token).getCurrentOrganization().body()!!.id
                    } catch (e: Exception) {
                        //TODO: Log
                        //TODO: Retry?
                        org = ""
                    }
                }
                telClient.enqueue(
                    IdentifyMessage.builder()
                        .userId(org)
                        .traits(
                            mapOf(
                                "idea ver" to ApplicationInfo.getInstance().strictVersion,
                                "anonymous" to anonMode.toString(),
                            )
                        )
                )
                telClient.flush()
            }
        }
    }


    fun trackAction(actionName: String, actionMetaData: Map<String, String>) {
        telClient.enqueue(
            TrackMessage.builder(actionName)
                .userId(org)
                .properties(actionMetaData)
        )
    }

    fun trackAction(actionName: String) {
        telClient.enqueue(
            TrackMessage.builder(actionName)
                .userId(org)
        )
    }

    fun trackDevOpsCrud(resourceType: String, resourceName: String, operation: CrudEnum, completed: Boolean) {
        telClient.enqueue(
            TrackMessage.builder("DevOps CRUD")
                .userId(org)
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
    fun trackDevOpsCrud(
        resourceType: String,
        resourceName: String,
        operation: CrudEnum,
        completed: Boolean,
        extraData: Map<String, String>
    ) {
        telClient.enqueue(
            TrackMessage.builder("DevOps CRUD")
                .userId(org)
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

    fun trackStargateCrud(resourceType: String, resourceName: String, operation: CrudEnum, completed: Boolean) {
        telClient.enqueue(
            TrackMessage.builder("Stargate CRUD")
                .userId(org)
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
        telClient.enqueue(
            TrackMessage.builder("Stargate CRUD")
                .userId(org)
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
        telClient.enqueue(
            TrackMessage.builder("Click Action")
                .userId(org)
                .properties(
                    mapOf(
                        "target" to actionTarget.toString(),
                        "type" to actionBehavior,
                    )
                )
        )
    }

    fun trackClick(actionTarget: ClickTarget, actionBehavior: String, extraData: Map<String, String>) {
        telClient.enqueue(
            TrackMessage.builder("Click Action")
                .userId(org)
                .properties(
                    mapOf(
                        "target" to actionTarget.toString(),
                        "type" to actionBehavior,
                    ) + extraData
                )
        )
    }
}
enum class CrudEnum {
    CREATE, READ, UPDATE, DELETE
}
enum class ClickTarget {
    BUTTON, LINK
}
