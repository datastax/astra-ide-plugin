package com.datastax.astra.jetbrains

import com.datastax.astra.devops_v2.apis.OperationsApi
import com.datastax.astra.jetbrains.credentials.ProfileManager
import com.datastax.astra.stargate_v2.apis.SchemasApi
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Nullable

import com.datastax.astra.devops_v2.models.Database

import java.net.URI


object AstraClient {
    lateinit var project: Project
    var accessToken: String = ""
        get() = ProfileManager.getInstance(project).activeProfile?.token.toString()

    fun operationsApi(): OperationsApi {
        println("using token: $accessToken")
        return com.datastax.astra.devops_v2.infrastructure.ApiClient(authName = "Bearer", bearerToken = accessToken)
                .createService(OperationsApi::class.java)
    }
    fun schemasApiForDatabase(database: Database): SchemasApi {
        val basePath = database.dataEndpointUrl.orEmpty().removeSuffix(URI(database.dataEndpointUrl).rawPath)
        return com.datastax.astra.stargate_v2.infrastructure.ApiClient(
            baseUrl = basePath
        ).createService(SchemasApi::class.java)
    }
}
fun test(){

}