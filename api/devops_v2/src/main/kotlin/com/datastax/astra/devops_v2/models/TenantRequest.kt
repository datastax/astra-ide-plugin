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
 *
 * @param cloudProvider
 * @param cloudRegion
 * @param plan
 * @param tenantName
 * @param userEmail
 */

data class TenantRequest(
    @SerializedName("cloudProvider")
    val cloudProvider: kotlin.String? = null,
    @SerializedName("cloudRegion")
    val cloudRegion: kotlin.String? = null,
    @SerializedName("plan")
    val plan: kotlin.String? = null,
    @SerializedName("tenantName")
    val tenantName: kotlin.String? = null,
    @SerializedName("userEmail")
    val userEmail: kotlin.String? = null
)
