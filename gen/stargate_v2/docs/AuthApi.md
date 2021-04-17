# AuthApi

All URIs are relative to *https://-.apps.astra.datastax.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createToken**](AuthApi.md#createToken) | **POST** /api/rest/v1/auth | Create an authorization token


<a name="createToken"></a>
# **createToken**
> AuthTokenResponse createToken(credentials)

Create an authorization token

To create an authorization token, you&#39;ll need the {databaseid} and {region} for your server, in addition to the path parameters. This applies only if you are using a classic database that was created before 4 March 2021 that has not migrated to the newest authentication.

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_v2.infrastructure.*
//import com.datastax.astra.stargate_v2.models.*

val apiInstance = AuthApi()
val credentials : Credentials =  // Credentials | 
try {
    val result : AuthTokenResponse = apiInstance.createToken(credentials)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling AuthApi#createToken")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling AuthApi#createToken")
    e.printStackTrace()
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

