# com.datastax.astra.stargate_rest_v2 - Kotlin client library for Stargate REST API reference

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

All URIs are relative to *https://-.apps.astra.datastax.com*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*AuthApi* | [**createToken**](docs/AuthApi.md#createtoken) | **POST** api/rest/v1/auth | Create an authorization token
*DataApi* | [**addRows**](docs/DataApi.md#addrows) | **POST** api/rest/v2/keyspaces/{keyspace-id}/{table-id} | Add rows
*DataApi* | [**deleteRows**](docs/DataApi.md#deleterows) | **DELETE** api/rest/v2/keyspaces/{keyspace-id}/{table-id}/{primary-key} | Delete a row by primary key
*DataApi* | [**getRows**](docs/DataApi.md#getrows) | **GET** api/rest/v2/keyspaces/{keyspace-id}/{table-id}/{primary-key} | Get a row
*DataApi* | [**replaceRows**](docs/DataApi.md#replacerows) | **PUT** api/rest/v2/keyspaces/{keyspace-id}/{table-id}/{primary-key} | Replace a row by primary key
*DataApi* | [**searchTable**](docs/DataApi.md#searchtable) | **GET** api/rest/v2/keyspaces/{keyspace-id}/{table-id} | Search a table
*DataApi* | [**updateRows**](docs/DataApi.md#updaterows) | **PATCH** api/rest/v2/keyspaces/{keyspace-id}/{table-id}/{primary-key} | Update a row by primary key
*SchemasApi* | [**createColumn**](docs/SchemasApi.md#createcolumn) | **POST** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns | Create a column
*SchemasApi* | [**createIndex**](docs/SchemasApi.md#createindex) | **POST** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/indexes | Create an index
*SchemasApi* | [**createTable**](docs/SchemasApi.md#createtable) | **POST** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables | Create a table
*SchemasApi* | [**deleteColumn**](docs/SchemasApi.md#deletecolumn) | **DELETE** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns/{column-id} | Delete a column
*SchemasApi* | [**deleteIndex**](docs/SchemasApi.md#deleteindex) | **DELETE** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/indexes/{index-id} | Delete an index
*SchemasApi* | [**deleteTable**](docs/SchemasApi.md#deletetable) | **DELETE** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id} | Delete a table
*SchemasApi* | [**getColumn**](docs/SchemasApi.md#getcolumn) | **GET** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns/{column-id} | Get a column
*SchemasApi* | [**getColumns**](docs/SchemasApi.md#getcolumns) | **GET** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns | List columns
*SchemasApi* | [**getIndexes**](docs/SchemasApi.md#getindexes) | **GET** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/indexes | List indexes for a given table
*SchemasApi* | [**getKeyspace**](docs/SchemasApi.md#getkeyspace) | **GET** api/rest/v2/schemas/keyspaces/{keyspace-id} | Get a keyspace using the {keyspace-id}
*SchemasApi* | [**getKeyspaces**](docs/SchemasApi.md#getkeyspaces) | **GET** api/rest/v2/schemas/keyspaces | Get all keyspaces
*SchemasApi* | [**getTable**](docs/SchemasApi.md#gettable) | **GET** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id} | Get a table
*SchemasApi* | [**getTables**](docs/SchemasApi.md#gettables) | **GET** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables | Get all tables
*SchemasApi* | [**replaceColumn**](docs/SchemasApi.md#replacecolumn) | **PUT** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id}/columns/{column-id} | Replace a column definition
*SchemasApi* | [**replaceTable**](docs/SchemasApi.md#replacetable) | **PUT** api/rest/v2/schemas/keyspaces/{keyspace-id}/tables/{table-id} | Replace a table definition, except for columns


<a name="documentation-for-models"></a>
## Documentation for Models

 - [com.datastax.astra.stargate_rest_v2.models.AuthTokenResponse](docs/AuthTokenResponse.md)
 - [com.datastax.astra.stargate_rest_v2.models.ClusteringExpression](docs/ClusteringExpression.md)
 - [com.datastax.astra.stargate_rest_v2.models.ColumnDefinition](docs/ColumnDefinition.md)
 - [com.datastax.astra.stargate_rest_v2.models.Credentials](docs/Credentials.md)
 - [com.datastax.astra.stargate_rest_v2.models.Datacenter](docs/Datacenter.md)
 - [com.datastax.astra.stargate_rest_v2.models.Error](docs/Error.md)
 - [com.datastax.astra.stargate_rest_v2.models.GetResponseWrapper](docs/GetResponseWrapper.md)
 - [com.datastax.astra.stargate_rest_v2.models.IndexDefinition](docs/IndexDefinition.md)
 - [com.datastax.astra.stargate_rest_v2.models.IndexOptions](docs/IndexOptions.md)
 - [com.datastax.astra.stargate_rest_v2.models.InlineResponse200](docs/InlineResponse200.md)
 - [com.datastax.astra.stargate_rest_v2.models.InlineResponse2001](docs/InlineResponse2001.md)
 - [com.datastax.astra.stargate_rest_v2.models.InlineResponse2002](docs/InlineResponse2002.md)
 - [com.datastax.astra.stargate_rest_v2.models.InlineResponse2003](docs/InlineResponse2003.md)
 - [com.datastax.astra.stargate_rest_v2.models.InlineResponse2004](docs/InlineResponse2004.md)
 - [com.datastax.astra.stargate_rest_v2.models.InlineResponse201](docs/InlineResponse201.md)
 - [com.datastax.astra.stargate_rest_v2.models.Keyspace](docs/Keyspace.md)
 - [com.datastax.astra.stargate_rest_v2.models.PrimaryKey](docs/PrimaryKey.md)
 - [com.datastax.astra.stargate_rest_v2.models.ResponseWrapper](docs/ResponseWrapper.md)
 - [com.datastax.astra.stargate_rest_v2.models.Table](docs/Table.md)
 - [com.datastax.astra.stargate_rest_v2.models.TableAdd](docs/TableAdd.md)
 - [com.datastax.astra.stargate_rest_v2.models.TableOptions](docs/TableOptions.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

All endpoints do not require authorization.
