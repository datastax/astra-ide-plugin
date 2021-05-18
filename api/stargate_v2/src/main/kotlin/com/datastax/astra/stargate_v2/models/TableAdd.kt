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

import com.datastax.astra.stargate_v2.models.ColumnDefinition
import com.datastax.astra.stargate_v2.models.PrimaryKey
import com.datastax.astra.stargate_v2.models.TableOptions

import com.google.gson.annotations.SerializedName

/**
 * 
 * @param name 
 * @param columnDefinitions 
 * @param primaryKey 
 * @param ifNotExists Determines whether to create a new table if a table with the same name exists. Attempting to create an existing table returns an error unless this option is true.
 * @param tableOptions 
 */

data class TableAdd (
    @SerializedName("name")
    val name: kotlin.String,
    @SerializedName("columnDefinitions")
    val columnDefinitions: kotlin.collections.List<ColumnDefinition>,
    @SerializedName("primaryKey")
    val primaryKey: PrimaryKey,
    /* Determines whether to create a new table if a table with the same name exists. Attempting to create an existing table returns an error unless this option is true. */
    @SerializedName("ifNotExists")
    val ifNotExists: kotlin.Boolean? = null,
    @SerializedName("tableOptions")
    val tableOptions: TableOptions? = null
)

