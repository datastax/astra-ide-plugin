/**
* Astra DevOps API
* Use this REST API to perform lifecycle actions for DataStax Astra databases.</br> </br> To get started, get your application token from your Astra database. You can then create, terminate, resize, park, and unpark databases using the DevOps API. You cannot park, unpark, or resize serverless databases.  # Authentication  <!-- ReDoc-Inject: <security-definitions> -->
*
* The version of the OpenAPI document: 2.0.0
* Contact: ad-astra@datastax.com
*
* NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
* https://openapi-generator.tech
* Do not edit the class manually.
*/
package org.openapi.example.model

import org.openapi.example.model.MigrationProxyMapping

import com.squareup.moshi.Json

/**
 * Configuration of the migration proxy and mappings of astra node to a customer node currently in use
 * @param originUsername origin cassandra username
 * @param originPassword origin cassandra password
 * @param mappings 
 */

data class MigrationProxyConfiguration (
    /* origin cassandra username */
    @Json(name = "originUsername")
    val originUsername: kotlin.String,
    /* origin cassandra password */
    @Json(name = "originPassword")
    val originPassword: kotlin.String,
    @Json(name = "mappings")
    val mappings: kotlin.collections.List<MigrationProxyMapping>
)

