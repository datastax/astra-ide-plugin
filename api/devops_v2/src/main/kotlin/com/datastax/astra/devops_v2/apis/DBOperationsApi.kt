package com.datastax.astra.devops_v2.apis

import com.datastax.astra.devops_v2.infrastructure.CollectionFormats.*
import com.datastax.astra.devops_v2.models.AvailableRegionCombination
import com.datastax.astra.devops_v2.models.CapacityUnits
import com.datastax.astra.devops_v2.models.ClientRoleList
import com.datastax.astra.devops_v2.models.CreateRoleRequest
import com.datastax.astra.devops_v2.models.CredsURL
import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.devops_v2.models.DatabaseInfoCreate
import com.datastax.astra.devops_v2.models.GenerateTokenBody
import com.datastax.astra.devops_v2.models.GenerateTokenResponse
import com.datastax.astra.devops_v2.models.GetOrganizationUsersResponse
import com.datastax.astra.devops_v2.models.Organization
import com.datastax.astra.devops_v2.models.Role
import com.datastax.astra.devops_v2.models.RoleInviteRequest
import com.datastax.astra.devops_v2.models.UpdateRoleRequest
import com.datastax.astra.devops_v2.models.UserInvite
import com.datastax.astra.devops_v2.models.UserPassword
import com.datastax.astra.devops_v2.models.UserResponse
import retrofit2.Response
import retrofit2.http.*

interface DBOperationsApi {
    /**
     * Adds keyspace into database
     * Adds the specified keyspace to the database.
     * Responses:
     *  - 201: created
     *  - 401: The user is unauthorized to perform the operation.
     *  - 404: The specified database was not found.
     *  - 422: The structured data in the request could not be parsed.
     *  - 5XX: A server error occurred.
     *
     * @param databaseID String representation of the database ID
     * @param keyspaceName Name of database keyspace
     * @return [Unit]
     */
    @POST("v2/databases/{databaseID}/keyspaces/{keyspaceName}")
    suspend fun addKeyspace(@Path("databaseID") databaseID: kotlin.String, @Path("keyspaceName") keyspaceName: kotlin.String): Response<Unit>

    /**
     * Create a role in an organization
     *
     * Responses:
     *  - 201: The request completed successfully and created an object.
     *  - 400: Bad request.
     *  - 403: The user is forbidden to perform the operation.
     *  - 404: The specified database was not found.
     *  - 409: The database is not in a valid state to perform the operation.
     *  - 500: A server error occurred.
     *
     * @param createRoleRequest The model for create role body. (optional)
     * @return [Unit]
     */
    @POST("v2/organizations/roles")
    suspend fun addOrganizationRole(@Body createRoleRequest: CreateRoleRequest? = null): Response<Unit>

    /**
     * Create a new database
     * Takes a user provided databaseInfo and returns the uuid for a new database.
     * Responses:
     *  - 201: created
     *  - 400: Bad request.
     *  - 401: The user is unauthorized to perform the operation.
     *  - 422: The structured data in the request could not be parsed.
     *  - 500: A server error occurred.
     *
     * @param databaseInfoCreate Definition of new database.
     * @return [Unit]
     */
    @POST("v2/databases")
    suspend fun createDatabase(@Body databaseInfoCreate: DatabaseInfoCreate): Response<Unit>

    /**
     * Delete a role by ID
     *
     * Responses:
     *  - 204: The request succeeded and no content is returned in response body.
     *  - 400: Bad request.
     *  - 404: The specified database was not found.
     *  - 500: A server error occurred.
     *
     * @param roleID id for the role
     * @return [Unit]
     */
    @DELETE("v2/organizations/roles/{roleID}")
    suspend fun deleteOrganizationRole(@Path("roleID") roleID: kotlin.String): Response<Unit>

    /**
     * Revokes a token
     *
     * Responses:
     *  - 200: The request completed successfully.
     *  - 400: Bad request.
     *  - 403: The user is forbidden to perform the operation.
     *  - 500: A server error occurred.
     *
     * @param clientId clientID to revoke token for
     * @return [Unit]
     */
    @DELETE("v2/clientIdSecret/{clientId}")
    suspend fun deleteTokenForClient(@Path("clientId") clientId: kotlin.String): Response<Unit>

    /**
     * Obtain zip for connecting to the database
     * Returns a temporary URL to download a zip file with certificates for connecting to the database. The URL expires after five minutes.&lt;p&gt;There are two types of the secure bundle URL: &lt;ul&gt;&lt;li&gt;&lt;b&gt;Internal&lt;/b&gt; - Use with VPC peering connections to use private networking and avoid public internet for communication.&lt;/li&gt; &lt;li&gt;&lt;b&gt;External&lt;/b&gt; - Use with any connection where the public internet is sufficient for communication between the application and the Astra database with MTLS.&lt;/li&gt;&lt;/ul&gt; Both types support MTLS for communication via the driver.&lt;/p&gt;
     * Responses:
     *  - 200: Credentials provides a link to download cluster secure-connect-*.zip file
     *  - 400: Bad request.
     *  - 401: The user is unauthorized to perform the operation.
     *  - 404: The specified database was not found.
     *  - 409: The database is not in a valid state to perform the operation.
     *  - 5XX: A server error occurred.
     *
     * @param databaseID String representation of the database ID
     * @return [CredsURL]
     */
    @POST("v2/databases/{databaseID}/secureBundleURL")
    suspend fun generateSecureBundleURL(@Path("databaseID") databaseID: kotlin.String): Response<CredsURL>

    /**
     * Generate token for client
     *
     * Responses:
     *  - 200: successful operation
     *  - 400: Bad request.
     *  - 403: The user is forbidden to perform the operation.
     *  - 500: A server error occurred.
     *
     * @param generateTokenBody The model for generating token for client. (optional)
     * @return [kotlin.collections.List<GenerateTokenResponse>]
     */
    @POST("v2/clientIdSecrets")
    suspend fun generateTokenForClient(@Body generateTokenBody: GenerateTokenBody? = null): Response<kotlin.collections.List<GenerateTokenResponse>>

    /**
     * Get a list of clients for an org
     *
     * Responses:
     *  - 200: successful operation
     *  - 400: Bad request.
     *  - 403: The user is forbidden to perform the operation.
     *  - 500: A server error occurred.
     *
     * @return [kotlin.collections.List<ClientRoleList>]
     */
    @GET("v2/clientIdSecrets")
    suspend fun getClientsForOrg(): Response<kotlin.collections.List<ClientRoleList>>

    /**
     * Get the current organization from the passed token
     * Retrieve the details for the organization in the provided token.
     * Responses:
     *  - 200: successful operation
     *  - 400: Bad request.
     *  - 403: The user is forbidden to perform the operation.
     *  - 404: The specified database was not found.
     *  - 500: A server error occurred.
     *
     * @return [kotlin.collections.List<Organization>]
     */
    @GET("v2/currentOrg")
    suspend fun getCurrentOrganization(): Response<kotlin.collections.List<Organization>>

    /**
     * Finds database by ID
     * Returns specified database.
     * Responses:
     *  - 200: successful operation
     *  - 400: Bad request.
     *  - 401: The user is unauthorized to perform the operation.
     *  - 404: The specified database was not found.
     *  - 5XX: A server error occurred.
     *
     * @param databaseID String representation of the database ID
     * @return [Database]
     */
    @GET("v2/databases/{databaseID}")
    suspend fun getDatabase(@Path("databaseID") databaseID: kotlin.String): Response<Database>

    /**
     * Get a role for an organization
     * Retrieve the details for a role for a given organization.
     * Responses:
     *  - 200: successful operation
     *  - 400: Bad request.
     *  - 403: The user is forbidden to perform the operation.
     *  - 404: The specified database was not found.
     *  - 500: A server error occurred.
     *
     * @param roleID id for the role
     * @return [kotlin.collections.List<Role>]
     */
    @GET("v2/organizations/roles/{roleID}")
    suspend fun getOrganizationRole(@Path("roleID") roleID: kotlin.String): Response<kotlin.collections.List<Role>>

    /**
     * Get all roles for an organization
     *
     * Responses:
     *  - 200: successful operation
     *  - 403: The user is forbidden to perform the operation.
     *  - 404: The specified database was not found.
     *  - 500: A server error occurred.
     *
     * @return [kotlin.collections.List<kotlin.collections.List<Role>>]
     */
    @GET("v2/organizations/roles")
    suspend fun getOrganizationRoles(): Response<kotlin.collections.List<kotlin.collections.List<Role>>>

    /**
     * Get an organization&#39;s user
     *
     * Responses:
     *  - 200: OK
     *  - 403: The user is forbidden to perform the operation.
     *  - 404: The specified database was not found.
     *  - 500: A server error occurred.
     *
     * @param userID id for the user
     * @return [UserResponse]
     */
    @GET("v2/organizations/users/{userID}")
    suspend fun getOrganizationUser(@Path("userID") userID: kotlin.String): Response<UserResponse>

    /**
     * Get an organization&#39;s users
     *
     * Responses:
     *  - 200: List of users for the current org
     *  - 404: The specified database was not found.
     *  - 500: A server error occurred.
     *
     * @return [GetOrganizationUsersResponse]
     */
    @GET("v2/organizations/users")
    suspend fun getOrganizationUsers(): Response<GetOrganizationUsersResponse>

    /**
     * Invite a user to an organization
     * Invite a user to an organization or resend an invitation with new invitation details, such as an updated expiration
     * Responses:
     *  - 200: successful operation
     *  - 400: Bad request.
     *  - 403: The user is forbidden to perform the operation.
     *  - 404: The specified database was not found.
     *  - 500: A server error occurred.
     *
     * @param userInvite  (optional)
     * @return [Unit]
     */
    @PUT("v2/organizations/users")
    suspend fun inviteUserToOrganization(@Body userInvite: UserInvite? = null): Response<Unit>

    /**
     * Returns supported regions and availability for a given user and organization
     * Returns all supported tier, cloud, region, count, and capacitity combinations.
     * Responses:
     *  - 200: successful operation
     *  - 401: The user is unauthorized to perform the operation.
     *  - 5XX: A server error occurred.
     *
     * @return [kotlin.collections.List<AvailableRegionCombination>]
     */
    @GET("v2/availableRegions")
    suspend fun listAvailableRegions(): Response<kotlin.collections.List<AvailableRegionCombination>>

    /**
     * Returns a list of databases
     * Get a list of databases visible to the user.
     * Responses:
     *  - 200: successful operation
     *  - 400: Bad request.
     *  - 401: The user is unauthorized to perform the operation.
     *  - 5XX: A server error occurred.
     *
     * @param include Allows filtering so that databases in listed states are returned. (optional, default to nonterminated)
     * @param provider Allows filtering so that databases from a given provider are returned. (optional, default to ALL)
     * @param startingAfter Optional parameter for pagination purposes. Used as this value for starting retrieving a specific page of results. (optional)
     * @param limit Optional parameter for pagination purposes. Specify the number of items for one page of data. (optional, default to 25)
     * @return [kotlin.collections.List<Database>]
     */
    @GET("v2/databases")
    suspend fun listDatabases(@Query("include") include: kotlin.String? = null, @Query("provider") provider: kotlin.String? = null, @Query("starting_after") startingAfter: kotlin.String? = null, @Query("limit") limit: kotlin.Int? = null): Response<kotlin.collections.List<Database>>

    /**
     * Parks a database
     * Parks a database.
     * Responses:
     *  - 202: The request was accepted.
     *  - 400: Bad request.
     *  - 401: The user is unauthorized to perform the operation.
     *  - 404: The specified database was not found.
     *  - 409: The database is not in a valid state to perform the operation.
     *  - 5XX: A server error occurred.
     *
     * @param databaseID String representation of the database ID
     * @return [Unit]
     */
    @POST("v2/databases/{databaseID}/park")
    suspend fun parkDatabase(@Path("databaseID") databaseID: kotlin.String): Response<Unit>

    /**
     * Remove or uninvite a user from an organization
     *
     * Responses:
     *  - 204: The request succeeded and no content is returned in response body.
     *  - 400: Bad request.
     *  - 403: The user is forbidden to perform the operation.
     *  - 404: The specified database was not found.
     *  - 500: A server error occurred.
     *
     * @param userID id for the user
     * @return [Unit]
     */
    @DELETE("v2/organizations/users/{userID}")
    suspend fun removeUserFromOrganization(@Path("userID") userID: kotlin.String): Response<Unit>

    /**
     * Resets Password
     * Sets a database password to the one specified in POST body.
     * Responses:
     *  - 202: The request was accepted.
     *  - 400: Bad request.
     *  - 401: The user is unauthorized to perform the operation.
     *  - 404: The specified database was not found.
     *  - 409: The database is not in a valid state to perform the operation.
     *  - 5XX: A server error occurred.
     *
     * @param databaseID String representation of the database ID
     * @param userPassword Map containing username and password. The specified password will be updated for the specified database user.
     * @return [Unit]
     */
    @POST("v2/databases/{databaseID}/resetPassword")
    suspend fun resetPassword(@Path("databaseID") databaseID: kotlin.String, @Body userPassword: UserPassword): Response<Unit>

    /**
     * Resizes a database
     * Resizes a database. Total number of capacity units desired should be specified. Reducing a size of a database is not supported at this time.
     * Responses:
     *  - 202: The request was accepted.
     *  - 400: Bad request.
     *  - 401: The user is unauthorized to perform the operation.
     *  - 404: The specified database was not found.
     *  - 409: The database is not in a valid state to perform the operation.
     *  - 5XX: A server error occurred.
     *
     * @param databaseID String representation of the database ID
     * @param capacityUnits Map containing capacityUnits key with a value greater than the current number of capacity units. The max increment of 3 additional capacity units can be added at one time.
     * @return [Unit]
     */
    @POST("v2/databases/{databaseID}/resize")
    suspend fun resizeDatabase(@Path("databaseID") databaseID: kotlin.String, @Body capacityUnits: CapacityUnits): Response<Unit>

    /**
     * Terminates a database
     * Terminates a database.
     * Responses:
     *  - 202: The request was accepted.
     *  - 400: Bad request.
     *  - 401: The user is unauthorized to perform the operation.
     *  - 404: The specified database was not found.
     *  - 409: The database is not in a valid state to perform the operation.
     *  - 5XX: A server error occurred.
     *
     * @param databaseID String representation of the database ID
     * @param preparedStateOnly For internal use only. Used to safely terminate prepared databases. (optional, default to false)
     * @return [Unit]
     */
    @POST("v2/databases/{databaseID}/terminate")
    suspend fun terminateDatabase(@Path("databaseID") databaseID: kotlin.String, @Query("preparedStateOnly") preparedStateOnly: kotlin.Boolean? = null): Response<Unit>

    /**
     * Unparks a database
     * Unparks a database.
     * Responses:
     *  - 202: The request was accepted.
     *  - 400: Bad request.
     *  - 401: The user is unauthorized to perform the operation.
     *  - 404: The specified database was not found.
     *  - 409: The database is not in a valid state to perform the operation.
     *  - 5XX: A server error occurred.
     *
     * @param databaseID String representation of the database ID
     * @return [Unit]
     */
    @POST("v2/databases/{databaseID}/unpark")
    suspend fun unparkDatabase(@Path("databaseID") databaseID: kotlin.String): Response<Unit>

    /**
     * Update a role within an organization
     * Update a role within an organization.
     * Responses:
     *  - 200: successful operation
     *  - 400: Bad request.
     *  - 403: The user is forbidden to perform the operation.
     *  - 404: The specified database was not found.
     *  - 500: A server error occurred.
     *
     * @param roleID id for the role
     * @param updateRoleRequest The model for update role body (optional)
     * @return [kotlin.collections.List<Role>]
     */
    @PUT("v2/organizations/roles/{roleID}")
    suspend fun updateRole(@Path("roleID") roleID: kotlin.String, @Body updateRoleRequest: UpdateRoleRequest? = null): Response<kotlin.collections.List<Role>>

    /**
     * Update organization roles for a user
     *
     * Responses:
     *  - 204: The request succeeded and no content is returned in response body.
     *  - 400: Bad request.
     *  - 403: The user is forbidden to perform the operation.
     *  - 404: The specified database was not found.
     *  - 500: A server error occurred.
     *
     * @param userID id for the user
     * @param roleInviteRequest  (optional)
     * @return [Unit]
     */
    @PUT("v2/organizations/users/{userID}/roles")
    suspend fun updateRolesForUserInOrganization(@Path("userID") userID: kotlin.String, @Body roleInviteRequest: RoleInviteRequest? = null): Response<Unit>
}
