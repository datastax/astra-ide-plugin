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

import org.openapi.example.model.Costs

import com.squareup.moshi.Json

/**
 * RegionCombination defines a Tier, cloud provider, region combination
 * @param tier 
 * @param cloudProvider 
 * @param region 
 * @param cost 
 */

data class RegionCombination (
    @Json(name = "tier")
    val tier: kotlin.String,
    @Json(name = "cloudProvider")
    val cloudProvider: kotlin.String,
    @Json(name = "region")
    val region: kotlin.String,
    @Json(name = "cost")
    val cost: Costs
)
