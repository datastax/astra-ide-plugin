# DBOperationsApi

All URIs are relative to *https://api.astra.datastax.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addKeyspace**](DBOperationsApi.md#addKeyspace) | **POST** v2/databases/{databaseID}/keyspaces/{keyspaceName} | Adds keyspace into database
[**addOrganizationRole**](DBOperationsApi.md#addOrganizationRole) | **POST** v2/organizations/roles | Create a role in an organization
[**createDatabase**](DBOperationsApi.md#createDatabase) | **POST** v2/databases | Create a new database
[**deleteOrganizationRole**](DBOperationsApi.md#deleteOrganizationRole) | **DELETE** v2/organizations/roles/{roleID} | Delete a role by ID
[**deleteTokenForClient**](DBOperationsApi.md#deleteTokenForClient) | **DELETE** v2/clientIdSecret/{clientId} | Revokes a token
[**generateSecureBundleURL**](DBOperationsApi.md#generateSecureBundleURL) | **POST** v2/databases/{databaseID}/secureBundleURL | Obtain zip for connecting to the database
[**generateTokenForClient**](DBOperationsApi.md#generateTokenForClient) | **POST** v2/clientIdSecrets | Generate token for client
[**getClientsForOrg**](DBOperationsApi.md#getClientsForOrg) | **GET** v2/clientIdSecrets | Get a list of clients for an org
[**getCurrentOrganization**](DBOperationsApi.md#getCurrentOrganization) | **GET** v2/currentOrg | Get the current organization from the passed token
[**getDatabase**](DBOperationsApi.md#getDatabase) | **GET** v2/databases/{databaseID} | Finds database by ID
[**getOrganizationRole**](DBOperationsApi.md#getOrganizationRole) | **GET** v2/organizations/roles/{roleID} | Get a role for an organization
[**getOrganizationRoles**](DBOperationsApi.md#getOrganizationRoles) | **GET** v2/organizations/roles | Get all roles for an organization
[**getOrganizationUser**](DBOperationsApi.md#getOrganizationUser) | **GET** v2/organizations/users/{userID} | Get an organization&#39;s user
[**getOrganizationUsers**](DBOperationsApi.md#getOrganizationUsers) | **GET** v2/organizations/users | Get an organization&#39;s users
[**inviteUserToOrganization**](DBOperationsApi.md#inviteUserToOrganization) | **PUT** v2/organizations/users | Invite a user to an organization
[**listAvailableRegions**](DBOperationsApi.md#listAvailableRegions) | **GET** v2/availableRegions | Returns supported regions and availability for a given user and organization
[**listDatabases**](DBOperationsApi.md#listDatabases) | **GET** v2/databases | Returns a list of databases
[**parkDatabase**](DBOperationsApi.md#parkDatabase) | **POST** v2/databases/{databaseID}/park | Parks a database
[**removeUserFromOrganization**](DBOperationsApi.md#removeUserFromOrganization) | **DELETE** v2/organizations/users/{userID} | Remove or uninvite a user from an organization
[**resetPassword**](DBOperationsApi.md#resetPassword) | **POST** v2/databases/{databaseID}/resetPassword | Resets Password
[**resizeDatabase**](DBOperationsApi.md#resizeDatabase) | **POST** v2/databases/{databaseID}/resize | Resizes a database
[**terminateDatabase**](DBOperationsApi.md#terminateDatabase) | **POST** v2/databases/{databaseID}/terminate | Terminates a database
[**unparkDatabase**](DBOperationsApi.md#unparkDatabase) | **POST** v2/databases/{databaseID}/unpark | Unparks a database
[**updateRole**](DBOperationsApi.md#updateRole) | **PUT** v2/organizations/roles/{roleID} | Update a role within an organization
[**updateRolesForUserInOrganization**](DBOperationsApi.md#updateRolesForUserInOrganization) | **PUT** v2/organizations/users/{userID}/roles | Update organization roles for a user



Adds keyspace into database

Adds the specified keyspace to the database.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
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


Create a role in an organization

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
val createRoleRequest : CreateRoleRequest =  // CreateRoleRequest | The model for create role body.

launch(Dispatchers.IO) {
    webService.addOrganizationRole(createRoleRequest)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **createRoleRequest** | [**CreateRoleRequest**](CreateRoleRequest.md)| The model for create role body. | [optional]

### Return type

null (empty response body)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Create a new database

Takes a user provided databaseInfo and returns the uuid for a new database.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
val databaseInfoCreate : DatabaseInfoCreate =  // DatabaseInfoCreate | Definition of new database.

launch(Dispatchers.IO) {
    webService.createDatabase(databaseInfoCreate)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **databaseInfoCreate** | [**DatabaseInfoCreate**](DatabaseInfoCreate.md)| Definition of new database. |

### Return type

null (empty response body)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Delete a role by ID

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
val roleID : kotlin.String = roleID_example // kotlin.String | id for the role

launch(Dispatchers.IO) {
    webService.deleteOrganizationRole(roleID)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **roleID** | **kotlin.String**| id for the role |

### Return type

null (empty response body)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Revokes a token

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
val clientId : kotlin.String = clientId_example // kotlin.String | clientID to revoke token for

launch(Dispatchers.IO) {
    webService.deleteTokenForClient(clientId)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clientId** | **kotlin.String**| clientID to revoke token for |

### Return type

null (empty response body)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
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
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
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


Generate token for client

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
val generateTokenBody : GenerateTokenBody =  // GenerateTokenBody | The model for generating token for client.

launch(Dispatchers.IO) {
    val result : kotlin.collections.List<GenerateTokenResponse> = webService.generateTokenForClient(generateTokenBody)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **generateTokenBody** | [**GenerateTokenBody**](GenerateTokenBody.md)| The model for generating token for client. | [optional]

### Return type

[**kotlin.collections.List&lt;GenerateTokenResponse&gt;**](GenerateTokenResponse.md)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Get a list of clients for an org

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)

launch(Dispatchers.IO) {
    val result : kotlin.collections.List<ClientRoleList> = webService.getClientsForOrg()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**kotlin.collections.List&lt;ClientRoleList&gt;**](ClientRoleList.md)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Get the current organization from the passed token

Retrieve the details for the organization in the provided token.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)

launch(Dispatchers.IO) {
    val result : kotlin.collections.List<Organization> = webService.getCurrentOrganization()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**kotlin.collections.List&lt;Organization&gt;**](Organization.md)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Finds database by ID

Returns specified database.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
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


Get a role for an organization

Retrieve the details for a role for a given organization.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
val roleID : kotlin.String = roleID_example // kotlin.String | id for the role

launch(Dispatchers.IO) {
    val result : kotlin.collections.List<Role> = webService.getOrganizationRole(roleID)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **roleID** | **kotlin.String**| id for the role |

### Return type

[**kotlin.collections.List&lt;Role&gt;**](Role.md)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Get all roles for an organization

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)

launch(Dispatchers.IO) {
    val result : kotlin.collections.List<kotlin.collections.List<Role>> = webService.getOrganizationRoles()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

**kotlin.collections.List&lt;kotlin.collections.List&lt;Role&gt;&gt;**

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Get an organization&#39;s user

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
val userID : kotlin.String = userID_example // kotlin.String | id for the user

launch(Dispatchers.IO) {
    val result : UserResponse = webService.getOrganizationUser(userID)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userID** | **kotlin.String**| id for the user |

### Return type

[**UserResponse**](UserResponse.md)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Get an organization&#39;s users

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)

launch(Dispatchers.IO) {
    val result : GetOrganizationUsersResponse = webService.getOrganizationUsers()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**GetOrganizationUsersResponse**](GetOrganizationUsersResponse.md)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Invite a user to an organization

Invite a user to an organization or resend an invitation with new invitation details, such as an updated expiration

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
val userInvite : UserInvite =  // UserInvite | 

launch(Dispatchers.IO) {
    webService.inviteUserToOrganization(userInvite)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userInvite** | [**UserInvite**](UserInvite.md)|  | [optional]

### Return type

null (empty response body)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Returns supported regions and availability for a given user and organization

Returns all supported tier, cloud, region, count, and capacitity combinations.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)

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

Get a list of databases visible to the user.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
val include : kotlin.String = include_example // kotlin.String | Allows filtering so that databases in listed states are returned.
val provider : kotlin.String = provider_example // kotlin.String | Allows filtering so that databases from a given provider are returned.
val startingAfter : kotlin.String = startingAfter_example // kotlin.String | Optional parameter for pagination purposes. Used as this value for starting retrieving a specific page of results.
val limit : kotlin.Int = 56 // kotlin.Int | Optional parameter for pagination purposes. Specify the number of items for one page of data.

launch(Dispatchers.IO) {
    val result : kotlin.collections.List<Database> = webService.listDatabases(include, provider, startingAfter, limit)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **include** | **kotlin.String**| Allows filtering so that databases in listed states are returned. | [optional] [default to nonterminated] [enum: nonterminated, all, active, pending, preparing, prepared, initializing, parked, parking, unparking, terminating, terminated, resizing, error, maintenance]
 **provider** | **kotlin.String**| Allows filtering so that databases from a given provider are returned. | [optional] [default to ALL] [enum: ALL, GCP, AWS, AZURE]
 **startingAfter** | **kotlin.String**| Optional parameter for pagination purposes. Used as this value for starting retrieving a specific page of results. | [optional]
 **limit** | **kotlin.Int**| Optional parameter for pagination purposes. Specify the number of items for one page of data. | [optional] [default to 25]

### Return type

[**kotlin.collections.List&lt;Database&gt;**](Database.md)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Parks a database

Parks a database.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
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


Remove or uninvite a user from an organization

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
val userID : kotlin.String = userID_example // kotlin.String | id for the user

launch(Dispatchers.IO) {
    webService.removeUserFromOrganization(userID)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userID** | **kotlin.String**| id for the user |

### Return type

null (empty response body)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Resets Password

Sets a database password to the one specified in POST body.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
val databaseID : kotlin.String = databaseID_example // kotlin.String | String representation of the database ID
val userPassword : UserPassword =  // UserPassword | Map containing username and password. The specified password will be updated for the specified database user.

launch(Dispatchers.IO) {
    webService.resetPassword(databaseID, userPassword)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **databaseID** | **kotlin.String**| String representation of the database ID |
 **userPassword** | [**UserPassword**](UserPassword.md)| Map containing username and password. The specified password will be updated for the specified database user. |

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
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
val databaseID : kotlin.String = databaseID_example // kotlin.String | String representation of the database ID
val capacityUnits : CapacityUnits =  // CapacityUnits | Map containing capacityUnits key with a value greater than the current number of capacity units. The max increment of 3 additional capacity units can be added at one time.

launch(Dispatchers.IO) {
    webService.resizeDatabase(databaseID, capacityUnits)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **databaseID** | **kotlin.String**| String representation of the database ID |
 **capacityUnits** | [**CapacityUnits**](CapacityUnits.md)| Map containing capacityUnits key with a value greater than the current number of capacity units. The max increment of 3 additional capacity units can be added at one time. |

### Return type

null (empty response body)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Terminates a database

Terminates a database.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
val databaseID : kotlin.String = databaseID_example // kotlin.String | String representation of the database ID
val preparedStateOnly : kotlin.Boolean = true // kotlin.Boolean | For internal use only. Used to safely terminate prepared databases.

launch(Dispatchers.IO) {
    webService.terminateDatabase(databaseID, preparedStateOnly)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **databaseID** | **kotlin.String**| String representation of the database ID |
 **preparedStateOnly** | **kotlin.Boolean**| For internal use only. Used to safely terminate prepared databases. | [optional] [default to false]

### Return type

null (empty response body)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Unparks a database

Unparks a database.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
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


Update a role within an organization

Update a role within an organization.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
val roleID : kotlin.String = roleID_example // kotlin.String | id for the role
val updateRoleRequest : UpdateRoleRequest =  // UpdateRoleRequest | The model for update role body

launch(Dispatchers.IO) {
    val result : kotlin.collections.List<Role> = webService.updateRole(roleID, updateRoleRequest)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **roleID** | **kotlin.String**| id for the role |
 **updateRoleRequest** | [**UpdateRoleRequest**](UpdateRoleRequest.md)| The model for update role body | [optional]

### Return type

[**kotlin.collections.List&lt;Role&gt;**](Role.md)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Update organization roles for a user

### Example
```kotlin
// Import classes:
//import com.datastax.astra.devops_v2.*
//import com.datastax.astra.devops_v2.infrastructure.*
//import com.datastax.astra.devops_v2.models.*

val apiClient = ApiClient()
apiClient.setBearerToken("TOKEN")
val webService = apiClient.createWebservice(DBOperationsApi::class.java)
val userID : kotlin.String = userID_example // kotlin.String | id for the user
val roleInviteRequest : RoleInviteRequest =  // RoleInviteRequest | 

launch(Dispatchers.IO) {
    webService.updateRolesForUserInOrganization(userID, roleInviteRequest)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userID** | **kotlin.String**| id for the user |
 **roleInviteRequest** | [**RoleInviteRequest**](RoleInviteRequest.md)|  | [optional]

### Return type

null (empty response body)

### Authorization


Configure Bearer:
    ApiClient().setBearerToken("TOKEN")

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

