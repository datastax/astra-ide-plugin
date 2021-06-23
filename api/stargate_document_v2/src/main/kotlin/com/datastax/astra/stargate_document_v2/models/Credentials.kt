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
 * User credentials for authenticating
 * @param username Username, must be ASTRA_DB_USERNAME
 * @param password Password, must be ASTRA_DB_PASSWORD
 */

data class Credentials (
    /* Username, must be ASTRA_DB_USERNAME */
    @SerializedName("username")
    val username: kotlin.String,
    /* Password, must be ASTRA_DB_PASSWORD */
    @SerializedName("password")
    val password: kotlin.String
)

