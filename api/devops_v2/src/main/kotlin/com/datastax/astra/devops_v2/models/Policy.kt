/**
* Astra DevOps API
* Use these REST APIs to perform lifecycle actions for DataStax Astra databases and DataStax Astra Streaming Pulsar instances.</br>  # Authentication  <!-- ReDoc-Inject: <security-definitions> -->
*
* The version of the OpenAPI document: 2.2.0
* Contact: ad-astra@datastax.com
*
* NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
* https://openapi-generator.tech
* Do not edit the class manually.
*/
package com.datastax.astra.devops_v2.models

import com.google.gson.annotations.SerializedName

/**
 * A policy for a role in Astra.
 * @param description A description of this policy.
 * @param resources The resources this policy can manipulate.
 * @param actions The actions this policy can take. Example actions: 'org-billing-write' 'db-keyspace-create'.
 * @param effect Effect this policy will have on the provided resource.
 */

data class Policy(
    /* A description of this policy. */
    @SerializedName("description")
    val description: kotlin.String,
    /* The resources this policy can manipulate. */
    @SerializedName("resources")
    val resources: kotlin.collections.List<kotlin.String>,
    /* The actions this policy can take. Example actions: 'org-billing-write' 'db-keyspace-create'. */
    @SerializedName("actions")
    val actions: kotlin.collections.List<Policy.Actions>,
    /* Effect this policy will have on the provided resource. */
    @SerializedName("effect")
    val effect: Policy.Effect
) {

    /**
     * The actions this policy can take. Example actions: 'org-billing-write' 'db-keyspace-create'.
     * Values: DB_MINUS_ALL_MINUS_KEYSPACE_MINUS_CREATE,DB_MINUS_ALL_MINUS_KEYSPACE_MINUS_DESCRIBE,DB_MINUS_CQL,DB_MINUS_GRAPHQL,DB_MINUS_KEYSPACE_MINUS_ALTER,DB_MINUS_KEYSPACE_MINUS_AUTHORIZE,DB_MINUS_KEYSPACE_MINUS_CREATE,DB_MINUS_KEYSPACE_MINUS_DESCRIBE,DB_MINUS_KEYSPACE_MINUS_DROP,DB_MINUS_KEYSPACE_MINUS_GRANT,DB_MINUS_KEYSPACE_MINUS_MODIFY,DB_MINUS_REST,DB_MINUS_TABLE_MINUS_ALTER,DB_MINUS_TABLE_MINUS_AUTHORIZE,DB_MINUS_TABLE_MINUS_CREATE,DB_MINUS_TABLE_MINUS_DESCRIBE,DB_MINUS_TABLE_MINUS_DROP,DB_MINUS_TABLE_MINUS_GRANT,DB_MINUS_TABLE_MINUS_MODIFY,DB_MINUS_TABLE_MINUS_SELECT,ORG_MINUS_AUDITS_MINUS_READ,ORG_MINUS_BILLING_MINUS_READ,ORG_MINUS_BILLING_MINUS_WRITE,ORG_MINUS_DB_MINUS_ADDPEERING,ORG_MINUS_DB_MINUS_CREATE,ORG_MINUS_DB_MINUS_EXPAND,ORG_MINUS_DB_MINUS_MANAGEMIGRATORPROXY,ORG_MINUS_DB_MINUS_PASSWORDRESET,ORG_MINUS_DB_MINUS_SUSPEND,ORG_MINUS_DB_MINUS_TERMINATE,ORG_MINUS_DB_MINUS_VIEW,ORG_MINUS_EXTERNAL_MINUS_AUTH_MINUS_READ,ORG_MINUS_EXTERNAL_MINUS_AUTH_MINUS_WRITE,ORG_MINUS_NOTIFICATION_MINUS_WRITE,ORG_MINUS_READ,ORG_MINUS_ROLE_MINUS_DELETE,ORG_MINUS_ROLE_MINUS_READ,ORG_MINUS_ROLE_MINUS_WRITE,ORG_MINUS_TOKEN_MINUS_READ,ORG_MINUS_TOKEN_MINUS_WRITE,ORG_MINUS_USER_MINUS_READ,ORG_MINUS_USER_MINUS_WRITE,ORG_MINUS_WRITE
     */
    enum class Actions(val value: kotlin.String) {
        @SerializedName(value = "db-all-keyspace-create") DB_MINUS_ALL_MINUS_KEYSPACE_MINUS_CREATE("db-all-keyspace-create"),
        @SerializedName(value = "db-all-keyspace-describe") DB_MINUS_ALL_MINUS_KEYSPACE_MINUS_DESCRIBE("db-all-keyspace-describe"),
        @SerializedName(value = "db-cql") DB_MINUS_CQL("db-cql"),
        @SerializedName(value = "db-graphql") DB_MINUS_GRAPHQL("db-graphql"),
        @SerializedName(value = "db-keyspace-alter") DB_MINUS_KEYSPACE_MINUS_ALTER("db-keyspace-alter"),
        @SerializedName(value = "db-keyspace-authorize") DB_MINUS_KEYSPACE_MINUS_AUTHORIZE("db-keyspace-authorize"),
        @SerializedName(value = "db-keyspace-create") DB_MINUS_KEYSPACE_MINUS_CREATE("db-keyspace-create"),
        @SerializedName(value = "db-keyspace-describe") DB_MINUS_KEYSPACE_MINUS_DESCRIBE("db-keyspace-describe"),
        @SerializedName(value = "db-keyspace-drop") DB_MINUS_KEYSPACE_MINUS_DROP("db-keyspace-drop"),
        @SerializedName(value = "db-keyspace-grant") DB_MINUS_KEYSPACE_MINUS_GRANT("db-keyspace-grant"),
        @SerializedName(value = "db-keyspace-modify") DB_MINUS_KEYSPACE_MINUS_MODIFY("db-keyspace-modify"),
        @SerializedName(value = "db-rest") DB_MINUS_REST("db-rest"),
        @SerializedName(value = "db-table-alter") DB_MINUS_TABLE_MINUS_ALTER("db-table-alter"),
        @SerializedName(value = "db-table-authorize") DB_MINUS_TABLE_MINUS_AUTHORIZE("db-table-authorize"),
        @SerializedName(value = "db-table-create") DB_MINUS_TABLE_MINUS_CREATE("db-table-create"),
        @SerializedName(value = "db-table-describe") DB_MINUS_TABLE_MINUS_DESCRIBE("db-table-describe"),
        @SerializedName(value = "db-table-drop") DB_MINUS_TABLE_MINUS_DROP("db-table-drop"),
        @SerializedName(value = "db-table-grant") DB_MINUS_TABLE_MINUS_GRANT("db-table-grant"),
        @SerializedName(value = "db-table-modify") DB_MINUS_TABLE_MINUS_MODIFY("db-table-modify"),
        @SerializedName(value = "db-table-select") DB_MINUS_TABLE_MINUS_SELECT("db-table-select"),
        @SerializedName(value = "org-audits-read") ORG_MINUS_AUDITS_MINUS_READ("org-audits-read"),
        @SerializedName(value = "org-billing-read") ORG_MINUS_BILLING_MINUS_READ("org-billing-read"),
        @SerializedName(value = "org-billing-write") ORG_MINUS_BILLING_MINUS_WRITE("org-billing-write"),
        @SerializedName(value = "org-db-addpeering") ORG_MINUS_DB_MINUS_ADDPEERING("org-db-addpeering"),
        @SerializedName(value = "org-db-create") ORG_MINUS_DB_MINUS_CREATE("org-db-create"),
        @SerializedName(value = "org-db-expand") ORG_MINUS_DB_MINUS_EXPAND("org-db-expand"),
        @SerializedName(value = "org-db-managemigratorproxy") ORG_MINUS_DB_MINUS_MANAGEMIGRATORPROXY("org-db-managemigratorproxy"),
        @SerializedName(value = "org-db-passwordreset") ORG_MINUS_DB_MINUS_PASSWORDRESET("org-db-passwordreset"),
        @SerializedName(value = "org-db-suspend") ORG_MINUS_DB_MINUS_SUSPEND("org-db-suspend"),
        @SerializedName(value = "org-db-terminate") ORG_MINUS_DB_MINUS_TERMINATE("org-db-terminate"),
        @SerializedName(value = "org-db-view") ORG_MINUS_DB_MINUS_VIEW("org-db-view"),
        @SerializedName(value = "org-external-auth-read") ORG_MINUS_EXTERNAL_MINUS_AUTH_MINUS_READ("org-external-auth-read"),
        @SerializedName(value = "org-external-auth-write") ORG_MINUS_EXTERNAL_MINUS_AUTH_MINUS_WRITE("org-external-auth-write"),
        @SerializedName(value = "org-notification-write") ORG_MINUS_NOTIFICATION_MINUS_WRITE("org-notification-write"),
        @SerializedName(value = "org-read") ORG_MINUS_READ("org-read"),
        @SerializedName(value = "org-role-delete") ORG_MINUS_ROLE_MINUS_DELETE("org-role-delete"),
        @SerializedName(value = "org-role-read") ORG_MINUS_ROLE_MINUS_READ("org-role-read"),
        @SerializedName(value = "org-role-write") ORG_MINUS_ROLE_MINUS_WRITE("org-role-write"),
        @SerializedName(value = "org-token-read") ORG_MINUS_TOKEN_MINUS_READ("org-token-read"),
        @SerializedName(value = "org-token-write") ORG_MINUS_TOKEN_MINUS_WRITE("org-token-write"),
        @SerializedName(value = "org-user-read") ORG_MINUS_USER_MINUS_READ("org-user-read"),
        @SerializedName(value = "org-user-write") ORG_MINUS_USER_MINUS_WRITE("org-user-write"),
        @SerializedName(value = "org-write") ORG_MINUS_WRITE("org-write");
    }
    /**
     * Effect this policy will have on the provided resource.
     * Values: ALLOW
     */
    enum class Effect(val value: kotlin.String) {
        @SerializedName(value = "allow") ALLOW("allow");
    }
}
