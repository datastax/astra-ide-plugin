package com.datastax.astra.jetbrains.utils.internal_apis

import com.datastax.astra.jetbrains.utils.internal_apis.models.GenerateTokenResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface InternalDevOpsApi {
    /**
     * Returns a bearer token for the given
     * Returns all supported tier, cloud, region, count, and capacitity combinations.
     * Responses:
     *  - 200: successful operation
     *
     * @param dstaxCookie Key for making datastax GQL call
     * @param graphQLCommand
     * @return [GenerateTokenResponse]
     */
    @Headers("Content-Type: application/json")
    @POST("api/v2/graphql")
    suspend fun getDatabaseAdminToken(@Header("Cookie") dstaxCookie: String, @Body graphQLCommand: RequestBody): Response<GenerateTokenResponse>
}
