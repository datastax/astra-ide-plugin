package com.datastax.astra.jetbrains.services.database.editor

import com.datastax.astra.jetbrains.explorer.TableEndpoint
import com.intellij.testFramework.LightVirtualFile

class TableVirtualFile(val endpoint: TableEndpoint) :
    LightVirtualFile(endpoint.table?.name.orEmpty())
