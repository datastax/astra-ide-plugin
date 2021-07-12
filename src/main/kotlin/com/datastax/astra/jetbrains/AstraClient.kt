package com.datastax.astra.jetbrains

import com.datastax.astra.devops_v2.apis.DBOperationsApi
import com.datastax.astra.jetbrains.credentials.ProfileManager
import com.datastax.astra.stargate_rest_v2.apis.SchemasApi
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Nullable

import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.stargate_document_v2.apis.DocumentsApi
import com.datastax.astra.stargate_rest_v2.apis.DataApi

import java.net.URI


object AstraClient {
    lateinit var project: Project
    var accessToken: String = ""
        get() = ProfileManager.getInstance(project).activeProfile?.token.toString()

    fun dbOperationsApi(): DBOperationsApi {
        return com.datastax.astra.devops_v2.infrastructure.ApiClient(authName = "Bearer", bearerToken = accessToken)
                .createService(DBOperationsApi::class.java)
    }
    fun schemasApiForDatabase(database: Database): SchemasApi {
        val basePath = database.dataEndpointUrl.orEmpty().removeSuffix(URI(database.dataEndpointUrl).rawPath)
        return com.datastax.astra.stargate_rest_v2.infrastructure.ApiClient(
            baseUrl = basePath
        ).createService(SchemasApi::class.java)
    }
    fun dataApiForDatabase(database: Database): DataApi {
        val basePath = database.dataEndpointUrl.orEmpty().removeSuffix(URI(database.dataEndpointUrl).rawPath)
        return com.datastax.astra.stargate_rest_v2.infrastructure.ApiClient(
            baseUrl = basePath
        ).createService(DataApi::class.java)
    }
    fun documentApiForDatabase(database: Database): DocumentsApi {
        val basePath = database.dataEndpointUrl.orEmpty().removeSuffix(URI(database.dataEndpointUrl).rawPath)
        return com.datastax.astra.stargate_document_v2.infrastructure.ApiClient(
            baseUrl = basePath
        ).createService(DocumentsApi::class.java)
    }
}
