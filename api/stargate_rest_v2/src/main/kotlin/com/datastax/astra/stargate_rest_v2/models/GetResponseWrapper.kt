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
package com.datastax.astra.stargate_rest_v2.models


import com.google.gson.annotations.SerializedName

/**
 * 
 * @param count The count of records returned
 * @param pageState A string representing the paging state to be used on future paging requests.
 * @param data The data returned by the request.
 */

data class GetResponseWrapper (
    /* The count of records returned */
    @SerializedName("count")
    val count: kotlin.Int? = null,
    /* A string representing the paging state to be used on future paging requests. */
    @SerializedName("pageState")
    val pageState: kotlin.String? = null,
    /* The data returned by the request. */
    @SerializedName("data")
    val data: kotlin.Any? = null
)

