package com.datastax.astra.jetbrains.telemetry

import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.credentials.CredentialsClient
import com.datastax.astra.jetbrains.credentials.ProfileManager
import com.intellij.openapi.application.ApplicationInfo
import com.segment.analytics.Analytics
import com.segment.analytics.messages.GroupMessage
import com.segment.analytics.messages.IdentifyMessage
import com.segment.analytics.messages.TrackMessage
import com.uchuhimo.konf.ConfigSpec
import kotlinx.coroutines.runBlocking
import java.util.UUID.randomUUID

//TODO: See testTelemetry for example of the functions
//TODO: Rewrite this as a service and not a static class
// Possibly expand this into more than one class at that time
object TelemetryManager {
    //TODO: Figure out safest way to store this

    var telClient = Analytics.builder(this::class.java.getResource("/telConfig").readText()).build()

    //Might not need to store a local version if written as a service
    var knownToken = ""
    var knownOrg = ""
    var anonMode = false

    //Make sure the "user" hasn't changed
    //TODO: Make this an active process that occurs as part of the credential change process
    fun checkCreds(){
        val activeToken = ProfileManager.getInstance(AstraClient.project).activeProfile?.token.toString()
        //If there were no tokens to use don't change the profile change since we can't without an ID
        //This should be cleaner once moved into a service
        if(activeToken != knownToken && activeToken != "null"){
            knownToken = activeToken
            runBlocking {
                knownOrg = CredentialsClient.operationsApi(activeToken).getCurrentOrganization().body()!!.id
            }
            //TODO: Re-enable this with userID change
            /*if(activeOrg != knownOrg){
                knownOrg = activeOrg
                trackOrgChange()
            }*/
            anonMode = false
            trackProfileChange()
        }
        //Only track a user if there's not currently a userID
        else if(!anonMode && knownOrg == ""){
            knownOrg = randomUUID().toString()
            anonMode = true
            trackProfileChange()
        }
    }

    //TODO: Build this for changing tokens
    fun trackProfileChange(){
        telClient.enqueue(
            IdentifyMessage.builder()
                //TODO: Figure out what all to track
                .userId(knownOrg)
                .traits(mapOf(
                    //TODO: Figure out what all to track
                    "idea ver" to ApplicationInfo.getInstance().getStrictVersion(),
                    "anonymous" to anonMode.toString(),
                    )
                )
        )
    }

    //TODO: Add to RefreshExplorerAction
    //TODO: Add to UserRegisterAction
    fun trackAction(actionName: String, actionMetaData: Map<String,String>){
        checkCreds()
        telClient.enqueue(
            TrackMessage.builder(actionName)
                .userId(knownOrg)
                .properties(actionMetaData)
        )
    }

    //TODO:Track orgID through groupID once a way to track users is identified
    // Possibly associate users through aliases
    /*fun trackOrgChange(){
        telClient.enqueue(
            GroupMessage.builder("some-group-id")
                .userId(activeOrg)
                .traits(mapOf(
                    //TODO: Figure out what all to track
                    "name" to "Org Name",
                    "size" to "Number of users")
                )
        )
    }*/

}
