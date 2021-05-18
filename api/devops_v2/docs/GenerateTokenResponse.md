
# GenerateTokenResponse

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**clientId** | **kotlin.String** | The ID of the client (UUID). | 
**secret** | **kotlin.String** | The secret token. | 
**orgId** | **kotlin.String** | The UUID of the organization. | 
**roles** | **kotlin.collections.List&lt;kotlin.String&gt;** | The roles for which the token will be generated. | 
**token** | **kotlin.String** | AstraCS:clientId:hex(sha256(secret)) |  [optional]



