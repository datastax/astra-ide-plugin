package com.datastax.astra.jetbrains.utils.internal_devops.models

import com.google.gson.annotations.SerializedName

/**
 * The outer container holding the container with a token inside.
 * @param token The bear token for authenticating RESTful calls
 */

data class Data(
    /* The Astra bearer token, AstraCS. */
    @SerializedName("generateToken")
    val generateToken: GenerateToken
)
