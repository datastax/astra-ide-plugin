# com.datastax.astra.stargate_document_v2 - Kotlin client library for Stargate Document API reference

## Requires

* Kotlin 1.4.30
* Gradle 6.8.3

## Build

First, create the gradle wrapper script:

```
gradle wrapper
```

Then, run:

```
./gradlew check assemble
```

This runs all tests and packages the library.

## Features/Implementation Notes

* Supports JSON inputs/outputs, File inputs, and Form inputs.
* Supports collection formats for query parameters: csv, tsv, ssv, pipes.
* Some Kotlin and Java types are fully qualified to avoid conflicts with types defined in OpenAPI definitions.
* Implementation of ApiClient is intended to reduce method counts, specifically to benefit Android targets.

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *https://d341f349-e5db-46d2-9c90-bb9ebaa6f0fc-us-east-1.apps.astra.datastax.com*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*DocumentsApi* | [**addDoc**](docs/DocumentsApi.md#adddoc) | **POST** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id} | Add a new document to {collection-id}
*DocumentsApi* | [**deleteCollectionSchema**](docs/DocumentsApi.md#deletecollectionschema) | **DELETE** api/rest/v2/schemas/namespaces/{namespace-id}/collections/{collection-id} | Delete a collection
*DocumentsApi* | [**deleteDoc**](docs/DocumentsApi.md#deletedoc) | **DELETE** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id} | Delete a  document
*DocumentsApi* | [**deleteSubDoc**](docs/DocumentsApi.md#deletesubdoc) | **DELETE** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id}/{document-path} | Delete a sub document by {document-path}
*DocumentsApi* | [**getCollection**](docs/DocumentsApi.md#getcollection) | **GET** api/rest/v2/schemas/namespaces/{namespace-id}/collections/{collection-id} | Get a collection
*DocumentsApi* | [**getDocById**](docs/DocumentsApi.md#getdocbyid) | **GET** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id} | Get a document by {document-id}
*DocumentsApi* | [**getSubDocByPath**](docs/DocumentsApi.md#getsubdocbypath) | **GET** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id}/{document-path} | Get a sub document by {document-path}
*DocumentsApi* | [**listCollections**](docs/DocumentsApi.md#listcollections) | **GET** api/rest/v2/namespaces/{namespace-id}/collections | List collections in a namespace
*DocumentsApi* | [**replaceDoc**](docs/DocumentsApi.md#replacedoc) | **PUT** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id} | Replace a document
*DocumentsApi* | [**replaceSubDoc**](docs/DocumentsApi.md#replacesubdoc) | **PUT** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id}/{document-path} | Replace a sub document
*DocumentsApi* | [**searchDoc**](docs/DocumentsApi.md#searchdoc) | **GET** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id} | Search for documents in {collection-id}
*DocumentsApi* | [**updatePartOfDoc**](docs/DocumentsApi.md#updatepartofdoc) | **PATCH** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id} | Update part of a document
*DocumentsApi* | [**updatePartOfSubDoc**](docs/DocumentsApi.md#updatepartofsubdoc) | **PATCH** api/rest/v2/namespaces/{namespace-id}/collections/{collection-id}/{document-id}/{document-path} | Update part of a sub document by {document-path}
*SchemasApi* | [**getAllNamespaces**](docs/SchemasApi.md#getallnamespaces) | **GET** api/rest/v2/schemas/namespaces | Get all namespaces
*SchemasApi* | [**getNamespace**](docs/SchemasApi.md#getnamespace) | **GET** api/rest/v2/schemas/namespaces/{namespace-id} | Get a namespace


<a name="documentation-for-models"></a>
## Documentation for Models

 - [com.datastax.astra.stargate_document_v2.models.Credentials](docs/Credentials.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

All endpoints do not require authorization.
