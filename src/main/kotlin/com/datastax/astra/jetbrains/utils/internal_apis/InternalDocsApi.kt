package com.datastax.astra.jetbrains.utils.internal_apis

import com.datastax.astra.jetbrains.utils.internal_apis.models.GenerateTokenResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface InternalDocsApi {
    /**
     * Gets a JSON schema from a collection
     * Responses:
     *  - 200: Successful operation
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 404: Not found
     *  - 500: Internal Server Error
     *
     * @param dstaxCookie Key for making datastax GQL call
     * @param graphQLCommand
     * @return [GenerateTokenResponse]
     */
    @Headers("Content-Type: application/json")
    @POST("api/v2/graphql")
    suspend fun getDatabaseAdminToken(@Header("Cookie") dstaxCookie: String, @Body graphQLCommand: RequestBody): Response<GenerateTokenResponse>
}
