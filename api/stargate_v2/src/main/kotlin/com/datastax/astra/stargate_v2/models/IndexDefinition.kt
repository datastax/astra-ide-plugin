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
 * @param column Column for which index will be created.
 * @param name Optional name for the index, which must be unique. If no name is specified, the index is named as follows: tablename_columnname_idx.
 * @param type Type of index, defined with a custom index class name or classpath. Secondary index is default, no type entered
 * @param options
 * @param kind Index kind for collections.
 * @param ifNotExists Determines creation of a new index, if an index with the same name exists. If an index exists, and this option is set to true, an error is returned.
 */

data class IndexDefinition(
    /* Column for which index will be created. */
    @SerializedName("column")
    val column: kotlin.String,
    /* Optional name for the index, which must be unique. If no name is specified, the index is named as follows: tablename_columnname_idx. */
    @SerializedName("name")
    val name: kotlin.String? = null,
    /* Type of index, defined with a custom index class name or classpath. Secondary index is default, no type entered */
    @SerializedName("type")
    val type: IndexDefinition.Type? = null,
    @SerializedName("options")
    val options: kotlin.collections.Map<kotlin.String, kotlin.String>? = null,
    /* Index kind for collections. */
    @SerializedName("kind")
    val kind: IndexDefinition.Kind? = null,
    /* Determines creation of a new index, if an index with the same name exists. If an index exists, and this option is set to true, an error is returned. */
    @SerializedName("ifNotExists")
    val ifNotExists: kotlin.Boolean? = null
) {

    /**
     * Type of index, defined with a custom index class name or classpath. Secondary index is default, no type entered
     * Values: ORG_PERIOD_APACHE_PERIOD_CASSANDRA_PERIOD_INDEX_PERIOD_SASI_PERIOD_SASI_INDEX,STORAGE_ATTACHED_INDEX
     */
    enum class Type(val value: kotlin.String) {
        @SerializedName(value = "org.apache.cassandra.index.sasi.SASIIndex") ORG_PERIOD_APACHE_PERIOD_CASSANDRA_PERIOD_INDEX_PERIOD_SASI_PERIOD_SASI_INDEX("org.apache.cassandra.index.sasi.SASIIndex"),
        @SerializedName(value = "StorageAttachedIndex") STORAGE_ATTACHED_INDEX("StorageAttachedIndex");
    }
    /**
     * Index kind for collections.
     * Values: FULL,KEYS,VALUES,ENTRIES
     */
    enum class Kind(val value: kotlin.String) {
        @SerializedName(value = "FULL") FULL("FULL"),
        @SerializedName(value = "KEYS") KEYS("KEYS"),
        @SerializedName(value = "VALUES") VALUES("VALUES"),
        @SerializedName(value = "ENTRIES") ENTRIES("ENTRIES");
    }
}
