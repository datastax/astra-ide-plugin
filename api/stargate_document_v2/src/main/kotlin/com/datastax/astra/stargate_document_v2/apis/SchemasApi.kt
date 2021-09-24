package com.datastax.astra.stargate_document_v2.apis

import com.datastax.astra.stargate_document_v2.infrastructure.CollectionFormats.*
import com.datastax.astra.stargate_document_v2.models.InlineResponse2001
import com.datastax.astra.stargate_document_v2.models.Keyspace
import retrofit2.Response
import retrofit2.http.*

interface SchemasApi {
    /**
     * Get all namespaces
     * Retrieve all available namespaces.
     * Responses:
     *  - 200:
     *  - 401: Unauthorized
     *  - 500: Internal server error
     *
     * @param xCassandraRequestId Unique identifier (UUID) for the request. Use any valid UUID.
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param pretty format results (optional)
     * @param raw unwrap results (optional)
     * @return [InlineResponse2001]
     */
    @GET("api/rest/v2/schemas/namespaces")
    suspend fun getAllNamespaces(@Header("X-Cassandra-Request-Id") xCassandraRequestId: java.util.UUID, @Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Query("pretty") pretty: kotlin.Boolean? = null, @Query("raw") raw: kotlin.Boolean? = null): Response<InlineResponse2001>

    /**
     * Get a namespace
     *
     * Responses:
     *  - 200:
     *  - 400: Invalid input
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 500: Internal server error
     *
     * @param xCassandraRequestId Unique identifier (UUID) for the request. Use any valid UUID.
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param namespaceId namespace name
     * @param pretty format results (optional)
     * @param raw unwrap results (optional)
     * @return [Keyspace]
     */
    @GET("api/rest/v2/schemas/namespaces/{namespace-id}")
    suspend fun getNamespace(@Header("X-Cassandra-Request-Id") xCassandraRequestId: java.util.UUID, @Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("namespace-id") namespaceId: kotlin.String, @Query("pretty") pretty: kotlin.Boolean? = null, @Query("raw") raw: kotlin.Boolean? = null): Response<Keyspace>



}
