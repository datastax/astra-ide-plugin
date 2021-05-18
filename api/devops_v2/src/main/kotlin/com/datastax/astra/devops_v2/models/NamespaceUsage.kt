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
 * A namespace and its usage
 * @param namespace 
 * @param topics 
 */

data class NamespaceUsage (
    @SerializedName("namespace")
    val namespace: kotlin.String? = null,
    @SerializedName("topics")
    val topics: kotlin.collections.List<kotlin.String>? = null
)

