/**
* Astra DevOps API
* Use these REST APIs to perform lifecycle actions for DataStax Astra databases and DataStax Astra Streaming Pulsar instances.</br>  # Authentication  <!-- ReDoc-Inject: <security-definitions> -->
*
* The version of the OpenAPI document: 2.2.0
* Contact: ad-astra@datastax.com
*
* NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
* https://openapi-generator.tech
* Do not edit the class manually.
*/
package com.datastax.astra.devops_v2.models

import com.google.gson.annotations.SerializedName

/**
 * Database contains the key information about a database.
 * @param id
 * @param orgId
 * @param ownerId
 * @param info
 * @param status
 * @param creationTime CreationTime in ISO RFC3339 format
 * @param terminationTime TerminationTime in ISO RFC3339 format
 * @param storage
 * @param availableActions
 * @param message Message to the customer about the cluster.
 * @param studioUrl
 * @param grafanaUrl
 * @param cqlshUrl
 * @param graphqlUrl
 * @param dataEndpointUrl
 */

data class Database(
    @SerializedName("id")
    val id: kotlin.String,
    @SerializedName("orgId")
    val orgId: kotlin.String,
    @SerializedName("ownerId")
    val ownerId: kotlin.String,
    @SerializedName("info")
    val info: DatabaseInfo,
    @SerializedName("status")
    val status: StatusEnum,
    /* CreationTime in ISO RFC3339 format */
    @SerializedName("creationTime")
    val creationTime: kotlin.String? = null,
    /* TerminationTime in ISO RFC3339 format */
    @SerializedName("terminationTime")
    val terminationTime: kotlin.String? = null,
    @SerializedName("storage")
    val storage: Storage? = null,
    @SerializedName("availableActions")
    val availableActions: kotlin.collections.List<Database.AvailableActions>? = null,
    /* Message to the customer about the cluster. */
    @SerializedName("message")
    val message: kotlin.String? = null,
    @SerializedName("studioUrl")
    val studioUrl: kotlin.String? = null,
    @SerializedName("grafanaUrl")
    val grafanaUrl: kotlin.String? = null,
    @SerializedName("cqlshUrl")
    val cqlshUrl: kotlin.String? = null,
    @SerializedName("graphqlUrl")
    val graphqlUrl: kotlin.String? = null,
    @SerializedName("dataEndpointUrl")
    val dataEndpointUrl: kotlin.String? = null
) {

    /**
     *
     * Values: PARK,UNPARK,RESIZE,RESET_PASSWORD,ADD_KEYSPACE,ADD_DATACENTERS,TERMINATE_DATACENTER,GET_CREDS,TERMINATE,REMOVE_KEYSPACE,ADD_TABLE,REMOVE_MIGRATION_PROXY,LAUNCH_MIGRATION_PROXY
     */
    enum class AvailableActions(val value: kotlin.String) {
        @SerializedName(value = "park")
        PARK("park"),

        @SerializedName(value = "unpark")
        UNPARK("unpark"),

        @SerializedName(value = "resize")
        RESIZE("resize"),

        @SerializedName(value = "resetPassword")
        RESET_PASSWORD("resetPassword"),

        @SerializedName(value = "addKeyspace")
        ADD_KEYSPACE("addKeyspace"),

        @SerializedName(value = "addDatacenters")
        ADD_DATACENTERS("addDatacenters"),

        @SerializedName(value = "terminateDatacenter")
        TERMINATE_DATACENTER("terminateDatacenter"),

        @SerializedName(value = "getCreds")
        GET_CREDS("getCreds"),

        @SerializedName(value = "terminate")
        TERMINATE("terminate"),

        @SerializedName(value = "removeKeyspace")
        REMOVE_KEYSPACE("removeKeyspace"),

        @SerializedName(value = "addTable")
        ADD_TABLE("addTable"),

        @SerializedName(value = "removeMigrationProxy")
        REMOVE_MIGRATION_PROXY("removeMigrationProxy"),

        @SerializedName(value = "launchMigrationProxy")
        LAUNCH_MIGRATION_PROXY("launchMigrationProxy");
    }
}
