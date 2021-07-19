package com.datastax.astra.jetbrains.utils.internal_devops.models

import com.google.gson.annotations.SerializedName
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

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

data class GenerateTokenRequest(
    val orgId: String,
    val graphqlBlob: RequestBody = """
        {"operationName":"generateToken","variables":{"input":{"orgId":"$orgId","roles":["1faa93f2-b889-4190-9585-4bc6e3c3595a"]}},"query":"mutation generateToken(${'$'}input: GenerateTokenInput\u0021) {generateToken(input: ${'$'}input){token}}"}
        """.trimIndent().toRequestBody("text/plain".toMediaTypeOrNull())
)