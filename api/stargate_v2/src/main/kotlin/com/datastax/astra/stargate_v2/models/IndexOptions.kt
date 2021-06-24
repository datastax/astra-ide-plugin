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
 * @param key
 * @param value
 */

data class IndexOptions(
    @SerializedName("key")
    val key: kotlin.String? = null,
    @SerializedName("value")
    val value: kotlin.String? = null
)
