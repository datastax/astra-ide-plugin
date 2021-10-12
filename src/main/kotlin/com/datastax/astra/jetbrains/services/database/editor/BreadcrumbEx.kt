package com.datastax.astra.jetbrains.services.database.editor

import com.datastax.astra.jetbrains.utils.AstraIcons
import com.datastax.astra.stargate_document_v2.models.DocCollection
import com.datastax.astra.stargate_rest_v2.models.Table
import com.intellij.icons.AllIcons
import com.intellij.ui.components.breadcrumbs.Breadcrumbs
import com.intellij.ui.components.breadcrumbs.Crumb

class BreadcrumbsEx(databaseName: String,keyspace: String, collection: DocCollection? = null,table: Table? = null): Breadcrumbs() {
    val crumbs: List<Crumb>
    init {

        crumbs = listOf(
            Crumb.Impl(AstraIcons.IntelliJ.Dbms, databaseName.ifEmpty { "[unnamed_db]" },"", listOf()),
            Crumb.Impl(AstraIcons.IntelliJ.ColBlueKeyIndex, keyspace,"", listOf()),
            if(collection == null){
                Crumb.Impl(AllIcons.Nodes.Folder, table?.name.orEmpty().ifEmpty { "[unnamed_collection]" } ,"", listOf())
            }else{
                Crumb.Impl(AllIcons.Nodes.Folder, collection?.name.orEmpty().ifEmpty { "[unnamed_collection]" } ,"", listOf())
            }
        )
        this.setCrumbs(crumbs)
    }
}