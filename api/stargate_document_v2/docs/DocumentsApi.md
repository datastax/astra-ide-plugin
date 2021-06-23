# DocumentsApi

All URIs are relative to *https://d341f349-e5db-46d2-9c90-bb9ebaa6f0fc-us-east-1.apps.astra.datastax.com*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addDoc**](DocumentsApi.md#addDoc) | **POST** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id} | Add a new document to {collection-id}
[**deleteCollectionSchema**](DocumentsApi.md#deleteCollectionSchema) | **DELETE** api/rest/v2/schemas/namespaces/{namespace-id}/collections/{collection-id} | Delete a collection
[**deleteDoc**](DocumentsApi.md#deleteDoc) | **DELETE** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id} | Delete a  document
[**deleteSubDoc**](DocumentsApi.md#deleteSubDoc) | **DELETE** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id}/{document-path} | Delete a sub document by {document-path}
[**getCollection**](DocumentsApi.md#getCollection) | **GET** api/rest/v2/schemas/namespaces/{namespace-id}/collections/{collection-id} | Get a collection
[**getDocById**](DocumentsApi.md#getDocById) | **GET** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id} | Get a document by {document-id}
[**getSubDocByPath**](DocumentsApi.md#getSubDocByPath) | **GET** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id}/{document-path} | Get a sub document by {document-path}
[**listCollections**](DocumentsApi.md#listCollections) | **GET** api/rest/v2/namespaces/{namespace-id}/collections | List collections in a namespace
[**replaceDoc**](DocumentsApi.md#replaceDoc) | **PUT** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id} | Replace a document
[**replaceSubDoc**](DocumentsApi.md#replaceSubDoc) | **PUT** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id}/{document-path} | Replace a sub document
[**searchDoc**](DocumentsApi.md#searchDoc) | **GET** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id} | Search for documents in {collection-id}
[**updatePartOfDoc**](DocumentsApi.md#updatePartOfDoc) | **PATCH** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id} | Update part of a document
[**updatePartOfSubDoc**](DocumentsApi.md#updatePartOfSubDoc) | **PATCH** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id}/{document-path} | Update part of a sub document by {document-path}



Add a new document to {collection-id}

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_document_v2.*
//import com.datastax.astra.stargate_document_v2.infrastructure.*
//import com.datastax.astra.stargate_document_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DocumentsApi::class.java)
val xCassandraRequestId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | Unique identifier (UUID) for the request. Use any valid UUID.
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val namespaceId : kotlin.String = namespaceId_example // kotlin.String | namespace name
val collectionId : kotlin.String = collectionId_example // kotlin.String | name of the document collection
val body : kotlin.Any = {"title":"Hello World","author":{"name":"CRW","social":{"foo-bar-789":{"followers":1,"likes":7}}}} // kotlin.Any | document
val pretty : kotlin.Boolean = true // kotlin.Boolean | format results

launch(Dispatchers.IO) {
    webService.addDoc(xCassandraRequestId, xCassandraToken, namespaceId, collectionId, body, pretty)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraRequestId** | [**java.util.UUID**](.md)| Unique identifier (UUID) for the request. Use any valid UUID. |
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **namespaceId** | **kotlin.String**| namespace name |
 **collectionId** | **kotlin.String**| name of the document collection |
 **body** | **kotlin.Any**| document |
 **pretty** | **kotlin.Boolean**| format results | [optional]

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Delete a collection

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_document_v2.*
//import com.datastax.astra.stargate_document_v2.infrastructure.*
//import com.datastax.astra.stargate_document_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DocumentsApi::class.java)
val xCassandraRequestId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | Unique identifier (UUID) for the request. Use any valid UUID.
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val namespaceId : kotlin.String = namespaceId_example // kotlin.String | namespace name
val collectionId : kotlin.String = collectionId_example // kotlin.String | name of the document collection
val pretty : kotlin.Boolean = true // kotlin.Boolean | format results

launch(Dispatchers.IO) {
    webService.deleteCollectionSchema(xCassandraRequestId, xCassandraToken, namespaceId, collectionId, pretty)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraRequestId** | [**java.util.UUID**](.md)| Unique identifier (UUID) for the request. Use any valid UUID. |
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **namespaceId** | **kotlin.String**| namespace name |
 **collectionId** | **kotlin.String**| name of the document collection |
 **pretty** | **kotlin.Boolean**| format results | [optional]

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Delete a  document

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_document_v2.*
//import com.datastax.astra.stargate_document_v2.infrastructure.*
//import com.datastax.astra.stargate_document_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DocumentsApi::class.java)
val xCassandraRequestId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | Unique identifier (UUID) for the request. Use any valid UUID.
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val namespaceId : kotlin.String = namespaceId_example // kotlin.String | namespace name
val collectionId : kotlin.String = collectionId_example // kotlin.String | name of the document collection
val documentId : kotlin.String = documentId_example // kotlin.String | the id of the document
val pretty : kotlin.Boolean = true // kotlin.Boolean | format results

launch(Dispatchers.IO) {
    webService.deleteDoc(xCassandraRequestId, xCassandraToken, namespaceId, collectionId, documentId, pretty)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraRequestId** | [**java.util.UUID**](.md)| Unique identifier (UUID) for the request. Use any valid UUID. |
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **namespaceId** | **kotlin.String**| namespace name |
 **collectionId** | **kotlin.String**| name of the document collection |
 **documentId** | **kotlin.String**| the id of the document |
 **pretty** | **kotlin.Boolean**| format results | [optional]

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Delete a sub document by {document-path}

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_document_v2.*
//import com.datastax.astra.stargate_document_v2.infrastructure.*
//import com.datastax.astra.stargate_document_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DocumentsApi::class.java)
val xCassandraRequestId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | Unique identifier (UUID) for the request. Use any valid UUID.
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val namespaceId : kotlin.String = namespaceId_example // kotlin.String | namespace name
val collectionId : kotlin.String = collectionId_example // kotlin.String | name of the document collection
val documentId : kotlin.String = documentId_example // kotlin.String | the id of the document
val documentPath : kotlin.String = documentPath_example // kotlin.String | a JSON path
val pretty : kotlin.Boolean = true // kotlin.Boolean | format results

launch(Dispatchers.IO) {
    webService.deleteSubDoc(xCassandraRequestId, xCassandraToken, namespaceId, collectionId, documentId, documentPath, pretty)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraRequestId** | [**java.util.UUID**](.md)| Unique identifier (UUID) for the request. Use any valid UUID. |
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **namespaceId** | **kotlin.String**| namespace name |
 **collectionId** | **kotlin.String**| name of the document collection |
 **documentId** | **kotlin.String**| the id of the document |
 **documentPath** | **kotlin.String**| a JSON path |
 **pretty** | **kotlin.Boolean**| format results | [optional]

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Get a collection

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_document_v2.*
//import com.datastax.astra.stargate_document_v2.infrastructure.*
//import com.datastax.astra.stargate_document_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DocumentsApi::class.java)
val xCassandraRequestId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | Unique identifier (UUID) for the request. Use any valid UUID.
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val namespaceId : kotlin.String = namespaceId_example // kotlin.String | namespace name
val collectionId : kotlin.String = collectionId_example // kotlin.String | name of the document collection
val pretty : kotlin.Boolean = true // kotlin.Boolean | format results
val raw : kotlin.Boolean = true // kotlin.Boolean | unwrap results

launch(Dispatchers.IO) {
    webService.getCollection(xCassandraRequestId, xCassandraToken, namespaceId, collectionId, pretty, raw)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraRequestId** | [**java.util.UUID**](.md)| Unique identifier (UUID) for the request. Use any valid UUID. |
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **namespaceId** | **kotlin.String**| namespace name |
 **collectionId** | **kotlin.String**| name of the document collection |
 **pretty** | **kotlin.Boolean**| format results | [optional]
 **raw** | **kotlin.Boolean**| unwrap results | [optional]

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Get a document by {document-id}

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_document_v2.*
//import com.datastax.astra.stargate_document_v2.infrastructure.*
//import com.datastax.astra.stargate_document_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DocumentsApi::class.java)
val xCassandraRequestId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | Unique identifier (UUID) for the request. Use any valid UUID.
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val namespaceId : kotlin.String = namespaceId_example // kotlin.String | namespace name
val collectionId : kotlin.String = collectionId_example // kotlin.String | name of the document collection
val documentId : kotlin.String = documentId_example // kotlin.String | the id of the document
val pretty : kotlin.Boolean = true // kotlin.Boolean | format results
val fields : kotlin.String = name, email // kotlin.String | URL escaped, comma delimited list of keys to include
val raw : kotlin.Boolean = true // kotlin.Boolean | unwrap results

launch(Dispatchers.IO) {
    webService.getDocById(xCassandraRequestId, xCassandraToken, namespaceId, collectionId, documentId, pretty, fields, raw)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraRequestId** | [**java.util.UUID**](.md)| Unique identifier (UUID) for the request. Use any valid UUID. |
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **namespaceId** | **kotlin.String**| namespace name |
 **collectionId** | **kotlin.String**| name of the document collection |
 **documentId** | **kotlin.String**| the id of the document |
 **pretty** | **kotlin.Boolean**| format results | [optional]
 **fields** | **kotlin.String**| URL escaped, comma delimited list of keys to include | [optional]
 **raw** | **kotlin.Boolean**| unwrap results | [optional]

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Get a sub document by {document-path}

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_document_v2.*
//import com.datastax.astra.stargate_document_v2.infrastructure.*
//import com.datastax.astra.stargate_document_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DocumentsApi::class.java)
val xCassandraRequestId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | Unique identifier (UUID) for the request. Use any valid UUID.
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val namespaceId : kotlin.String = namespaceId_example // kotlin.String | namespace name
val collectionId : kotlin.String = collectionId_example // kotlin.String | name of the document collection
val documentId : kotlin.String = documentId_example // kotlin.String | the id of the document
val documentPath : kotlin.String = documentPath_example // kotlin.String | a JSON path
val pretty : kotlin.Boolean = true // kotlin.Boolean | format results
val fields : kotlin.String = name, email // kotlin.String | URL escaped, comma delimited list of keys to include
val raw : kotlin.Boolean = true // kotlin.Boolean | unwrap results

launch(Dispatchers.IO) {
    webService.getSubDocByPath(xCassandraRequestId, xCassandraToken, namespaceId, collectionId, documentId, documentPath, pretty, fields, raw)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraRequestId** | [**java.util.UUID**](.md)| Unique identifier (UUID) for the request. Use any valid UUID. |
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **namespaceId** | **kotlin.String**| namespace name |
 **collectionId** | **kotlin.String**| name of the document collection |
 **documentId** | **kotlin.String**| the id of the document |
 **documentPath** | **kotlin.String**| a JSON path |
 **pretty** | **kotlin.Boolean**| format results | [optional]
 **fields** | **kotlin.String**| URL escaped, comma delimited list of keys to include | [optional]
 **raw** | **kotlin.Boolean**| unwrap results | [optional]

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


List collections in a namespace

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_document_v2.*
//import com.datastax.astra.stargate_document_v2.infrastructure.*
//import com.datastax.astra.stargate_document_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DocumentsApi::class.java)
val xCassandraRequestId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | Unique identifier (UUID) for the request. Use any valid UUID.
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val namespaceId : kotlin.String = namespaceId_example // kotlin.String | namespace name
val pretty : kotlin.Boolean = true // kotlin.Boolean | format results
val raw : kotlin.Boolean = true // kotlin.Boolean | unwrap results

launch(Dispatchers.IO) {
    webService.listCollections(xCassandraRequestId, xCassandraToken, namespaceId, pretty, raw)
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


Replace a document

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_document_v2.*
//import com.datastax.astra.stargate_document_v2.infrastructure.*
//import com.datastax.astra.stargate_document_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DocumentsApi::class.java)
val xCassandraRequestId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | Unique identifier (UUID) for the request. Use any valid UUID.
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val namespaceId : kotlin.String = namespaceId_example // kotlin.String | namespace name
val collectionId : kotlin.String = collectionId_example // kotlin.String | name of the document collection
val documentId : kotlin.String = documentId_example // kotlin.String | the id of the document
val body : kotlin.Any = {"title":"Hello World","author":{"name":"DKG"}} // kotlin.Any | document
val pretty : kotlin.Boolean = true // kotlin.Boolean | format results

launch(Dispatchers.IO) {
    webService.replaceDoc(xCassandraRequestId, xCassandraToken, namespaceId, collectionId, documentId, body, pretty)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraRequestId** | [**java.util.UUID**](.md)| Unique identifier (UUID) for the request. Use any valid UUID. |
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **namespaceId** | **kotlin.String**| namespace name |
 **collectionId** | **kotlin.String**| name of the document collection |
 **documentId** | **kotlin.String**| the id of the document |
 **body** | **kotlin.Any**| document |
 **pretty** | **kotlin.Boolean**| format results | [optional]

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Replace a sub document

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_document_v2.*
//import com.datastax.astra.stargate_document_v2.infrastructure.*
//import com.datastax.astra.stargate_document_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DocumentsApi::class.java)
val xCassandraRequestId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | Unique identifier (UUID) for the request. Use any valid UUID.
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val namespaceId : kotlin.String = namespaceId_example // kotlin.String | namespace name
val collectionId : kotlin.String = collectionId_example // kotlin.String | name of the document collection
val documentId : kotlin.String = documentId_example // kotlin.String | the id of the document
val documentPath : kotlin.String = documentPath_example // kotlin.String | a JSON path
val body : kotlin.Any = {"foo-bar-789":{"followers":1,"likes":7}} // kotlin.Any | document
val pretty : kotlin.Boolean = true // kotlin.Boolean | format results

launch(Dispatchers.IO) {
    webService.replaceSubDoc(xCassandraRequestId, xCassandraToken, namespaceId, collectionId, documentId, documentPath, body, pretty)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraRequestId** | [**java.util.UUID**](.md)| Unique identifier (UUID) for the request. Use any valid UUID. |
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **namespaceId** | **kotlin.String**| namespace name |
 **collectionId** | **kotlin.String**| name of the document collection |
 **documentId** | **kotlin.String**| the id of the document |
 **documentPath** | **kotlin.String**| a JSON path |
 **body** | **kotlin.Any**| document |
 **pretty** | **kotlin.Boolean**| format results | [optional]

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Search for documents in {collection-id}

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_document_v2.*
//import com.datastax.astra.stargate_document_v2.infrastructure.*
//import com.datastax.astra.stargate_document_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DocumentsApi::class.java)
val xCassandraRequestId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | Unique identifier (UUID) for the request. Use any valid UUID.
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val namespaceId : kotlin.String = namespaceId_example // kotlin.String | namespace name
val collectionId : kotlin.String = collectionId_example // kotlin.String | name of the document collection
val pretty : kotlin.Boolean = true // kotlin.Boolean | format results
val where : kotlin.Any = {"author.name":"Cliff Wicklow","createTime":{"$gte":0},"$or":[{"name":"Cliff"},{"documentId":"my-first-post-a6h54"}]} // kotlin.Any | URL escaped JSON query using the following keys:  | Key | Operation | |-|-| | $lt | Less Than | | $lte | Less Than Or Equal To | | $gt | Greater Than | | $gte | Greater Than Or Equal To | | $ne | Not Equal To | | $in | Contained In | | $exists | A value is set for the key | | $select | This matches a value for a key in the result of a different query | | $dontSelect | Requires that a key’s value not match a value for a key in the result of a different query | | $all | Contains all of the given values | | $regex | Requires that a key’s value match a regular expression | | $text | Performs a full text search on indexed fields | 
val fields : kotlin.String = name, email // kotlin.String | URL escaped, comma delimited list of keys to include
val pageSize : kotlin.Int = 10 // kotlin.Int | restrict the number of returned items (max 100)
val pageState : kotlin.String =  // kotlin.String | move the cursor to a particular result
val sort : kotlin.Any = {"documentId":"asc","name":"desc"} // kotlin.Any | keys to sort by
val raw : kotlin.Boolean = true // kotlin.Boolean | unwrap results

launch(Dispatchers.IO) {
    webService.searchDoc(xCassandraRequestId, xCassandraToken, namespaceId, collectionId, pretty, where, fields, pageSize, pageState, sort, raw)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraRequestId** | [**java.util.UUID**](.md)| Unique identifier (UUID) for the request. Use any valid UUID. |
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **namespaceId** | **kotlin.String**| namespace name |
 **collectionId** | **kotlin.String**| name of the document collection |
 **pretty** | **kotlin.Boolean**| format results | [optional]
 **where** | [**kotlin.Any**](.md)| URL escaped JSON query using the following keys:  | Key | Operation | |-|-| | $lt | Less Than | | $lte | Less Than Or Equal To | | $gt | Greater Than | | $gte | Greater Than Or Equal To | | $ne | Not Equal To | | $in | Contained In | | $exists | A value is set for the key | | $select | This matches a value for a key in the result of a different query | | $dontSelect | Requires that a key’s value not match a value for a key in the result of a different query | | $all | Contains all of the given values | | $regex | Requires that a key’s value match a regular expression | | $text | Performs a full text search on indexed fields |  | [optional]
 **fields** | **kotlin.String**| URL escaped, comma delimited list of keys to include | [optional]
 **pageSize** | **kotlin.Int**| restrict the number of returned items (max 100) | [optional]
 **pageState** | **kotlin.String**| move the cursor to a particular result | [optional]
 **sort** | [**kotlin.Any**](.md)| keys to sort by | [optional]
 **raw** | **kotlin.Boolean**| unwrap results | [optional]

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


Update part of a document

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_document_v2.*
//import com.datastax.astra.stargate_document_v2.infrastructure.*
//import com.datastax.astra.stargate_document_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DocumentsApi::class.java)
val xCassandraRequestId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | Unique identifier (UUID) for the request. Use any valid UUID.
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val namespaceId : kotlin.String = namespaceId_example // kotlin.String | namespace name
val collectionId : kotlin.String = collectionId_example // kotlin.String | name of the document collection
val documentId : kotlin.String = documentId_example // kotlin.String | the id of the document
val body : kotlin.Any = {"title":"Hello World"} // kotlin.Any | document
val pretty : kotlin.Boolean = true // kotlin.Boolean | format results

launch(Dispatchers.IO) {
    webService.updatePartOfDoc(xCassandraRequestId, xCassandraToken, namespaceId, collectionId, documentId, body, pretty)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraRequestId** | [**java.util.UUID**](.md)| Unique identifier (UUID) for the request. Use any valid UUID. |
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **namespaceId** | **kotlin.String**| namespace name |
 **collectionId** | **kotlin.String**| name of the document collection |
 **documentId** | **kotlin.String**| the id of the document |
 **body** | **kotlin.Any**| document |
 **pretty** | **kotlin.Boolean**| format results | [optional]

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


Update part of a sub document by {document-path}

### Example
```kotlin
// Import classes:
//import com.datastax.astra.stargate_document_v2.*
//import com.datastax.astra.stargate_document_v2.infrastructure.*
//import com.datastax.astra.stargate_document_v2.models.*

val apiClient = ApiClient()
val webService = apiClient.createWebservice(DocumentsApi::class.java)
val xCassandraRequestId : java.util.UUID = 38400000-8cf0-11bd-b23e-10b96e4ef00d // java.util.UUID | Unique identifier (UUID) for the request. Use any valid UUID.
val xCassandraToken : kotlin.String = xCassandraToken_example // kotlin.String | The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request.
val namespaceId : kotlin.String = namespaceId_example // kotlin.String | namespace name
val collectionId : kotlin.String = collectionId_example // kotlin.String | name of the document collection
val documentId : kotlin.String = documentId_example // kotlin.String | the id of the document
val documentPath : kotlin.String = documentPath_example // kotlin.String | a JSON path
val body : kotlin.Any = {"title":"Hello World"} // kotlin.Any | document
val pretty : kotlin.Boolean = true // kotlin.Boolean | format results

launch(Dispatchers.IO) {
    webService.updatePartOfSubDoc(xCassandraRequestId, xCassandraToken, namespaceId, collectionId, documentId, documentPath, body, pretty)
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **xCassandraRequestId** | [**java.util.UUID**](.md)| Unique identifier (UUID) for the request. Use any valid UUID. |
 **xCassandraToken** | **kotlin.String**| The application token for serverless databases or the token returned from the authorization endpoint for classic databases. Use this token in each request. |
 **namespaceId** | **kotlin.String**| namespace name |
 **collectionId** | **kotlin.String**| name of the document collection |
 **documentId** | **kotlin.String**| the id of the document |
 **documentPath** | **kotlin.String**| a JSON path |
 **body** | **kotlin.Any**| document |
 **pretty** | **kotlin.Boolean**| format results | [optional]

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

