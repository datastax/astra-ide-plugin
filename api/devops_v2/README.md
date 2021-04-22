# com.datastax.astra.devops_v2 - Kotlin client library for Astra DevOps API

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

All URIs are relative to *https://api.astra.datastax.com*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*OperationsApi* | [**addKeyspace**](docs/OperationsApi.md#addkeyspace) | **POST** v2/databases/{databaseID}/keyspaces/{keyspaceName} | Adds keyspace into database
*OperationsApi* | [**createDatabase**](docs/OperationsApi.md#createdatabase) | **POST** v2/databases | Create a new database
*OperationsApi* | [**generateSecureBundleURL**](docs/OperationsApi.md#generatesecurebundleurl) | **POST** v2/databases/{databaseID}/secureBundleURL | Obtain zip for connecting to the database
*OperationsApi* | [**getDatabase**](docs/OperationsApi.md#getdatabase) | **GET** v2/databases/{databaseID} | Finds database by ID
*OperationsApi* | [**listAvailableRegions**](docs/OperationsApi.md#listavailableregions) | **GET** v2/availableRegions | Returns supported regions and availability for a given user and organization
*OperationsApi* | [**listDatabases**](docs/OperationsApi.md#listdatabases) | **GET** v2/databases | Returns a list of databases
*OperationsApi* | [**parkDatabase**](docs/OperationsApi.md#parkdatabase) | **POST** v2/databases/{databaseID}/park | Parks a database
*OperationsApi* | [**resetPassword**](docs/OperationsApi.md#resetpassword) | **POST** v2/databases/{databaseID}/resetPassword | Resets Password
*OperationsApi* | [**resizeDatabase**](docs/OperationsApi.md#resizedatabase) | **POST** v2/databases/{databaseID}/resize | Resizes a database
*OperationsApi* | [**terminateDatabase**](docs/OperationsApi.md#terminatedatabase) | **POST** v2/databases/{databaseID}/terminate | Terminates a database
*OperationsApi* | [**unparkDatabase**](docs/OperationsApi.md#unparkdatabase) | **POST** v2/databases/{databaseID}/unpark | Unparks a database


<a name="documentation-for-models"></a>
## Documentation for Models

 - [com.datastax.astra.devops_v2.models.AvailableRegionCombination](docs/AvailableRegionCombination.md)
 - [com.datastax.astra.devops_v2.models.CapacityUnits](docs/CapacityUnits.md)
 - [com.datastax.astra.devops_v2.models.Costs](docs/Costs.md)
 - [com.datastax.astra.devops_v2.models.CredsURL](docs/CredsURL.md)
 - [com.datastax.astra.devops_v2.models.Database](docs/Database.md)
 - [com.datastax.astra.devops_v2.models.DatabaseInfo](docs/DatabaseInfo.md)
 - [com.datastax.astra.devops_v2.models.DatabaseInfoCreate](docs/DatabaseInfoCreate.md)
 - [com.datastax.astra.devops_v2.models.Error](docs/Error.md)
 - [com.datastax.astra.devops_v2.models.Errors](docs/Errors.md)
 - [com.datastax.astra.devops_v2.models.MigrationProxyConfiguration](docs/MigrationProxyConfiguration.md)
 - [com.datastax.astra.devops_v2.models.MigrationProxyMapping](docs/MigrationProxyMapping.md)
 - [com.datastax.astra.devops_v2.models.RegionCombination](docs/RegionCombination.md)
 - [com.datastax.astra.devops_v2.models.ServiceAccountTokenInput](docs/ServiceAccountTokenInput.md)
 - [com.datastax.astra.devops_v2.models.ServiceAccountTokenResponse](docs/ServiceAccountTokenResponse.md)
 - [com.datastax.astra.devops_v2.models.StatusEnum](docs/StatusEnum.md)
 - [com.datastax.astra.devops_v2.models.Storage](docs/Storage.md)
 - [com.datastax.astra.devops_v2.models.UserPassword](docs/UserPassword.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

<a name="Bearer"></a>
### Bearer

- **Type**: HTTP basic authentication

