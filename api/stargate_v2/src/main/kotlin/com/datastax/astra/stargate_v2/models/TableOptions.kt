/**
* Stargate REST API reference
* The Stargate REST API provides CRUD operations on table data managed by Stargate. Keep in mind that you will need information for the server ({databaseId} and {region}) for all of these operations.
*
* The version of the OpenAPI document: 2.0.0
*
*
* NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
* https://openapi-generator.tech
* Do not edit the class manually.
*/
package com.datastax.astra.stargate_v2.models

import com.google.gson.annotations.SerializedName

/**
 *
 * @param defaultTimeToLive Defines the Time To Live (TTL), which determines the time period (in seconds) to expire data. If the value is >0, TTL is enabled for the entire table and an expiration timestamp is added to each column. The maximum value is 630720000 (20 years). A new TTL timestamp is calculated each time the data is updated and the row is removed after the data expires.
 * @param clusteringExpression
 */

data class TableOptions(
    /* Defines the Time To Live (TTL), which determines the time period (in seconds) to expire data. If the value is >0, TTL is enabled for the entire table and an expiration timestamp is added to each column. The maximum value is 630720000 (20 years). A new TTL timestamp is calculated each time the data is updated and the row is removed after the data expires. */
    @SerializedName("defaultTimeToLive")
    val defaultTimeToLive: kotlin.Int? = null,
    @SerializedName("clusteringExpression")
    val clusteringExpression: kotlin.collections.List<ClusteringExpression>? = null
)
