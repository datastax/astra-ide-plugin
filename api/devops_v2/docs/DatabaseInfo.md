
# DatabaseInfo

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **kotlin.String** | Name of the database--user friendly identifier. |  [optional]
**keyspace** | **kotlin.String** | Keyspace name in database. |  [optional]
**cloudProvider** | [**inline**](#CloudProviderEnum) | This is the cloud provider where the database lives. |  [optional]
**tier** | [**inline**](#TierEnum) | With the exception of classic databases, all databases are serverless. Classic databases can no longer be created with the DevOps API. |  [optional]
**capacityUnits** | **kotlin.Int** | Capacity units were used for classic databases, but are not used for serverless databases. Enter 1 CU for serverless databases. Classic databases can no longer be created with the DevOps API. |  [optional]
**region** | **kotlin.String** | Region refers to the cloud region. |  [optional]
**user** | **kotlin.String** | User is the user to access the database. |  [optional]
**password** | **kotlin.String** | Password for the user to access the database. |  [optional]
**additionalKeyspaces** | **kotlin.collections.List&lt;kotlin.String&gt;** | Additional keyspaces names in database. |  [optional]


<a name="CloudProviderEnum"></a>
## Enum: cloudProvider
Name | Value
---- | -----
cloudProvider | AWS, GCP


<a name="TierEnum"></a>
## Enum: tier
Name | Value
---- | -----
tier | serverless



