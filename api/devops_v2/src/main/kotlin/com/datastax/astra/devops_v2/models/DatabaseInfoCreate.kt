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
 * DatabaseInfo is the user-provided information describing a database.
 * @param name Name of the database--user friendly identifier.
 * @param keyspace Keyspace name in database.
 * @param cloudProvider This is the cloud provider where the database lives.
 * @param tier With the exception of classic databases, all databases are serverless. Classic databases can no longer be created with the DevOps API.
 * @param capacityUnits Capacity units were used for classic databases, but are not used for serverless databases. Enter 1 CU for serverless databases. Classic databases can no longer be created with the DevOps API.
 * @param region Region refers to the cloud region.
 */

data class DatabaseInfoCreate(
    /* Name of the database--user friendly identifier. */
    @SerializedName("name")
    val name: kotlin.String,
    /* Keyspace name in database. */
    @SerializedName("keyspace")
    val keyspace: kotlin.String,
    /* This is the cloud provider where the database lives. */
    @SerializedName("cloudProvider")
    val cloudProvider: DatabaseInfoCreate.CloudProvider,
    /* With the exception of classic databases, all databases are serverless. Classic databases can no longer be created with the DevOps API. */
    @SerializedName("tier")
    val tier: DatabaseInfoCreate.Tier,
    /* Capacity units were used for classic databases, but are not used for serverless databases. Enter 1 CU for serverless databases. Classic databases can no longer be created with the DevOps API. */
    @SerializedName("capacityUnits")
    val capacityUnits: kotlin.Int,
    /* Region refers to the cloud region. */
    @SerializedName("region")
    val region: kotlin.String
) {

    /**
     * This is the cloud provider where the database lives.
     * Values: AWS,GCP
     */
    enum class CloudProvider(val value: kotlin.String) {
        @SerializedName(value = "AWS")
        AWS("AWS"),

        @SerializedName(value = "GCP")
        GCP("GCP"),

        @SerializedName(value = "AZURE")
        AZURE("AZURE");
    }

    /**
     * With the exception of classic databases, all databases are serverless. Classic databases can no longer be created with the DevOps API.
     * Values: SERVERLESS
     */
    enum class Tier(val value: kotlin.String) {
        @SerializedName(value = "serverless")
        SERVERLESS("serverless");
    }
}
