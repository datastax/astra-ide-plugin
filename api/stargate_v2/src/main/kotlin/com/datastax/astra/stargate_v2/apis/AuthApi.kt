package com.datastax.astra.stargate_v2.apis

import com.datastax.astra.stargate_v2.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody

import com.datastax.astra.stargate_v2.models.AuthTokenResponse
import com.datastax.astra.stargate_v2.models.Credentials
import com.datastax.astra.stargate_v2.models.Error

interface AuthApi {
    /**
     * Create an authorization token
     * To create an authorization token, you&#39;ll need the {databaseid} and {region} for your server, in addition to the path parameters. This applies only if you are using a classic database that was created before 4 March 2021 that has not migrated to the newest authentication.
     * Responses:
     *  - 201: Created
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 500: Internal server error
     * 
     * @param credentials  
     * @return [AuthTokenResponse]
     */
    @POST("api/rest/v1/auth")
    suspend fun createToken(@Body credentials: Credentials): Response<AuthTokenResponse>

}
