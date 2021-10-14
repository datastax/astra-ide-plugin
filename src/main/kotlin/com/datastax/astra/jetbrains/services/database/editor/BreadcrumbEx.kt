package com.datastax.astra.jetbrains.services.database.editor

import com.datastax.astra.jetbrains.utils.AstraIcons
import com.datastax.astra.stargate_document_v2.models.DocCollection
import com.datastax.astra.stargate_rest_v2.models.Table
import com.intellij.icons.AllIcons
import com.intellij.ui.components.breadcrumbs.Breadcrumbs
import com.intellij.ui.components.breadcrumbs.Crumb

class BreadcrumbsEx(databaseName: String,keyspace: String, table: String? = null, collection: String? = null, document: String? = null): Breadcrumbs() {
    val crumbs = mutableListOf<Crumb>()
    init {
        crumbs.add(Crumb.Impl(AstraIcons.IntelliJ.Dbms, databaseName.ifEmpty { "[unnamed_db]" },"", listOf()))
        crumbs.add(Crumb.Impl(AstraIcons.IntelliJ.ColBlueKeyIndex, keyspace,"", listOf()))
        when {
            table != null -> crumbs.add(Crumb.Impl(AllIcons.Nodes.DataTables, table.orEmpty().ifEmpty { "[unnamed_table]" } ,"", listOf()))
            collection != null -> crumbs.add(Crumb.Impl(AllIcons.Nodes.Folder, collection.orEmpty().ifEmpty { "[unnamed_collection]" } ,"", listOf()))
        }
        if (document != null){
            crumbs.add(Crumb.Impl(AllIcons.FileTypes.Json, "..."+document.drop(30) ,"", listOf()))
        }
        this.setCrumbs(crumbs)
    }
}