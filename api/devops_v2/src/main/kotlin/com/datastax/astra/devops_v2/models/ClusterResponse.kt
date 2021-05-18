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
 * ClusterResponse is the response struct for a cluster.
 * @param brokerServiceUrl 
 * @param cloudProvider 
 * @param cloudRegion 
 * @param clusterName 
 * @param clusterType 
 * @param webServiceUrl 
 * @param websocketUrl 
 */

data class ClusterResponse (
    @SerializedName("brokerServiceUrl")
    val brokerServiceUrl: kotlin.String? = null,
    @SerializedName("cloudProvider")
    val cloudProvider: kotlin.String? = null,
    @SerializedName("cloudRegion")
    val cloudRegion: kotlin.String? = null,
    @SerializedName("clusterName")
    val clusterName: kotlin.String? = null,
    @SerializedName("clusterType")
    val clusterType: kotlin.String? = null,
    @SerializedName("webServiceUrl")
    val webServiceUrl: kotlin.String? = null,
    @SerializedName("websocketUrl")
    val websocketUrl: kotlin.String? = null
)

