package com.datastax.astra.jetbrains

import com.datastax.astra.devops_v2.apis.OperationsApi
import com.datastax.astra.stargate_v2.apis.SchemasApi
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

class AstraClient {
    val accessToken =
        "GET FROM ACCOUNT SETTINGS"

    init {
        com.datastax.astra.stargate_v2.infrastructure.ApiClient.accessToken = accessToken
        com.datastax.astra.devops_v2.infrastructure.ApiClient.accessToken = accessToken
    }

    val operationsApi: OperationsApi
        get() = OperationsApi()

    fun schemasApi(basePath: String) = SchemasApi(basePath)


    companion object {
        @JvmStatic
        fun getInstance(): AstraClient = AstraClient()
    }
}

fun Project.astraClient(): AstraClient {
    return AstraClient.getInstance()
}
