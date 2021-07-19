package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.devops_v2.apis.DBOperationsApi
import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.utils.internal_devops.InternalDevOpsApi

/*
Client that uses the operationsApi client for verifying tokens
 */
object CredentialsClient {
    fun operationsApi(token: String): DBOperationsApi {
        return com.datastax.astra.devops_v2.infrastructure.ApiClient(authName = "Bearer", bearerToken = token)
            .createService(DBOperationsApi::class.java)
    }

    fun internalOpsApi(): InternalDevOpsApi {
        return com.datastax.astra.devops_v2.infrastructure.ApiClient(message("internal.devops.base_url"))
            .createService(InternalDevOpsApi::class.java)
    }
}
