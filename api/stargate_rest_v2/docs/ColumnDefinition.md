
# ColumnDefinition

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **kotlin.String** | Name for the column, which must be unique. | 
**typeDefinition** | [**inline**](#TypeDefinitionEnum) | The type of data allowed in the column. | 
**static** | **kotlin.Boolean** | Denotes whether the column is shared by all rows of a partition. |  [optional]


<a name="TypeDefinitionEnum"></a>
## Enum: typeDefinition
Name | Value
---- | -----
typeDefinition | ascii, text, varchar, tinyint, smallint, int, bigint, varint, decimal, float, double, date, DateRangeType, duration, time, timestamp, uuid, timeuuid, blob, boolean, counter, inet, PointType, LineStringType, PolygonType, frozen, list, map, set, tuple



