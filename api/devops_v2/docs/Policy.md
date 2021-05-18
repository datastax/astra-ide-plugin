
# Policy

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**description** | **kotlin.String** | A description of this policy. | 
**resources** | **kotlin.collections.List&lt;kotlin.String&gt;** | The resources this policy can manipulate. | 
**actions** | [**inline**](#kotlin.collections.List&lt;ActionsEnum&gt;) | The actions this policy can take. Example actions: &#39;org-billing-write&#39; &#39;db-keyspace-create&#39;. | 
**effect** | [**inline**](#EffectEnum) | Effect this policy will have on the provided resource. | 


<a name="kotlin.collections.List<ActionsEnum>"></a>
## Enum: actions
Name | Value
---- | -----
actions | db-all-keyspace-create, db-all-keyspace-describe, db-cql, db-graphql, db-keyspace-alter, db-keyspace-authorize, db-keyspace-create, db-keyspace-describe, db-keyspace-drop, db-keyspace-grant, db-keyspace-modify, db-rest, db-table-alter, db-table-authorize, db-table-create, db-table-describe, db-table-drop, db-table-grant, db-table-modify, db-table-select, org-audits-read, org-billing-read, org-billing-write, org-db-addpeering, org-db-create, org-db-expand, org-db-managemigratorproxy, org-db-passwordreset, org-db-suspend, org-db-terminate, org-db-view, org-external-auth-read, org-external-auth-write, org-notification-write, org-read, org-role-delete, org-role-read, org-role-write, org-token-read, org-token-write, org-user-read, org-user-write, org-write


<a name="EffectEnum"></a>
## Enum: effect
Name | Value
---- | -----
effect | allow



