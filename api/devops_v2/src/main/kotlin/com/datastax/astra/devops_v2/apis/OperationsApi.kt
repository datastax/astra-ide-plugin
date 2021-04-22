package com.datastax.astra.devops_v2.apis

import com.datastax.astra.devops_v2.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody

import com.datastax.astra.devops_v2.models.AvailableRegionCombination
import com.datastax.astra.devops_v2.models.CapacityUnits
import com.datastax.astra.devops_v2.models.CredsURL
import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.devops_v2.models.DatabaseInfoCreate
import com.datastax.astra.devops_v2.models.Errors
import com.datastax.astra.devops_v2.models.UserPassword

interface OperationsApi {
    /**
     * Adds keyspace into database
     * Adds the specified keyspace to the database
     * Responses:
     *  - 201: created
     *  - 401: The user is unauthorized to perform the operation
     *  - 404: The specified database was not found
     *  - 422: The structured data in the request could not be parsed
     *  - 5XX: A server error occurred
     * 
     * @param databaseID String representation of the database ID 
     * @param keyspaceName Name of database keyspace 
     * @return [Unit]
     */
    @POST("v2/databases/{databaseID}/keyspaces/{keyspaceName}")
    suspend fun addKeyspace(@Path("databaseID") databaseID: kotlin.String, @Path("keyspaceName") keyspaceName: kotlin.String): Response<Unit>

    /**
     * Create a new database
     * Takes a user provided databaseInfo and returns the uuid for a new database
     * Responses:
     *  - 201: created
     *  - 400: Bad request
     *  - 401: The user is unauthorized to perform the operation
     *  - 422: The structured data in the request could not be parsed
     *  - 500: A server error occurred
     * 
     * @param databaseInfoCreate Definition of new database 
     * @return [Unit]
     */
    @POST("v2/databases")
    suspend fun createDatabase(@Body databaseInfoCreate: DatabaseInfoCreate): Response<Unit>

    /**
     * Obtain zip for connecting to the database
     * Returns a temporary URL to download a zip file with certificates for connecting to the database. The URL expires after five minutes.&lt;p&gt;There are two types of the secure bundle URL: &lt;ul&gt;&lt;li&gt;&lt;b&gt;Internal&lt;/b&gt; - Use with VPC peering connections to use private networking and avoid public internet for communication.&lt;/li&gt; &lt;li&gt;&lt;b&gt;External&lt;/b&gt; - Use with any connection where the public internet is sufficient for communication between the application and the Astra database with MTLS.&lt;/li&gt;&lt;/ul&gt; Both types support MTLS for communication via the driver.&lt;/p&gt;
     * Responses:
     *  - 200: Credentials provides a link to download cluster secure-connect-*.zip file
     *  - 400: Bad request
     *  - 401: The user is unauthorized to perform the operation
     *  - 404: The specified database was not found
     *  - 409: The database is not in a valid state to perform the operation
     *  - 5XX: A server error occurred
     * 
     * @param databaseID String representation of the database ID 
     * @return [CredsURL]
     */
    @POST("v2/databases/{databaseID}/secureBundleURL")
    suspend fun generateSecureBundleURL(@Path("databaseID") databaseID: kotlin.String): Response<CredsURL>

    /**
     * Finds database by ID
     * Returns specified database
     * Responses:
     *  - 200: successful operation
     *  - 400: Bad request
     *  - 401: The user is unauthorized to perform the operation
     *  - 404: The specified database was not found
     *  - 5XX: A server error occurred
     * 
     * @param databaseID String representation of the database ID 
     * @return [Database]
     */
    @GET("v2/databases/{databaseID}")
    suspend fun getDatabase(@Path("databaseID") databaseID: kotlin.String): Response<Database>

    /**
     * Returns supported regions and availability for a given user and organization
     * Returns all supported tier, cloud, region, count, and capacitity combinations
     * Responses:
     *  - 200: successful operation
     *  - 401: The user is unauthorized to perform the operation
     *  - 5XX: A server error occurred
     * 
     * @return [kotlin.collections.List<AvailableRegionCombination>]
     */
    @GET("v2/availableRegions")
    suspend fun listAvailableRegions(): Response<kotlin.collections.List<AvailableRegionCombination>>

    /**
     * Returns a list of databases
     * Get a list of databases visible to the user
     * Responses:
     *  - 200: successful operation
     *  - 400: Bad request
     *  - 401: The user is unauthorized to perform the operation
     *  - 5XX: A server error occurred
     * 
     * @param include Allows filtering so that databases in listed states are returned (optional, default to nonterminated)
     * @param provider Allows filtering so that databases from a given provider are returned (optional, default to ALL)
     * @param startingAfter Optional parameter for pagination purposes. Used as this value for starting retrieving a specific page of results (optional)
     * @param limit Optional parameter for pagination purposes. Specify the number of items for one page of data (optional, default to 25)
     * @return [kotlin.collections.List<Database>]
     */
    @GET("v2/databases")
    suspend fun listDatabases(@Query("include") include: kotlin.String? = null, @Query("provider") provider: kotlin.String? = null, @Query("starting_after") startingAfter: kotlin.String? = null, @Query("limit") limit: kotlin.Int? = null): Response<kotlin.collections.List<Database>>

    /**
     * Parks a database
     * Parks a database
     * Responses:
     *  - 202: The request was accepted
     *  - 400: Bad request
     *  - 401: The user is unauthorized to perform the operation
     *  - 404: The specified database was not found
     *  - 409: The database is not in a valid state to perform the operation
     *  - 5XX: A server error occurred
     * 
     * @param databaseID String representation of the database ID 
     * @return [Unit]
     */
    @POST("v2/databases/{databaseID}/park")
    suspend fun parkDatabase(@Path("databaseID") databaseID: kotlin.String): Response<Unit>

    /**
     * Resets Password
     * Sets a database password to the one specified in POST body
     * Responses:
     *  - 202: The request was accepted
     *  - 400: Bad request
     *  - 401: The user is unauthorized to perform the operation
     *  - 404: The specified database was not found
     *  - 409: The database is not in a valid state to perform the operation
     *  - 5XX: A server error occurred
     * 
     * @param databaseID String representation of the database ID 
     * @param userPassword Map containing username and password. The specified password will be updated for the specified database user 
     * @return [Unit]
     */
    @POST("v2/databases/{databaseID}/resetPassword")
    suspend fun resetPassword(@Path("databaseID") databaseID: kotlin.String, @Body userPassword: UserPassword): Response<Unit>

    /**
     * Resizes a database
     * Resizes a database. Total number of capacity units desired should be specified. Reducing a size of a database is not supported at this time.
     * Responses:
     *  - 202: The request was accepted
     *  - 400: Bad request
     *  - 401: The user is unauthorized to perform the operation
     *  - 404: The specified database was not found
     *  - 409: The database is not in a valid state to perform the operation
     *  - 5XX: A server error occurred
     * 
     * @param databaseID String representation of the database ID 
     * @param capacityUnits Map containing capacityUnits key with a value greater than the current number of capacity units (max increment of 3 additional capacity units) 
     * @return [Unit]
     */
    @POST("v2/databases/{databaseID}/resize")
    suspend fun resizeDatabase(@Path("databaseID") databaseID: kotlin.String, @Body capacityUnits: CapacityUnits): Response<Unit>

    /**
     * Terminates a database
     * Terminates a database
     * Responses:
     *  - 202: The request was accepted
     *  - 400: Bad request
     *  - 401: The user is unauthorized to perform the operation
     *  - 404: The specified database was not found
     *  - 409: The database is not in a valid state to perform the operation
     *  - 5XX: A server error occurred
     * 
     * @param databaseID String representation of the database ID 
     * @param preparedStateOnly For internal use only.  Used to safely terminate prepared databases. (optional, default to false)
     * @return [Unit]
     */
    @POST("v2/databases/{databaseID}/terminate")
    suspend fun terminateDatabase(@Path("databaseID") databaseID: kotlin.String, @Query("preparedStateOnly") preparedStateOnly: kotlin.Boolean? = null): Response<Unit>

    /**
     * Unparks a database
     * Unparks a database
     * Responses:
     *  - 202: The request was accepted
     *  - 400: Bad request
     *  - 401: The user is unauthorized to perform the operation
     *  - 404: The specified database was not found
     *  - 409: The database is not in a valid state to perform the operation
     *  - 5XX: A server error occurred
     * 
     * @param databaseID String representation of the database ID 
     * @return [Unit]
     */
    @POST("v2/databases/{databaseID}/unpark")
    suspend fun unparkDatabase(@Path("databaseID") databaseID: kotlin.String): Response<Unit>

}
