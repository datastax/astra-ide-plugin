package com.datastax.astra.jetbrains

import com.datastax.astra.devops_v2.apis.OperationsApi
import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.stargate_v2.apis.SchemasApi
import java.net.URI

object AstraClient {
    val accessToken =
        "AstraCS:fdtRtnwsaJDTNPhJXOROpyeh:9cdb34db92c7e886cdeebf047b33d859f29938c69954d8f0db496253884b7ce5"

    fun operationsApi(): OperationsApi {
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
