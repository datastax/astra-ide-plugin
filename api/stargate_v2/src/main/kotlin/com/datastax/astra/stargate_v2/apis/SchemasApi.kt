package com.datastax.astra.stargate_v2.apis

import com.datastax.astra.stargate_v2.infrastructure.CollectionFormats.*
import com.datastax.astra.stargate_v2.models.ColumnDefinition
import com.datastax.astra.stargate_v2.models.IndexDefinition
import com.datastax.astra.stargate_v2.models.InlineResponse200
import com.datastax.astra.stargate_v2.models.InlineResponse2001
import com.datastax.astra.stargate_v2.models.InlineResponse2002
import com.datastax.astra.stargate_v2.models.InlineResponse201
import com.datastax.astra.stargate_v2.models.Keyspace
import com.datastax.astra.stargate_v2.models.Table
import com.datastax.astra.stargate_v2.models.TableAdd
import retrofit2.Response
import retrofit2.http.*

interface SchemasApi {
    /**
     * Create a column
     *
     * Responses:
     *  - 201: resource created
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 409: Conflict
     *  - 500: Internal server error
     *
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param keyspaceId keyspace name
     * @param tableId table name
     * @param columnDefinition
     * @return [InlineResponse201]
     */
    @POST("api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns")
    suspend fun createColumn(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Path("table-id") tableId: kotlin.String, @Body columnDefinition: ColumnDefinition): Response<InlineResponse201>

    /**
     * Create an index
     *
     * Responses:
     *  - 201: resource created
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 409: Conflict
     *  - 500: Internal server error
     *
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param keyspaceId keyspace name
     * @param tableId table name
     * @param indexDefinition
     * @return [kotlin.String]
     */
    @POST("api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/indexes")
    suspend fun createIndex(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Path("table-id") tableId: kotlin.String, @Body indexDefinition: IndexDefinition): Response<kotlin.String>

    /**
     * Create a table
     *
     * Responses:
     *  - 201: resource created
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 409: Conflict
     *  - 500: Internal server error
     *
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param keyspaceId keyspace name
     * @param tableAdd
     * @return [InlineResponse201]
     */
    @POST("api/rest/v2/schemas/keyspaces/{keyspace-id}/tables")
    suspend fun createTable(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Body tableAdd: TableAdd): Response<InlineResponse201>

    /**
     * Delete a column
     *
     * Responses:
     *  - 204: No Content
     *  - 401: Unauthorized
     *  - 500: Internal server error
     *
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param keyspaceId keyspace name
     * @param tableId table name
     * @param columnId column name
     * @return [Unit]
     */
    @DELETE("api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns/{column-id}")
    suspend fun deleteColumn(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Path("table-id") tableId: kotlin.String, @Path("column-id") columnId: kotlin.String): Response<Unit>

    /**
     * Delete an index
     *
     * Responses:
     *  - 204: No Content
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 500: Internal server error
     *
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param keyspaceId keyspace name
     * @param tableId table name
     * @param indexId index name
     * @return [Unit]
     */
    @DELETE("api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/indexes/{index-id}")
    suspend fun deleteIndex(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Path("table-id") tableId: kotlin.String, @Path("index-id") indexId: kotlin.String): Response<Unit>

    /**
     * Delete a table
     *
     * Responses:
     *  - 204: No Content
     *  - 401: Unauthorized
     *  - 500: Internal server error
     *
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param keyspaceId keyspace name
     * @param tableId table name
     * @return [Unit]
     */
    @DELETE("api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}")
    suspend fun deleteTable(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Path("table-id") tableId: kotlin.String): Response<Unit>

    /**
     * Get a column
     *
     * Responses:
     *  - 200:
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 500: Internal server error
     *
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param keyspaceId keyspace name
     * @param tableId table name
     * @param columnId column name
     * @param raw Unwrap results. (optional, default to false)
     * @return [ColumnDefinition]
     */
    @GET("api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns/{column-id}")
    suspend fun getColumn(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Path("table-id") tableId: kotlin.String, @Path("column-id") columnId: kotlin.String, @Query("raw") raw: kotlin.Boolean? = null): Response<ColumnDefinition>

    /**
     * List columns
     *
     * Responses:
     *  - 200:
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 500: Internal server error
     *
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param keyspaceId keyspace name
     * @param tableId table name
     * @param raw Unwrap results. (optional, default to false)
     * @return [InlineResponse2002]
     */
    @GET("api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns")
    suspend fun getColumns(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Path("table-id") tableId: kotlin.String, @Query("raw") raw: kotlin.Boolean? = null): Response<InlineResponse2002>

    /**
     * List indexes for a given table
     *
     * Responses:
     *  - 200:
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 500: Internal server error
     *
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param keyspaceId keyspace name
     * @param tableId table name
     * @param raw Unwrap results. (optional, default to false)
     * @return [kotlin.collections.List<kotlin.collections.List<kotlin.Any>>]
     */
    @GET("api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/indexes")
    suspend fun getIndexes(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Path("table-id") tableId: kotlin.String, @Query("raw") raw: kotlin.Boolean? = null): Response<kotlin.collections.List<kotlin.collections.List<kotlin.Any>>>

    /**
     * Get a keyspace using the {keyspace-id}
     *
     * Responses:
     *  - 200:
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 500: Internal server error
     *
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param keyspaceId keyspace name
     * @param raw Unwrap results. (optional, default to false)
     * @return [Keyspace]
     */
    @GET("api/rest/v2/schemas/keyspaces/{keyspace-id}")
    suspend fun getKeyspace(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Query("raw") raw: kotlin.Boolean? = null): Response<Keyspace>

    /**
     * Get all keyspaces
     * Retrieve all available keyspaces in the specific database.
     * Responses:
     *  - 200:
     *  - 401: Unauthorized
     *  - 500: Internal server error
     *
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param raw Unwrap results. (optional, default to false)
     * @return [InlineResponse200]
     */
    @GET("api/rest/v2/schemas/keyspaces")
    suspend fun getKeyspaces(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Query("raw") raw: kotlin.Boolean? = null): Response<InlineResponse200>

    /**
     * Get a table
     *
     * Responses:
     *  - 200:
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 500: Internal server error
     *
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param keyspaceId keyspace name
     * @param tableId table name
     * @param raw Unwrap results. (optional, default to false)
     * @return [Table]
     */
    @GET("api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}")
    suspend fun getTable(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Path("table-id") tableId: kotlin.String, @Query("raw") raw: kotlin.Boolean? = null): Response<Table>

    /**
     * Get all tables
     *
     * Responses:
     *  - 200:
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 500: Internal server error
     *
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param keyspaceId keyspace name
     * @param raw Unwrap results. (optional, default to false)
     * @return [InlineResponse2001]
     */
    @GET("api/rest/v2/schemas/keyspaces/{keyspace-id}/tables")
    suspend fun getTables(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Query("raw") raw: kotlin.Boolean? = null): Response<InlineResponse2001>

    /**
     * Replace a column definition
     *
     * Responses:
     *  - 200: resource updated
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 409: Conflict
     *  - 500: Internal server error
     *
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param keyspaceId keyspace name
     * @param tableId table name
     * @param columnId column name
     * @param columnDefinition
     * @return [InlineResponse201]
     */
    @PUT("api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns/{column-id}")
    suspend fun replaceColumn(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Path("table-id") tableId: kotlin.String, @Path("column-id") columnId: kotlin.String, @Body columnDefinition: ColumnDefinition): Response<InlineResponse201>

    /**
     * Replace a table definition, except for columns
     *
     * Responses:
     *  - 200: resource updated
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 404: Not Found
     *  - 409: Conflict
     *  - 500: Internal server error
     *
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
     * @param keyspaceId keyspace name
     * @param tableId table name
     * @param tableAdd
     * @return [InlineResponse201]
     */
    @PUT("api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}")
    suspend fun replaceTable(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Path("table-id") tableId: kotlin.String, @Body tableAdd: TableAdd): Response<InlineResponse201>
}
