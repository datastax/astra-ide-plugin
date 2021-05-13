package com.datastax.astra.jetbrains

import com.datastax.astra.devops_v2.apis.OperationsApi
import com.datastax.astra.jetbrains.credentials.ProfileManager
import com.datastax.astra.stargate_v2.apis.SchemasApi
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Nullable

object AstraClient {
    var accessToken: String = ""

    fun operationsApi(project: @Nullable Project?): OperationsApi {
        accessToken = project?.let { ProfileManager.getInstance(it).activeProfile?.token }.toString()
        return com.datastax.astra.devops_v2.infrastructure.ApiClient(authName = "Bearer", bearerToken = accessToken)
                .createService(OperationsApi::class.java)
    }
    fun schemasApi(basePath: String): SchemasApi {
        return com.datastax.astra.stargate_v2.infrastructure.ApiClient(
            baseUrl = basePath
        ).createService(SchemasApi::class.java)
    }
}
