# AuthApi

All URIs are relative to *https://-.apps.astra.datastax.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createToken**](AuthApi.md#createToken) | **POST** api/rest/v1/auth | Create an authorization token



Create an authorization token

To create an authorization token, you&#39;ll need the {databaseid} and {region} for your server, in addition to the path parameters. This applies only if you are using a classic database that was created before 4 March 2021 that has not migrated to the newest authentication.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_rest_v2.*
//import com.datastax.astra.stargate_rest_v2.infrastructure.*
//import com.datastax.astra.stargate_rest_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(AuthApi::class.java)
val credentials : Credentials =  // Credentials | 

launch(Dispatchers.IO) {
    val result : AuthTokenResponse = webService.createToken(credentials)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **credentials** | [**Credentials**](Credentials.md)|  |

### Return type

[**AuthTokenResponse**](AuthTokenResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json, */*

