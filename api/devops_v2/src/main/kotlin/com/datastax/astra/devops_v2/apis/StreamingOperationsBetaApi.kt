package com.datastax.astra.devops_v2.apis

import com.datastax.astra.devops_v2.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody

import com.datastax.astra.devops_v2.models.CloudProviderRegionResponse
import com.datastax.astra.devops_v2.models.TenantClusterPlanResponse
import com.datastax.astra.devops_v2.models.TenantLimitResponse
import com.datastax.astra.devops_v2.models.TenantRequest

interface StreamingOperationsBetaApi {
    /**
     * Creates a tenant
     * Creates a tenant, the default namespace, a plan policy, and a Pulsar JWT.&lt;/br&gt;&lt;/br&gt; &lt;strong&gt;TIP\\:&lt;/strong&gt;  You can use the Apache Pulsar &lt;a href&#x3D;\&quot;https://pulsar.apache.org/admin-rest-api/?version&#x3D;2.7.1&amp;apiversion&#x3D;v2\&quot;&gt;REST administration interface&lt;/a&gt; or command line utilities to perform additional CRUD operations.
     * Responses:
     *  - 200: Tenant successfully created
     *  - 401: Authentication failure
     *  - 409: Tenant already exists
     *  - 500: Failed to read the HTTP body
     * 
     * @param tenantRequest Request body for tenant creation 
     * @param topic A topic name for auto-creation (if not specified, no topic is created). (optional)
     * @return [kotlin.collections.List<TenantClusterPlanResponse>]
     */
    @POST("v2/streaming/tenants")
    suspend fun createTenant(@Body tenantRequest: TenantRequest, @Query("topic") topic: kotlin.String? = null): Response<kotlin.collections.List<TenantClusterPlanResponse>>

    /**
     * Deletes a tenant
     * Deletes a tenant from a cluster.
     * Responses:
     *  - 202: Tenant deleted successfully
     *  - 401: Authentication failure
     *  - 429: over rate limit
     *  - 500: Failed to read the HTTP body
     * 
     * @param tenant Tenant name 
     * @param cluster Cluster name 
     * @param opt Performs a soft delete that only marks the tenant as deleted in the database (opt&#x3D;soft). (optional)
     * @return [Unit]
     */
    @DELETE("v2/streaming/tenants/{tenant}/clusters/{cluster}")
    suspend fun deleteTenant(@Path("tenant") tenant: kotlin.String, @Path("cluster") cluster: kotlin.String, @Query("opt") opt: kotlin.String? = null): Response<Unit>

    /**
     * Returns 200 if the tenant exists
     * Returns 200 if the tenant name exists
     * Responses:
     *  - 200: Tenant name exists
     *  - 401: Authentication failure
     *  - 404: Tenant name not found
     *  - 429: Over rate limit
     *  - 500: Failed to read the HTTP body
     * 
     * @param tenant Tenant name 
     * @return [Unit]
     */
    @HEAD("v2/streaming/tenants/{tenant}")
    suspend fun evaluateTenantName(@Path("tenant") tenant: kotlin.String): Response<Unit>

    /**
     * Gets a list of providers and regions
     * Returns all available providers and all regions under each provider.
     * Responses:
     *  - 200: All providers and regions under each provider
     *  - 401: Authentication failure
     *  - 429: Over rate limit
     *  - 500: Failed to read the HTTP body
     * 
     * @return [CloudProviderRegionResponse]
     */
    @GET("v2/streaming/providers")
    suspend fun getCloudProvidersRegions(): Response<CloudProviderRegionResponse>

    /**
     * Gets a tenant&#39;s number of namespaces and topics, and the limits for both
     * For the specified tenant, returns the current number of namespaces and topics, and the limits for both.
     * Responses:
     *  - 200: List of tenant namespaces, topics, and the limits and current usage for both
     *  - 401: Authentication failure
     *  - 429: Over rate limit
     *  - 500: Failed to read the HTTP body
     * 
     * @param tenant Tenant name 
     * @return [kotlin.collections.List<TenantLimitResponse>]
     */
    @GET("v2/streaming/tenants/{tenant}/limits")
    suspend fun getTenantLimit(@Path("tenant") tenant: kotlin.String): Response<kotlin.collections.List<TenantLimitResponse>>

    /**
     * Gets a list of tenants from the token&#39;s auth header
     * Lists all Pulsar tenants under the organization that is retrieved from the token in the authorization header.
     * Responses:
     *  - 200: List of tenants successfully retrieved
     *  - 401: Authentication failure
     *  - 429: over rate limit
     *  - 500: Failed to read the HTTP body
     * 
     * @return [kotlin.collections.List<TenantClusterPlanResponse>]
     */
    @GET("v2/streaming/tenants")
    suspend fun getTenants(): Response<kotlin.collections.List<TenantClusterPlanResponse>>

}
