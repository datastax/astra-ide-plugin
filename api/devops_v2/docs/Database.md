
# Database

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **kotlin.String** |  | 
**orgId** | **kotlin.String** |  | 
**ownerId** | **kotlin.String** |  | 
**info** | [**DatabaseInfo**](DatabaseInfo.md) |  | 
**status** | [**StatusEnum**](StatusEnum.md) |  | 
**creationTime** | **kotlin.String** | CreationTime in ISO RFC3339 format |  [optional]
**terminationTime** | **kotlin.String** | TerminationTime in ISO RFC3339 format |  [optional]
**storage** | [**Storage**](Storage.md) |  |  [optional]
**availableActions** | [**inline**](#kotlin.collections.List&lt;AvailableActionsEnum&gt;) |  |  [optional]
**message** | **kotlin.String** | Message to the customer about the cluster. |  [optional]
**studioUrl** | **kotlin.String** |  |  [optional]
**grafanaUrl** | **kotlin.String** |  |  [optional]
**cqlshUrl** | **kotlin.String** |  |  [optional]
**graphqlUrl** | **kotlin.String** |  |  [optional]
**dataEndpointUrl** | **kotlin.String** |  |  [optional]


<a name="kotlin.collections.List<AvailableActionsEnum>"></a>
## Enum: availableActions
Name | Value
---- | -----
availableActions | park, unpark, resize, resetPassword, addKeyspace, addDatacenters, terminateDatacenter, getCreds, terminate, removeKeyspace, addTable, removeMigrationProxy, launchMigrationProxy



