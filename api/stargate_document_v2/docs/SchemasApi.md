# SchemasApi

All URIs are relative to *https://d341f349-e5db-46d2-9c90-bb9ebaa6f0fc-us-east-1.apps.astra.datastax.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getAllNamespaces**](SchemasApi.md#getAllNamespaces) | **GET** api/rest/v2/schemas/namespaces | Get all namespaces
[**getNamespace**](SchemasApi.md#getNamespace) | **GET** api/rest/v2/schemas/namespaces/{namespace-id} | Get a namespace



Get all namespaces

Retrieve all available namespaces.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_document_v2.*
//import com.datastax.astra.stargate_document_v2.infrastructure.*
//import com.datastax.astra.stargate_document_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(SchemasApi::class.java)
val xCassandraRequestId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | Unique identifier (UUID) for the request. Use any valid UUID.
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val pretty : kotlin.Boolean = true // kotlin.Boolean | format results
val raw : kotlin.Boolean = true // kotlin.Boolean | unwrap results

launch(Dispatchers.IO) {
    webService.getAllNamespaces(xCassandraRequestId, xCassandraToken, pretty, raw)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraRequestId** | [**java.util.UUID**](.md)| Unique identifier (UUID) for the request. Use any valid UUID. |
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **pretty** | **kotlin.Boolean**| format results | [optional]
 **raw** | **kotlin.Boolean**| unwrap results | [optional]

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Get a namespace

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_document_v2.*
//import com.datastax.astra.stargate_document_v2.infrastructure.*
//import com.datastax.astra.stargate_document_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(SchemasApi::class.java)
val xCassandraRequestId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | Unique identifier (UUID) for the request. Use any valid UUID.
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val namespaceId : kotlin.String = namespaceId_example // kotlin.String | namespace name
val pretty : kotlin.Boolean = true // kotlin.Boolean | format results
val raw : kotlin.Boolean = true // kotlin.Boolean | unwrap results

launch(Dispatchers.IO) {
    webService.getNamespace(xCassandraRequestId, xCassandraToken, namespaceId, pretty, raw)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraRequestId** | [**java.util.UUID**](.md)| Unique identifier (UUID) for the request. Use any valid UUID. |
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **namespaceId** | **kotlin.String**| namespace name |
 **pretty** | **kotlin.Boolean**| format results | [optional]
 **raw** | **kotlin.Boolean**| unwrap results | [optional]

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

