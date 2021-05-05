package com.datastax.astra.jetbrains

import com.datastax.astra.devops_v2.apis.OperationsApi
import com.datastax.astra.jetbrains.credentials.CredentialsClient
import com.datastax.astra.stargate_v2.apis.SchemasApi

object AstraClient {
    val astraCredClient = CredentialsClient()
    val accessToken = astraCredClient.token()

    fun operationsApi(): OperationsApi {
        return com.datastax.astra.devops_v2.infrastructure.ApiClient(authName = "Bearer", bearerToken = accessToken)
                .createService(OperationsApi::class.java)
    }
    fun schemasApi(basePath: String): SchemasApi {
        return com.datastax.astra.stargate_v2.infrastructure.ApiClient(
            baseUrl = basePath
        ).createService(SchemasApi::class.java)
    }
}
