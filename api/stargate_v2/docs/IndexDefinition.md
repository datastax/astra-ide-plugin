
# IndexDefinition

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**column** | **kotlin.String** | Column for which index will be created. | 
**name** | **kotlin.String** | Optional name for the index, which must be unique. If no name is specified, the index is named as follows: tablename_columnname_idx. |  [optional]
**type** | [**inline**](#TypeEnum) | Type of index, defined with a custom index class name or classpath. Secondary index is default, no type entered |  [optional]
**options** | **kotlin.collections.Map&lt;kotlin.String, kotlin.String&gt;** |  |  [optional]
**kind** | [**inline**](#KindEnum) | Index kind for collections. |  [optional]
**ifNotExists** | **kotlin.Boolean** | Determines creation of a new index, if an index with the same name exists. If an index exists, and this option is set to true, an error is returned. |  [optional]


<a name="TypeEnum"></a>
## Enum: type
Name | Value
---- | -----
type | org.apache.cassandra.index.sasi.SASIIndex, StorageAttachedIndex


<a name="KindEnum"></a>
## Enum: kind
Name | Value
---- | -----
kind | FULL, KEYS, VALUES, ENTRIES



