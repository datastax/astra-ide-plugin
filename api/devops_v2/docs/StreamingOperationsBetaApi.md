# StreamingOperationsBetaApi

All URIs are relative to *https://api.astra.datastax.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createTenant**](StreamingOperationsBetaApi.md#createTenant) | **POST** v2/streaming/tenants | Creates a tenant
[**deleteTenant**](StreamingOperationsBetaApi.md#deleteTenant) | **DELETE** v2/streaming/tenants/{tenant}/clusters/{cluster} | Deletes a tenant
[**evaluateTenantName**](StreamingOperationsBetaApi.md#evaluateTenantName) | **HEAD** v2/streaming/tenants/{tenant} | Returns 200 if the tenant exists
[**getCloudProvidersRegions**](StreamingOperationsBetaApi.md#getCloudProvidersRegions) | **GET** v2/streaming/providers | Gets a list of providers and regions
[**getTenantLimit**](StreamingOperationsBetaApi.md#getTenantLimit) | **GET** v2/streaming/tenants/{tenant}/limits | Gets a tenant&#39;s number of namespaces and topics, and the limits for both
[**getTenants**](StreamingOperationsBetaApi.md#getTenants) | **GET** v2/streaming/tenants | Gets a list of tenants from the token&#39;s auth header



Creates a tenant

Creates a tenant, the default namespace, a plan policy, and a Pulsar JWT.&lt;/br&gt;&lt;/br&gt; &lt;strong&gt;TIP\\:&lt;/strong&gt;  You can use the Apache Pulsar &lt;a href&#x3D;\&quot;https://pulsar.apache.org/admin-rest-api/?version&#x3D;2.7.1&amp;apiversion&#x3D;v2\&quot;&gt;REST administration interface&lt;/a&gt; or command line utilities to perform additional CRUD operations.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(StreamingOperationsBetaApi::class.java)
val tenantRequest : TenantRequest =  // TenantRequest | Request body for tenant creation
val topic : kotlin.String = topic_example // kotlin.String | A topic name for auto-creation (if not specified, no topic is created).

launch(Dispatchers.IO) {
    val result : kotlin.collections.List<TenantClusterPlanResponse> = webService.createTenant(tenantRequest, topic)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **tenantRequest** | [**TenantRequest**](TenantRequest.md)| Request body for tenant creation |
 **topic** | **kotlin.String**| A topic name for auto-creation (if not specified, no topic is created). | [optional]

### Return type

[**kotlin.collections.List&lt;TenantClusterPlanResponse&gt;**](TenantClusterPlanResponse.md)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Deletes a tenant

Deletes a tenant from a cluster.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(StreamingOperationsBetaApi::class.java)
val tenant : kotlin.String = tenant_example // kotlin.String | Tenant name
val cluster : kotlin.String = cluster_example // kotlin.String | Cluster name
val opt : kotlin.String = opt_example // kotlin.String | Performs a soft delete that only marks the tenant as deleted in the database (opt=soft).

launch(Dispatchers.IO) {
    webService.deleteTenant(tenant, cluster, opt)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **tenant** | **kotlin.String**| Tenant name |
 **cluster** | **kotlin.String**| Cluster name |
 **opt** | **kotlin.String**| Performs a soft delete that only marks the tenant as deleted in the database (opt&#x3D;soft). | [optional]

### Return type

null (empty response body)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Returns 200 if the tenant exists

Returns 200 if the tenant name exists

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(StreamingOperationsBetaApi::class.java)
val tenant : kotlin.String = tenant_example // kotlin.String | Tenant name

launch(Dispatchers.IO) {
    webService.evaluateTenantName(tenant)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **tenant** | **kotlin.String**| Tenant name |

### Return type

null (empty response body)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Gets a list of providers and regions

Returns all available providers and all regions under each provider.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(StreamingOperationsBetaApi::class.java)

launch(Dispatchers.IO) {
    val result : CloudProviderRegionResponse = webService.getCloudProvidersRegions()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**CloudProviderRegionResponse**](CloudProviderRegionResponse.md)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Gets a tenant&#39;s number of namespaces and topics, and the limits for both

For the specified tenant, returns the current number of namespaces and topics, and the limits for both.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(StreamingOperationsBetaApi::class.java)
val tenant : kotlin.String = tenant_example // kotlin.String | Tenant name

launch(Dispatchers.IO) {
    val result : kotlin.collections.List<TenantLimitResponse> = webService.getTenantLimit(tenant)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **tenant** | **kotlin.String**| Tenant name |

### Return type

[**kotlin.collections.List&lt;TenantLimitResponse&gt;**](TenantLimitResponse.md)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Gets a list of tenants from the token&#39;s auth header

Lists all Pulsar tenants under the organization that is retrieved from the token in the authorization header.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(StreamingOperationsBetaApi::class.java)

launch(Dispatchers.IO) {
    val result : kotlin.collections.List<TenantClusterPlanResponse> = webService.getTenants()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**kotlin.collections.List&lt;TenantClusterPlanResponse&gt;**](TenantClusterPlanResponse.md)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

