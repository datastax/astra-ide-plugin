package com.datastax.astra.jetbrains.telemetry

import com.segment.analytics.Analytics
import com.segment.analytics.messages.GroupMessage
import com.segment.analytics.messages.IdentifyMessage
import com.segment.analytics.messages.TrackMessage

class TelemetryManager {
    var telClient = Analytics.builder("DVku2hfmrgixOIBzcDWwadEqtOMVVxkU").build()
    //TODO: Implement something to keep this userID current for use in other calls
    // Handle situation where user has no tokens since they haven't registered
    var userIDHash = 0
    //TODO: See testTelemetry for example of the functions

    //TODO:Track orgID as groupID
    //TODO:Identify user (use token and hash with other data?)

    //TODO: Build this for changing tokens
    //Possibly associate new token hash with the old via alias
    fun trackProfileChange(){
        //TODO: Either implement here or implement and call the hash function here
        telClient.enqueue(
            IdentifyMessage.builder()
                //TODO: Figure out what all to track
                .userId("f4ca124298")
                .traits(mapOf(
                    //TODO: Figure out what all to track
                    "name" to "Michael Bolton",
                    "email" to "mbolton@example.com")
                )
        );
        //TODO:
        // IF new ORG ID is different from previous call trackOrgChange
    }

    fun trackOrgChange(){
        telClient.enqueue(
            GroupMessage.builder("some-group-id")
                .userId("$userIDHash")
                .traits(mapOf(
                    //TODO: Figure out what all to track
                    "name" to "Org Name",
                    "size" to "Number of users")
                )
        )
    }


    fun trackCreateDB(){
        telClient.enqueue(
            TrackMessage.builder("Create Database")
                .userId("$userIDHash")
                .properties(mapOf(
                    "Status" to "Information about plugin when refresh clicked"
                    )
                )
        )
    }


    fun trackDeleteDB(){
        telClient.enqueue(
            TrackMessage.builder("Delete Database")
                .userId("$userIDHash")
                .properties(mapOf(
                    "Status" to "Information about plugin when refresh clicked"
                    )
                )
        )
    }


    fun trackCreateKS(){
        telClient.enqueue(
            TrackMessage.builder("Create Keyspace")
                .userId("$userIDHash")
                .properties(mapOf(
                    "Status" to "Information about plugin when refresh clicked"
                    )
                )
        )
    }

    //TODO: Add to RefreshExplorerAction
    fun trackManualRefresh(){
        telClient.enqueue(
            TrackMessage.builder("Plugin Refreshed")
                .userId("$userIDHash")
                .properties(mapOf(
                    "Status" to "Information about plugin when refresh clicked"
                    )
                )
        )
    }

    //TODO: Add to UserRegisterAction
    fun trackRegister(){
        telClient.enqueue(
            TrackMessage.builder("Register Link")
            .userId("Anonymous user?")
            .properties(mapOf(
                "Meta" to "Information about plugin when user clicked Register"
                )
            )
        )
    }

}