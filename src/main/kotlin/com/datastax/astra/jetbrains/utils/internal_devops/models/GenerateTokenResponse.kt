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
