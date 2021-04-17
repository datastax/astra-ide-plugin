# SchemasApi

All URIs are relative to *https://-.apps.astra.datastax.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createColumn**](SchemasApi.md#createColumn) | **POST** /api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns | Create a column
[**createTable**](SchemasApi.md#createTable) | **POST** /api/rest/v2/schemas/keyspaces/{keyspace-id}/tables | Create a table
[**deleteColumn**](SchemasApi.md#deleteColumn) | **DELETE** /api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns/{column-id} | Delete a column
[**deleteTable**](SchemasApi.md#deleteTable) | **DELETE** /api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id} | Delete a table
[**getColumn**](SchemasApi.md#getColumn) | **GET** /api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns/{column-id} | Get a column
[**getColumns**](SchemasApi.md#getColumns) | **GET** /api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns | List columns
[**getKeyspace**](SchemasApi.md#getKeyspace) | **GET** /api/rest/v2/schemas/keyspaces/{keyspace-id} | Get a keyspace using the {keyspace-id}
[**getKeyspaces**](SchemasApi.md#getKeyspaces) | **GET** /api/rest/v2/schemas/keyspaces | Get all keyspaces
[**getTable**](SchemasApi.md#getTable) | **GET** /api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id} | Get a table
[**getTables**](SchemasApi.md#getTables) | **GET** /api/rest/v2/schemas/keyspaces/{keyspace-id}/tables | Get all tables
[**replaceColumn**](SchemasApi.md#replaceColumn) | **PUT** /api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns/{column-id} | Replace a column definition
[**replaceTable**](SchemasApi.md#replaceTable) | **PUT** /api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id} | Replace a table definition, except for columns


<a name="createColumn"></a>
# **createColumn**
> InlineResponse201 createColumn(xCassandraToken, keyspaceId, tableId, columnDefinition)

Create a column

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiInstance = SchemasApi()
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val columnDefinition : ColumnDefinition =  // ColumnDefinition | 
try {
    val result : InlineResponse201 = apiInstance.createColumn(xCassandraToken, keyspaceId, tableId, columnDefinition)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SchemasApi#createColumn")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SchemasApi#createColumn")
    e.printStackTrace()
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

<a name="createTable"></a>
# **createTable**
> InlineResponse201 createTable(xCassandraToken, keyspaceId, tableAdd)

Create a table

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiInstance = SchemasApi()
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableAdd : TableAdd =  // TableAdd | 
try {
    val result : InlineResponse201 = apiInstance.createTable(xCassandraToken, keyspaceId, tableAdd)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SchemasApi#createTable")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SchemasApi#createTable")
    e.printStackTrace()
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

<a name="deleteColumn"></a>
# **deleteColumn**
> deleteColumn(xCassandraToken, keyspaceId, tableId, columnId)

Delete a column

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiInstance = SchemasApi()
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val columnId : kotlin.String = columnId_example // kotlin.String | column name
try {
    apiInstance.deleteColumn(xCassandraToken, keyspaceId, tableId, columnId)
} catch (e: ClientException) {
    println("4xx response calling SchemasApi#deleteColumn")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SchemasApi#deleteColumn")
    e.printStackTrace()
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

<a name="deleteTable"></a>
# **deleteTable**
> deleteTable(xCassandraToken, keyspaceId, tableId)

Delete a table

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiInstance = SchemasApi()
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
try {
    apiInstance.deleteTable(xCassandraToken, keyspaceId, tableId)
} catch (e: ClientException) {
    println("4xx response calling SchemasApi#deleteTable")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SchemasApi#deleteTable")
    e.printStackTrace()
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

<a name="getColumn"></a>
# **getColumn**
> ColumnDefinition getColumn(xCassandraToken, keyspaceId, tableId, columnId, raw)

Get a column

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiInstance = SchemasApi()
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val columnId : kotlin.String = columnId_example // kotlin.String | column name
val raw : kotlin.Boolean = true // kotlin.Boolean | unwrap results
try {
    val result : ColumnDefinition = apiInstance.getColumn(xCassandraToken, keyspaceId, tableId, columnId, raw)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SchemasApi#getColumn")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SchemasApi#getColumn")
    e.printStackTrace()
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

<a name="getColumns"></a>
# **getColumns**
> InlineResponse2002 getColumns(xCassandraToken, keyspaceId, tableId, raw)

List columns

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiInstance = SchemasApi()
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val raw : kotlin.Boolean = true // kotlin.Boolean | unwrap results
try {
    val result : InlineResponse2002 = apiInstance.getColumns(xCassandraToken, keyspaceId, tableId, raw)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SchemasApi#getColumns")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SchemasApi#getColumns")
    e.printStackTrace()
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

<a name="getKeyspace"></a>
# **getKeyspace**
> Keyspace getKeyspace(xCassandraToken, keyspaceId, raw)

Get a keyspace using the {keyspace-id}

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiInstance = SchemasApi()
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val raw : kotlin.Boolean = true // kotlin.Boolean | unwrap results
try {
    val result : Keyspace = apiInstance.getKeyspace(xCassandraToken, keyspaceId, raw)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SchemasApi#getKeyspace")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SchemasApi#getKeyspace")
    e.printStackTrace()
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

<a name="getKeyspaces"></a>
# **getKeyspaces**
> InlineResponse200 getKeyspaces(xCassandraToken, raw)

Get all keyspaces

Retrieve all available keyspaces in the specific database.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiInstance = SchemasApi()
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val raw : kotlin.Boolean = true // kotlin.Boolean | unwrap results
try {
    val result : InlineResponse200 = apiInstance.getKeyspaces(xCassandraToken, raw)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SchemasApi#getKeyspaces")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SchemasApi#getKeyspaces")
    e.printStackTrace()
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

<a name="getTable"></a>
# **getTable**
> Table getTable(xCassandraToken, keyspaceId, tableId, raw)

Get a table

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiInstance = SchemasApi()
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val raw : kotlin.Boolean = true // kotlin.Boolean | unwrap results
try {
    val result : Table = apiInstance.getTable(xCassandraToken, keyspaceId, tableId, raw)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SchemasApi#getTable")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SchemasApi#getTable")
    e.printStackTrace()
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

<a name="getTables"></a>
# **getTables**
> InlineResponse2001 getTables(xCassandraToken, keyspaceId, raw)

Get all tables

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiInstance = SchemasApi()
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val raw : kotlin.Boolean = true // kotlin.Boolean | unwrap results
try {
    val result : InlineResponse2001 = apiInstance.getTables(xCassandraToken, keyspaceId, raw)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SchemasApi#getTables")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SchemasApi#getTables")
    e.printStackTrace()
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

<a name="replaceColumn"></a>
# **replaceColumn**
> InlineResponse201 replaceColumn(xCassandraToken, keyspaceId, tableId, columnId, columnDefinition)

Replace a column definition

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiInstance = SchemasApi()
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val columnId : kotlin.String = columnId_example // kotlin.String | column name
val columnDefinition : ColumnDefinition =  // ColumnDefinition | 
try {
    val result : InlineResponse201 = apiInstance.replaceColumn(xCassandraToken, keyspaceId, tableId, columnId, columnDefinition)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SchemasApi#replaceColumn")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SchemasApi#replaceColumn")
    e.printStackTrace()
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

<a name="replaceTable"></a>
# **replaceTable**
> InlineResponse201 replaceTable(xCassandraToken, keyspaceId, tableId, tableAdd)

Replace a table definition, except for columns

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiInstance = SchemasApi()
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val tableAdd : TableAdd =  // TableAdd | 
try {
    val result : InlineResponse201 = apiInstance.replaceTable(xCassandraToken, keyspaceId, tableId, tableAdd)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SchemasApi#replaceTable")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SchemasApi#replaceTable")
    e.printStackTrace()
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

