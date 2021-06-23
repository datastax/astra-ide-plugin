package com.datastax.astra.stargate_rest_v2.apis

import com.datastax.astra.stargate_rest_v2.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody

import com.datastax.astra.stargate_rest_v2.models.Error
import com.datastax.astra.stargate_rest_v2.models.InlineResponse2003
import com.datastax.astra.stargate_rest_v2.models.InlineResponse2004

interface DataApi {
    /**
     * Add rows
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
     * @param requestBody  
     * @return [kotlin.collections.Map<kotlin.String, kotlin.String>]
     */
    @POST("api/rest/v2/keyspaces/{keyspace-id}/{table-id}")
    suspend fun addRows(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Path("table-id") tableId: kotlin.String, @Body requestBody: kotlin.collections.Map<kotlin.String, kotlin.String>): Response<kotlin.collections.Map<kotlin.String, kotlin.String>>

    /**
     * Delete a row by primary key
     * 
     * Responses:
     *  - 204: No Content
     *  - 401: Unauthorized
     *  - 500: Internal server error
     * 
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. 
     * @param keyspaceId keyspace name 
     * @param tableId table name 
     * @param primaryKey Value from the primary key column for the table. Define composite keys by separating values with slashes (&#x60;val1/val2...&#x60;) in the order they were defined. &lt;/br&gt; For example, if the composite key was defined as &#x60;PRIMARY KEY(race_year, race_name)&#x60; then the primary key in the path would be &#x60;race_year/race_name&#x60;  
     * @return [Unit]
     */
    @DELETE("api/rest/v2/keyspaces/{keyspace-id}/{table-id}/{primary-key}")
    suspend fun deleteRows(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Path("table-id") tableId: kotlin.String, @Path("primary-key") primaryKey: kotlin.String): Response<Unit>

    /**
     * Get a row
     * 
     * Responses:
     *  - 200: 
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 500: Internal server error
     * 
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. 
     * @param keyspaceId keyspace name 
     * @param tableId table name 
     * @param primaryKey Value from the primary key column for the table. Define composite keys by separating values with slashes (&#x60;val1/val2...&#x60;) in the order they were defined. &lt;/br&gt; For example, if the composite key was defined as &#x60;PRIMARY KEY(race_year, race_name)&#x60; then the primary key in the path would be &#x60;race_year/race_name&#x60;  
     * @param fields URL escaped, comma delimited list of keys to include (optional)
     * @param pageSize restrict the number of returned items (optional)
     * @param pageState move the cursor to a particular result (optional)
     * @param sort keys to sort by (optional)
     * @param raw Unwrap results. (optional, default to false)
     * @return [InlineResponse2003]
     */
    @GET("api/rest/v2/keyspaces/{keyspace-id}/{table-id}/{primary-key}")
    suspend fun getRows(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Path("table-id") tableId: kotlin.String, @Path("primary-key") primaryKey: kotlin.String, @Query("fields") fields: kotlin.String? = null, @Query("page-size") pageSize: kotlin.Int? = null, @Query("page-state") pageState: kotlin.String? = null, @Query("sort") sort: kotlin.collections.Map<kotlin.String, kotlin.String>? = null, @Query("raw") raw: kotlin.Boolean? = null): Response<InlineResponse2003>

    /**
     * Replace a row by primary key
     * 
     * Responses:
     *  - 200: resource updated
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 500: Internal server error
     * 
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. 
     * @param keyspaceId keyspace name 
     * @param tableId table name 
     * @param primaryKey Value from the primary key column for the table. Define composite keys by separating values with slashes (&#x60;val1/val2...&#x60;) in the order they were defined. &lt;/br&gt; For example, if the composite key was defined as &#x60;PRIMARY KEY(race_year, race_name)&#x60; then the primary key in the path would be &#x60;race_year/race_name&#x60;  
     * @param requestBody document 
     * @param raw Unwrap results. (optional, default to false)
     * @return [InlineResponse2004]
     */
    @PUT("api/rest/v2/keyspaces/{keyspace-id}/{table-id}/{primary-key}")
    suspend fun replaceRows(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Path("table-id") tableId: kotlin.String, @Path("primary-key") primaryKey: kotlin.String, @Body requestBody: kotlin.collections.Map<kotlin.String, kotlin.String>, @Query("raw") raw: kotlin.Boolean? = null): Response<InlineResponse2004>

    /**
     * Search a table
     * 
     * Responses:
     *  - 200: 
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 500: Internal server error
     * 
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. 
     * @param keyspaceId keyspace name 
     * @param tableId table name 
     * @param where URL escaped JSON query using the following keys:  | Key | Operation | |---|---| | $lt | Less Than | | $lte | Less Than Or Equal To | | $gt | Greater Than | | $gte | Greater Than Or Equal To | | $ne | Not Equal To | | $in | Contained In | | $exists | A value is set for the key |  (optional)
     * @param fields URL escaped, comma delimited list of keys to include (optional)
     * @param pageSize restrict the number of returned items (optional)
     * @param pageState move the cursor to a particular result (optional)
     * @param sort keys to sort by (optional)
     * @param raw Unwrap results. (optional, default to false)
     * @return [InlineResponse2003]
     */
    @GET("api/rest/v2/keyspaces/{keyspace-id}/{table-id}")
    suspend fun searchTable(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Path("table-id") tableId: kotlin.String, @Query("where") where: kotlin.Any? = null, @Query("fields") fields: kotlin.String? = null, @Query("page-size") pageSize: kotlin.Int? = null, @Query("page-state") pageState: kotlin.String? = null, @Query("sort") sort: kotlin.collections.Map<kotlin.String, kotlin.String>? = null, @Query("raw") raw: kotlin.Boolean? = null): Response<InlineResponse2003>

    /**
     * Update a row by primary key
     * 
     * Responses:
     *  - 200: resource updated
     *  - 400: Bad Request
     *  - 401: Unauthorized
     *  - 500: Internal server error
     * 
     * @param xCassandraToken The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. 
     * @param keyspaceId keyspace name 
     * @param tableId table name 
     * @param primaryKey Value from the primary key column for the table. Define composite keys by separating values with slashes (&#x60;val1/val2...&#x60;) in the order they were defined. &lt;/br&gt; For example, if the composite key was defined as &#x60;PRIMARY KEY(race_year, race_name)&#x60; then the primary key in the path would be &#x60;race_year/race_name&#x60;  
     * @param requestBody document 
     * @param raw Unwrap results. (optional, default to false)
     * @return [InlineResponse2004]
     */
    @PATCH("api/rest/v2/keyspaces/{keyspace-id}/{table-id}/{primary-key}")
    suspend fun updateRows(@Header("X-Cassandra-Token") xCassandraToken: kotlin.String, @Path("keyspace-id") keyspaceId: kotlin.String, @Path("table-id") tableId: kotlin.String, @Path("primary-key") primaryKey: kotlin.String, @Body requestBody: kotlin.collections.Map<kotlin.String, kotlin.String>, @Query("raw") raw: kotlin.Boolean? = null): Response<InlineResponse2004>

}
