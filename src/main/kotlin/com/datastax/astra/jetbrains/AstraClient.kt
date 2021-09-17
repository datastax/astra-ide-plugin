package com.datastax.astra.jetbrains

import com.datastax.astra.devops_v2.apis.DBOperationsApi
import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.jetbrains.credentials.ProfileManager
import com.datastax.astra.jetbrains.utils.AstraClientBase
import com.datastax.astra.stargate_document_v2.apis.DocumentsApi
import com.datastax.astra.stargate_rest_v2.apis.DataApi
import com.datastax.astra.stargate_rest_v2.apis.SchemasApi
import com.intellij.openapi.project.Project
import java.net.URI
import com.datastax.astra.stargate_document_v2.apis.SchemasApi as SchemasApiDoc

object AstraClient: AstraClientBase() {
    lateinit var project: Project
    override var accessToken: String = ""
        get() = ProfileManager.getInstance(project).activeProfile?.token.toString()

}
