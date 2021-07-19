package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.stargate_rest_v2.models.Table
import com.intellij.testFramework.LightVirtualFile
import kotlinx.coroutines.CoroutineScope

class TableVirtualFile(val table: Table, val database: Database) :
    LightVirtualFile(table?.name.orEmpty()),
    CoroutineScope by ApplicationThreadPoolScope("Table")
