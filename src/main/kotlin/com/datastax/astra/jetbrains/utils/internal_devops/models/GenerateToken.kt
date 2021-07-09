package com.datastax.astra.jetbrains.utils.internal_devops.models

import com.google.gson.annotations.SerializedName

/**
 * The inter container holding the token.
 * @param token The bear token for authenticating RESTful calls
 */

data class GenerateToken(
    /* The Astra bearer token, AstraCS. */
    @SerializedName("token")
    val token: String,
)
