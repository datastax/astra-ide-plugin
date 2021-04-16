
# DatabaseInfoCreate

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **kotlin.String** | Name of the database--user friendly identifier | 
**keyspace** | **kotlin.String** | Keyspace name in database | 
**cloudProvider** | [**inline**](#CloudProviderEnum) | This is the cloud provider where the database lives. | 
**tier** | [**inline**](#TierEnum) | With the exception of classic databases, all databases are serverless. Classic databases can no longer be created with the DevOps API. | 
**capacityUnits** | **kotlin.Int** | Capacity units were used for classic databases, but are not used for serverless databases. Enter 1 CU for serverless databases. Classic databases can no longer be created with the DevOps API. | 
**region** | **kotlin.String** | Region refers to the cloud region. | 


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



