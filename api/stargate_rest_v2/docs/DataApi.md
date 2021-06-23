# DataApi

All URIs are relative to *https://-.apps.astra.datastax.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addRows**](DataApi.md#addRows) | **POST** api/rest/v2/keyspaces/{keyspace-id}/{table-id} | Add rows
[**deleteRows**](DataApi.md#deleteRows) | **DELETE** api/rest/v2/keyspaces/{keyspace-id}/{table-id}/{primary-key} | Delete a row by primary key
[**getRows**](DataApi.md#getRows) | **GET** api/rest/v2/keyspaces/{keyspace-id}/{table-id}/{primary-key} | Get a row
[**replaceRows**](DataApi.md#replaceRows) | **PUT** api/rest/v2/keyspaces/{keyspace-id}/{table-id}/{primary-key} | Replace a row by primary key
[**searchTable**](DataApi.md#searchTable) | **GET** api/rest/v2/keyspaces/{keyspace-id}/{table-id} | Search a table
[**updateRows**](DataApi.md#updateRows) | **PATCH** api/rest/v2/keyspaces/{keyspace-id}/{table-id}/{primary-key} | Update a row by primary key



Add rows

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_rest_v2.*
//import com.datastax.astra.stargate_rest_v2.infrastructure.*
//import com.datastax.astra.stargate_rest_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DataApi::class.java)
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val requestBody : kotlin.collections.Map<kotlin.String, kotlin.String> =  // kotlin.collections.Map<kotlin.String, kotlin.String> | 

launch(Dispatchers.IO) {
    val result : kotlin.collections.Map<kotlin.String, kotlin.String> = webService.addRows(xCassandraToken, keyspaceId, tableId, requestBody)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **keyspaceId** | **kotlin.String**| keyspace name |
 **tableId** | **kotlin.String**| table name |
 **requestBody** | [**kotlin.collections.Map&lt;kotlin.String, kotlin.String&gt;**](kotlin.String.md)|  |

### Return type

**kotlin.collections.Map&lt;kotlin.String, kotlin.String&gt;**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, */*


Delete a row by primary key

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_rest_v2.*
//import com.datastax.astra.stargate_rest_v2.infrastructure.*
//import com.datastax.astra.stargate_rest_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DataApi::class.java)
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val primaryKey : kotlin.String = primaryKey_example // kotlin.String | Value from the primary key column for the table. Define composite keys by separating values with slashes (`val1/val2...`) in the order they were defined. </br> For example, if the composite key was defined as `PRIMARY KEY(race_year, race_name)` then the primary key in the path would be `race_year/race_name` 

launch(Dispatchers.IO) {
    webService.deleteRows(xCassandraToken, keyspaceId, tableId, primaryKey)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **keyspaceId** | **kotlin.String**| keyspace name |
 **tableId** | **kotlin.String**| table name |
 **primaryKey** | **kotlin.String**| Value from the primary key column for the table. Define composite keys by separating values with slashes (&#x60;val1/val2...&#x60;) in the order they were defined. &lt;/br&gt; For example, if the composite key was defined as &#x60;PRIMARY KEY(race_year, race_name)&#x60; then the primary key in the path would be &#x60;race_year/race_name&#x60;  |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


Get a row

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_rest_v2.*
//import com.datastax.astra.stargate_rest_v2.infrastructure.*
//import com.datastax.astra.stargate_rest_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DataApi::class.java)
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val primaryKey : kotlin.String = primaryKey_example // kotlin.String | Value from the primary key column for the table. Define composite keys by separating values with slashes (`val1/val2...`) in the order they were defined. </br> For example, if the composite key was defined as `PRIMARY KEY(race_year, race_name)` then the primary key in the path would be `race_year/race_name` 
val fields : kotlin.String = fields_example // kotlin.String | URL escaped, comma delimited list of keys to include
val pageSize : kotlin.Int = 56 // kotlin.Int | restrict the number of returned items
val pageState : kotlin.String = pageState_example // kotlin.String | move the cursor to a particular result
val sort : kotlin.collections.Map<kotlin.String, kotlin.String> =  // kotlin.collections.Map<kotlin.String, kotlin.String> | keys to sort by
val raw : kotlin.Boolean = true // kotlin.Boolean | Unwrap results.

launch(Dispatchers.IO) {
    val result : InlineResponse2003 = webService.getRows(xCassandraToken, keyspaceId, tableId, primaryKey, fields, pageSize, pageState, sort, raw)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **keyspaceId** | **kotlin.String**| keyspace name |
 **tableId** | **kotlin.String**| table name |
 **primaryKey** | **kotlin.String**| Value from the primary key column for the table. Define composite keys by separating values with slashes (&#x60;val1/val2...&#x60;) in the order they were defined. &lt;/br&gt; For example, if the composite key was defined as &#x60;PRIMARY KEY(race_year, race_name)&#x60; then the primary key in the path would be &#x60;race_year/race_name&#x60;  |
 **fields** | **kotlin.String**| URL escaped, comma delimited list of keys to include | [optional]
 **pageSize** | **kotlin.Int**| restrict the number of returned items | [optional]
 **pageState** | **kotlin.String**| move the cursor to a particular result | [optional]
 **sort** | [**kotlin.collections.Map&lt;kotlin.String, kotlin.String&gt;**](kotlin.String.md)| keys to sort by | [optional] [enum: asc, desc]
 **raw** | **kotlin.Boolean**| Unwrap results. | [optional] [default to false]

### Return type

[**InlineResponse2003**](InlineResponse2003.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, */*


Replace a row by primary key

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_rest_v2.*
//import com.datastax.astra.stargate_rest_v2.infrastructure.*
//import com.datastax.astra.stargate_rest_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DataApi::class.java)
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val primaryKey : kotlin.String = primaryKey_example // kotlin.String | Value from the primary key column for the table. Define composite keys by separating values with slashes (`val1/val2...`) in the order they were defined. </br> For example, if the composite key was defined as `PRIMARY KEY(race_year, race_name)` then the primary key in the path would be `race_year/race_name` 
val requestBody : kotlin.collections.Map<kotlin.String, kotlin.String> =  // kotlin.collections.Map<kotlin.String, kotlin.String> | document
val raw : kotlin.Boolean = true // kotlin.Boolean | Unwrap results.

launch(Dispatchers.IO) {
    val result : InlineResponse2004 = webService.replaceRows(xCassandraToken, keyspaceId, tableId, primaryKey, requestBody, raw)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **keyspaceId** | **kotlin.String**| keyspace name |
 **tableId** | **kotlin.String**| table name |
 **primaryKey** | **kotlin.String**| Value from the primary key column for the table. Define composite keys by separating values with slashes (&#x60;val1/val2...&#x60;) in the order they were defined. &lt;/br&gt; For example, if the composite key was defined as &#x60;PRIMARY KEY(race_year, race_name)&#x60; then the primary key in the path would be &#x60;race_year/race_name&#x60;  |
 **requestBody** | [**kotlin.collections.Map&lt;kotlin.String, kotlin.String&gt;**](kotlin.String.md)| document |
 **raw** | **kotlin.Boolean**| Unwrap results. | [optional] [default to false]

### Return type

[**InlineResponse2004**](InlineResponse2004.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, */*


Search a table

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_rest_v2.*
//import com.datastax.astra.stargate_rest_v2.infrastructure.*
//import com.datastax.astra.stargate_rest_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DataApi::class.java)
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val where : kotlin.Any = Object // kotlin.Any | URL escaped JSON query using the following keys:  | Key | Operation | |---|---| | $lt | Less Than | | $lte | Less Than Or Equal To | | $gt | Greater Than | | $gte | Greater Than Or Equal To | | $ne | Not Equal To | | $in | Contained In | | $exists | A value is set for the key | 
val fields : kotlin.String = fields_example // kotlin.String | URL escaped, comma delimited list of keys to include
val pageSize : kotlin.Int = 56 // kotlin.Int | restrict the number of returned items
val pageState : kotlin.String = pageState_example // kotlin.String | move the cursor to a particular result
val sort : kotlin.collections.Map<kotlin.String, kotlin.String> =  // kotlin.collections.Map<kotlin.String, kotlin.String> | keys to sort by
val raw : kotlin.Boolean = true // kotlin.Boolean | Unwrap results.

launch(Dispatchers.IO) {
    val result : InlineResponse2003 = webService.searchTable(xCassandraToken, keyspaceId, tableId, where, fields, pageSize, pageState, sort, raw)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **keyspaceId** | **kotlin.String**| keyspace name |
 **tableId** | **kotlin.String**| table name |
 **where** | [**kotlin.Any**](.md)| URL escaped JSON query using the following keys:  | Key | Operation | |---|---| | $lt | Less Than | | $lte | Less Than Or Equal To | | $gt | Greater Than | | $gte | Greater Than Or Equal To | | $ne | Not Equal To | | $in | Contained In | | $exists | A value is set for the key |  | [optional]
 **fields** | **kotlin.String**| URL escaped, comma delimited list of keys to include | [optional]
 **pageSize** | **kotlin.Int**| restrict the number of returned items | [optional]
 **pageState** | **kotlin.String**| move the cursor to a particular result | [optional]
 **sort** | [**kotlin.collections.Map&lt;kotlin.String, kotlin.String&gt;**](kotlin.String.md)| keys to sort by | [optional] [enum: asc, desc]
 **raw** | **kotlin.Boolean**| Unwrap results. | [optional] [default to false]

### Return type

[**InlineResponse2003**](InlineResponse2003.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, */*


Update a row by primary key

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_rest_v2.*
//import com.datastax.astra.stargate_rest_v2.infrastructure.*
//import com.datastax.astra.stargate_rest_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DataApi::class.java)
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val keyspaceId : kotlin.String = keyspaceId_example // kotlin.String | keyspace name
val tableId : kotlin.String = tableId_example // kotlin.String | table name
val primaryKey : kotlin.String = primaryKey_example // kotlin.String | Value from the primary key column for the table. Define composite keys by separating values with slashes (`val1/val2...`) in the order they were defined. </br> For example, if the composite key was defined as `PRIMARY KEY(race_year, race_name)` then the primary key in the path would be `race_year/race_name` 
val requestBody : kotlin.collections.Map<kotlin.String, kotlin.String> =  // kotlin.collections.Map<kotlin.String, kotlin.String> | document
val raw : kotlin.Boolean = true // kotlin.Boolean | Unwrap results.

launch(Dispatchers.IO) {
    val result : InlineResponse2004 = webService.updateRows(xCassandraToken, keyspaceId, tableId, primaryKey, requestBody, raw)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **keyspaceId** | **kotlin.String**| keyspace name |
 **tableId** | **kotlin.String**| table name |
 **primaryKey** | **kotlin.String**| Value from the primary key column for the table. Define composite keys by separating values with slashes (&#x60;val1/val2...&#x60;) in the order they were defined. &lt;/br&gt; For example, if the composite key was defined as &#x60;PRIMARY KEY(race_year, race_name)&#x60; then the primary key in the path would be &#x60;race_year/race_name&#x60;  |
 **requestBody** | [**kotlin.collections.Map&lt;kotlin.String, kotlin.String&gt;**](kotlin.String.md)| document |
 **raw** | **kotlin.Boolean**| Unwrap results. | [optional] [default to false]

### Return type

[**InlineResponse2004**](InlineResponse2004.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, */*

