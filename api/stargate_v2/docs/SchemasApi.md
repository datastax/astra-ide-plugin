# SchemasApi

All URIs are relative to *https://-.apps.astra.datastax.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createColumn**](SchemasApi.md#createColumn) | **POST** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns | Create a column
[**createTable**](SchemasApi.md#createTable) | **POST** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables | Create a table
[**deleteColumn**](SchemasApi.md#deleteColumn) | **DELETE** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns/{column-id} | Delete a column
[**deleteTable**](SchemasApi.md#deleteTable) | **DELETE** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id} | Delete a table
[**getColumn**](SchemasApi.md#getColumn) | **GET** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns/{column-id} | Get a column
[**getColumns**](SchemasApi.md#getColumns) | **GET** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns | List columns
[**getKeyspace**](SchemasApi.md#getKeyspace) | **GET** api/rest/v2/schemas/keyspaces/{keyspace-id} | Get a keyspace using the {keyspace-id}
[**getKeyspaces**](SchemasApi.md#getKeyspaces) | **GET** api/rest/v2/schemas/keyspaces | Get all keyspaces
[**getTable**](SchemasApi.md#getTable) | **GET** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id} | Get a table
[**getTables**](SchemasApi.md#getTables) | **GET** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables | Get all tables
[**replaceColumn**](SchemasApi.md#replaceColumn) | **PUT** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns/{column-id} | Replace a column definition
[**replaceTable**](SchemasApi.md#replaceTable) | **PUT** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id} | Replace a table definition, except for columns



Create a column

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.*
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(SchemasApi::class.java)
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val columnDefinition : ColumnDefinition =  // ColumnDefinition | 

launch(Dispatchers.IO) {
    val result : InlineResponse201 = webService.createColumn(xCassandraToken, keyspaceId, tableId, columnDefinition)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **keyspaceId** | **kotlin.String**| keyspace name |
 **tableId** | **kotlin.String**| table name |
 **columnDefinition** | [**ColumnDefinition**](ColumnDefinition.md)|  |

### Return type

[**InlineResponse201**](InlineResponse201.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, */*


Create a table

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.*
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(SchemasApi::class.java)
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableAdd : TableAdd =  // TableAdd | 

launch(Dispatchers.IO) {
    val result : InlineResponse201 = webService.createTable(xCassandraToken, keyspaceId, tableAdd)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **keyspaceId** | **kotlin.String**| keyspace name |
 **tableAdd** | [**TableAdd**](TableAdd.md)|  |

### Return type

[**InlineResponse201**](InlineResponse201.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, */*


Delete a column

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.*
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(SchemasApi::class.java)
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val columnId : kotlin.String = columnId_example // kotlin.String | column name

launch(Dispatchers.IO) {
    webService.deleteColumn(xCassandraToken, keyspaceId, tableId, columnId)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **keyspaceId** | **kotlin.String**| keyspace name |
 **tableId** | **kotlin.String**| table name |
 **columnId** | **kotlin.String**| column name |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


Delete a table

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.*
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(SchemasApi::class.java)
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name

launch(Dispatchers.IO) {
    webService.deleteTable(xCassandraToken, keyspaceId, tableId)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **keyspaceId** | **kotlin.String**| keyspace name |
 **tableId** | **kotlin.String**| table name |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


Get a column

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.*
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(SchemasApi::class.java)
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val columnId : kotlin.String = columnId_example // kotlin.String | column name
val raw : kotlin.Boolean = true // kotlin.Boolean | unwrap results

launch(Dispatchers.IO) {
    val result : ColumnDefinition = webService.getColumn(xCassandraToken, keyspaceId, tableId, columnId, raw)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **keyspaceId** | **kotlin.String**| keyspace name |
 **tableId** | **kotlin.String**| table name |
 **columnId** | **kotlin.String**| column name |
 **raw** | **kotlin.Boolean**| unwrap results | [optional] [default to false]

### Return type

[**ColumnDefinition**](ColumnDefinition.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, */*


List columns

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.*
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(SchemasApi::class.java)
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val raw : kotlin.Boolean = true // kotlin.Boolean | unwrap results

launch(Dispatchers.IO) {
    val result : InlineResponse2002 = webService.getColumns(xCassandraToken, keyspaceId, tableId, raw)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **keyspaceId** | **kotlin.String**| keyspace name |
 **tableId** | **kotlin.String**| table name |
 **raw** | **kotlin.Boolean**| unwrap results | [optional] [default to false]

### Return type

[**InlineResponse2002**](InlineResponse2002.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, */*


Get a keyspace using the {keyspace-id}

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.*
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(SchemasApi::class.java)
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val raw : kotlin.Boolean = true // kotlin.Boolean | unwrap results

launch(Dispatchers.IO) {
    val result : Keyspace = webService.getKeyspace(xCassandraToken, keyspaceId, raw)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **keyspaceId** | **kotlin.String**| keyspace name |
 **raw** | **kotlin.Boolean**| unwrap results | [optional] [default to false]

### Return type

[**Keyspace**](Keyspace.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, */*


Get all keyspaces

Retrieve all available keyspaces in the specific database.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.*
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(SchemasApi::class.java)
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val raw : kotlin.Boolean = true // kotlin.Boolean | unwrap results

launch(Dispatchers.IO) {
    val result : InlineResponse200 = webService.getKeyspaces(xCassandraToken, raw)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **raw** | **kotlin.Boolean**| unwrap results | [optional] [default to false]

### Return type

[**InlineResponse200**](InlineResponse200.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, */*


Get a table

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.*
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(SchemasApi::class.java)
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val raw : kotlin.Boolean = true // kotlin.Boolean | unwrap results

launch(Dispatchers.IO) {
    val result : Table = webService.getTable(xCassandraToken, keyspaceId, tableId, raw)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **keyspaceId** | **kotlin.String**| keyspace name |
 **tableId** | **kotlin.String**| table name |
 **raw** | **kotlin.Boolean**| unwrap results | [optional] [default to false]

### Return type

[**Table**](Table.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, */*


Get all tables

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.*
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(SchemasApi::class.java)
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val raw : kotlin.Boolean = true // kotlin.Boolean | unwrap results

launch(Dispatchers.IO) {
    val result : InlineResponse2001 = webService.getTables(xCassandraToken, keyspaceId, raw)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **keyspaceId** | **kotlin.String**| keyspace name |
 **raw** | **kotlin.Boolean**| unwrap results | [optional] [default to false]

### Return type

[**InlineResponse2001**](InlineResponse2001.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, */*


Replace a column definition

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.*
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(SchemasApi::class.java)
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val columnId : kotlin.String = columnId_example // kotlin.String | column name
val columnDefinition : ColumnDefinition =  // ColumnDefinition | 

launch(Dispatchers.IO) {
    val result : InlineResponse201 = webService.replaceColumn(xCassandraToken, keyspaceId, tableId, columnId, columnDefinition)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **keyspaceId** | **kotlin.String**| keyspace name |
 **tableId** | **kotlin.String**| table name |
 **columnId** | **kotlin.String**| column name |
 **columnDefinition** | [**ColumnDefinition**](ColumnDefinition.md)|  |

### Return type

[**InlineResponse201**](InlineResponse201.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, */*


Replace a table definition, except for columns

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.*
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(SchemasApi::class.java)
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val tableAdd : TableAdd =  // TableAdd | 

launch(Dispatchers.IO) {
    val result : InlineResponse201 = webService.replaceTable(xCassandraToken, keyspaceId, tableId, tableAdd)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **keyspaceId** | **kotlin.String**| keyspace name |
 **tableId** | **kotlin.String**| table name |
 **tableAdd** | [**TableAdd**](TableAdd.md)|  |

### Return type

[**InlineResponse201**](InlineResponse201.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, */*

