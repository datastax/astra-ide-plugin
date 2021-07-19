package com.datastax.astra.jetbrains.utils.internal_devops.models

import com.google.gson.annotations.SerializedName

/**
 * The response for a requested token.
 * @param data The main container object that holds a container holding the token
 */
data class GenerateTokenResponse(
    /* The ID of the client (UUID). */
    @SerializedName("data")
    val data: Data,
)

/**
 * The outer container holding the container with a token inside.
 * @param token The bear token for authenticating RESTful calls
 */
data class Data(
    /* The Astra bearer token, AstraCS. */
    @SerializedName("generateToken")
    val generateToken: GenerateToken
)

/**
 * The inter container holding the token.
 * @param token The bear token for authenticating RESTful calls
 */
data class GenerateToken(
    /* The Astra bearer token, AstraCS. */
    @SerializedName("token")
    val token: String,
)
