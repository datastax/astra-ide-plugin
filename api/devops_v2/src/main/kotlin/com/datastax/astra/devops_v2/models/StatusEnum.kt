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
*
* Values: ACTIVE,PENDING,PREPARING,PREPARED,INITIALIZING,PARKED,PARKING,UNPARKING,TERMINATED,TERMINATING,RESIZING,ERROR,MAINTENANCE,UNKNOWN
*/

enum class StatusEnum(val value: kotlin.String) {

    @SerializedName(value = "ACTIVE")
    ACTIVE("ACTIVE"),

    @SerializedName(value = "PENDING")
    PENDING("PENDING"),

    @SerializedName(value = "PREPARING")
    PREPARING("PREPARING"),

    @SerializedName(value = "PREPARED")
    PREPARED("PREPARED"),

    @SerializedName(value = "INITIALIZING")
    INITIALIZING("INITIALIZING"),

    @SerializedName(value = "PARKED")
    PARKED("PARKED"),

    @SerializedName(value = "PARKING")
    PARKING("PARKING"),

    @SerializedName(value = "UNPARKING")
    UNPARKING("UNPARKING"),

    @SerializedName(value = "TERMINATED")
    TERMINATED("TERMINATED"),

    @SerializedName(value = "TERMINATING")
    TERMINATING("TERMINATING"),

    @SerializedName(value = "RESIZING")
    RESIZING("RESIZING"),

    @SerializedName(value = "ERROR")
    ERROR("ERROR"),

    @SerializedName(value = "MAINTENANCE")
    MAINTENANCE("MAINTENANCE"),

    @SerializedName(value = "HIBERNATING")
    HIBERNATING("HIBERNATING"),

    @SerializedName(value = "HIBERNATED")
    HIBERNATED("HIBERNATED"),

    @SerializedName(value = "RESUMING")
    RESUMING("RESUMING"),

    @SerializedName(value = "UNKNOWN")
    UNKNOWN("UNKNOWN");

    /**
     This override toString avoids using the enum var name and uses the actual api value instead.
     In cases the var name and value are different, the client would send incorrect enums to the server.
     **/
    override fun toString(): String {
        return value
    }
}
