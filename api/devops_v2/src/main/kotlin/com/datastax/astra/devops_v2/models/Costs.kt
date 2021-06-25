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
 * @param costPerMinCents
 * @param costPerHourCents
 * @param costPerDayCents
 * @param costPerMonthCents
 * @param costPerMinParkedCents
 * @param costPerHourParkedCents
 * @param costPerDayParkedCents
 * @param costPerMonthParkedCents
 */

data class Costs(
    @SerializedName("costPerMinCents")
    val costPerMinCents: kotlin.Double? = null,
    @SerializedName("costPerHourCents")
    val costPerHourCents: kotlin.Double? = null,
    @SerializedName("costPerDayCents")
    val costPerDayCents: kotlin.Double? = null,
    @SerializedName("costPerMonthCents")
    val costPerMonthCents: kotlin.Double? = null,
    @SerializedName("costPerMinParkedCents")
    val costPerMinParkedCents: kotlin.Double? = null,
    @SerializedName("costPerHourParkedCents")
    val costPerHourParkedCents: kotlin.Double? = null,
    @SerializedName("costPerDayParkedCents")
    val costPerDayParkedCents: kotlin.Double? = null,
    @SerializedName("costPerMonthParkedCents")
    val costPerMonthParkedCents: kotlin.Double? = null
)
