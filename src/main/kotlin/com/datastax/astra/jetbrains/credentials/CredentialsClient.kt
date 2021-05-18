package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.devops_v2.apis.OperationsApi

/*
Client that uses the operationsApi client for verifying tokens
 */
object CredentialsClient {
    fun operationsApi(token: String): OperationsApi {
        return com.datastax.astra.devops_v2.infrastructure.ApiClient(authName = "Bearer", bearerToken = token)
            .createService(OperationsApi::class.java)
    }
}

