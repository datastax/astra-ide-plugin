/**
* Stargate Document API reference
* The Stargate Document API provides CRUD operations on document data managed by Stargate.
*
* The version of the OpenAPI document: 2.0.0
*
*
* NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
* https://openapi-generator.tech
* Do not edit the class manually.
*/
package com.datastax.astra.stargate_document_v2.models

import com.google.gson.annotations.SerializedName

/**
 *
 * @param name The name of the keyspace.
 * @param datacenters The datacenters within a keyspace. Only applies for those keyspaces created with NetworkTopologyStrategy.
 */

data class Keyspace(
    /* The name of the keyspace. */
    @SerializedName("name")
    val name: kotlin.String,
    /* The datacenters within a keyspace. Only applies for those keyspaces created with NetworkTopologyStrategy. */
    @SerializedName("datacenters")
    val datacenters: kotlin.collections.List<Datacenter>? = null
)
