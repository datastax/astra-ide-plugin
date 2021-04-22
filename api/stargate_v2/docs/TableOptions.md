
# TableOptions

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**defaultTimeToLive** | **kotlin.Int** | Defines the Time To Live (TTL), which determines the time period (in seconds) to expire data. If the value is &gt;0, TTL is enabled for the entire table and an expiration timestamp is added to each column. The maximum value is 630720000 (20 years). A new TTL timestamp is calculated each time the data is updated and the row is removed after the data expires. |  [optional]
**clusteringExpression** | [**kotlin.collections.List&lt;ClusteringExpression&gt;**](ClusteringExpression.md) |  |  [optional]



