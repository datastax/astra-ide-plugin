# OperationsApi

All URIs are relative to *https://api.astra.datastax.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addKeyspace**](OperationsApi.md#addKeyspace) | **POST** v2/databases/{databaseID}/keyspaces/{keyspaceName} | Adds keyspace into database
[**createDatabase**](OperationsApi.md#createDatabase) | **POST** v2/databases | Create a new database
[**generateSecureBundleURL**](OperationsApi.md#generateSecureBundleURL) | **POST** v2/databases/{databaseID}/secureBundleURL | Obtain zip for connecting to the database
[**getDatabase**](OperationsApi.md#getDatabase) | **GET** v2/databases/{databaseID} | Finds database by ID
[**listAvailableRegions**](OperationsApi.md#listAvailableRegions) | **GET** v2/availableRegions | Returns supported regions and availability for a given user and organization
[**listDatabases**](OperationsApi.md#listDatabases) | **GET** v2/databases | Returns a list of databases
[**parkDatabase**](OperationsApi.md#parkDatabase) | **POST** v2/databases/{databaseID}/park | Parks a database
[**resetPassword**](OperationsApi.md#resetPassword) | **POST** v2/databases/{databaseID}/resetPassword | Resets Password
[**resizeDatabase**](OperationsApi.md#resizeDatabase) | **POST** v2/databases/{databaseID}/resize | Resizes a database
[**terminateDatabase**](OperationsApi.md#terminateDatabase) | **POST** v2/databases/{databaseID}/terminate | Terminates a database
[**unparkDatabase**](OperationsApi.md#unparkDatabase) | **POST** v2/databases/{databaseID}/unpark | Unparks a database



Adds keyspace into database

Adds the specified keyspace to the database

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(OperationsApi::class.java)
val databaseID : kotlin.String = databaseID_example // kotlin.String | String representation of the database ID
val keyspaceName : kotlin.String = keyspaceName_example // kotlin.String | Name of database keyspace

launch(Dispatchers.IO) {
    webService.addKeyspace(databaseID, keyspaceName)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **databaseID** | **kotlin.String**| String representation of the database ID |
 **keyspaceName** | **kotlin.String**| Name of database keyspace |

### Return type

null (empty response body)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Create a new database

Takes a user provided databaseInfo and returns the uuid for a new database

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(OperationsApi::class.java)
val databaseInfoCreate : DatabaseInfoCreate =  // DatabaseInfoCreate | Definition of new database

launch(Dispatchers.IO) {
    webService.createDatabase(databaseInfoCreate)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **databaseInfoCreate** | [**DatabaseInfoCreate**](DatabaseInfoCreate.md)| Definition of new database |

### Return type

null (empty response body)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Obtain zip for connecting to the database

Returns a temporary URL to download a zip file with certificates for connecting to the database. The URL expires after five minutes.&lt;p&gt;There are two types of the secure bundle URL: &lt;ul&gt;&lt;li&gt;&lt;b&gt;Internal&lt;/b&gt; - Use with VPC peering connections to use private networking and avoid public internet for communication.&lt;/li&gt; &lt;li&gt;&lt;b&gt;External&lt;/b&gt; - Use with any connection where the public internet is sufficient for communication between the application and the Astra database with MTLS.&lt;/li&gt;&lt;/ul&gt; Both types support MTLS for communication via the driver.&lt;/p&gt;

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(OperationsApi::class.java)
val databaseID : kotlin.String = databaseID_example // kotlin.String | String representation of the database ID

launch(Dispatchers.IO) {
    val result : CredsURL = webService.generateSecureBundleURL(databaseID)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **databaseID** | **kotlin.String**| String representation of the database ID |

### Return type

[**CredsURL**](CredsURL.md)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Finds database by ID

Returns specified database

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(OperationsApi::class.java)
val databaseID : kotlin.String = databaseID_example // kotlin.String | String representation of the database ID

launch(Dispatchers.IO) {
    val result : Database = webService.getDatabase(databaseID)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **databaseID** | **kotlin.String**| String representation of the database ID |

### Return type

[**Database**](Database.md)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Returns supported regions and availability for a given user and organization

Returns all supported tier, cloud, region, count, and capacitity combinations

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(OperationsApi::class.java)

launch(Dispatchers.IO) {
    val result : kotlin.collections.List<AvailableRegionCombination> = webService.listAvailableRegions()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**kotlin.collections.List&lt;AvailableRegionCombination&gt;**](AvailableRegionCombination.md)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Returns a list of databases

Get a list of databases visible to the user

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(OperationsApi::class.java)
val include : kotlin.String = include_example // kotlin.String | Allows filtering so that databases in listed states are returned
val provider : kotlin.String = provider_example // kotlin.String | Allows filtering so that databases from a given provider are returned
val startingAfter : kotlin.String = startingAfter_example // kotlin.String | Optional parameter for pagination purposes. Used as this value for starting retrieving a specific page of results
val limit : kotlin.Int = 56 // kotlin.Int | Optional parameter for pagination purposes. Specify the number of items for one page of data

launch(Dispatchers.IO) {
    val result : kotlin.collections.List<Database> = webService.listDatabases(include, provider, startingAfter, limit)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **include** | **kotlin.String**| Allows filtering so that databases in listed states are returned | [optional] [default to nonterminated] [enum: nonterminated, all, active, pending, preparing, prepared, initializing, parked, parking, unparking, terminating, terminated, resizing, error, maintenance]
 **provider** | **kotlin.String**| Allows filtering so that databases from a given provider are returned | [optional] [default to ALL] [enum: ALL, GCP, AWS, AZURE]
 **startingAfter** | **kotlin.String**| Optional parameter for pagination purposes. Used as this value for starting retrieving a specific page of results | [optional]
 **limit** | **kotlin.Int**| Optional parameter for pagination purposes. Specify the number of items for one page of data | [optional] [default to 25]

### Return type

[**kotlin.collections.List&lt;Database&gt;**](Database.md)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Parks a database

Parks a database

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(OperationsApi::class.java)
val databaseID : kotlin.String = databaseID_example // kotlin.String | String representation of the database ID

launch(Dispatchers.IO) {
    webService.parkDatabase(databaseID)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **databaseID** | **kotlin.String**| String representation of the database ID |

### Return type

null (empty response body)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Resets Password

Sets a database password to the one specified in POST body

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(OperationsApi::class.java)
val databaseID : kotlin.String = databaseID_example // kotlin.String | String representation of the database ID
val userPassword : UserPassword =  // UserPassword | Map containing username and password. The specified password will be updated for the specified database user

launch(Dispatchers.IO) {
    webService.resetPassword(databaseID, userPassword)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **databaseID** | **kotlin.String**| String representation of the database ID |
 **userPassword** | [**UserPassword**](UserPassword.md)| Map containing username and password. The specified password will be updated for the specified database user |

### Return type

null (empty response body)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Resizes a database

Resizes a database. Total number of capacity units desired should be specified. Reducing a size of a database is not supported at this time.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(OperationsApi::class.java)
val databaseID : kotlin.String = databaseID_example // kotlin.String | String representation of the database ID
val capacityUnits : CapacityUnits =  // CapacityUnits | Map containing capacityUnits key with a value greater than the current number of capacity units (max increment of 3 additional capacity units)

launch(Dispatchers.IO) {
    webService.resizeDatabase(databaseID, capacityUnits)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **databaseID** | **kotlin.String**| String representation of the database ID |
 **capacityUnits** | [**CapacityUnits**](CapacityUnits.md)| Map containing capacityUnits key with a value greater than the current number of capacity units (max increment of 3 additional capacity units) |

### Return type

null (empty response body)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Terminates a database

Terminates a database

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(OperationsApi::class.java)
val databaseID : kotlin.String = databaseID_example // kotlin.String | String representation of the database ID
val preparedStateOnly : kotlin.Boolean = true // kotlin.Boolean | For internal use only.  Used to safely terminate prepared databases.

launch(Dispatchers.IO) {
    webService.terminateDatabase(databaseID, preparedStateOnly)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **databaseID** | **kotlin.String**| String representation of the database ID |
 **preparedStateOnly** | **kotlin.Boolean**| For internal use only.  Used to safely terminate prepared databases. | [optional] [default to false]

### Return type

null (empty response body)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Unparks a database

Unparks a database

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(OperationsApi::class.java)
val databaseID : kotlin.String = databaseID_example // kotlin.String | String representation of the database ID

launch(Dispatchers.IO) {
    webService.unparkDatabase(databaseID)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **databaseID** | **kotlin.String**| String representation of the database ID |

### Return type

null (empty response body)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

