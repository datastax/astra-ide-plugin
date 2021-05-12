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


import com.squareup.moshi.Json

/**
 * Defines a column list for the primary key. Can be either a single column, compound primary key, or composite partition key. Provide multiple columns for the partition key to define a composite partition key.
 * @param partitionKey Name of the column or columns that constitute the partition key.
 * @param clusteringKey Name of the column or columns that constitute the clustering key.
 */

data class PrimaryKey (
    /* Name of the column or columns that constitute the partition key. */
    @Json(name = "partitionKey")
    val partitionKey: kotlin.collections.List<kotlin.String>,
    /* Name of the column or columns that constitute the clustering key. */
    @Json(name = "clusteringKey")
    val clusteringKey: kotlin.collections.List<kotlin.String>? = null
)
