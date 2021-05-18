package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.testFramework.LightVirtualFile
import kotlinx.coroutines.CoroutineScope

class TableVirtualFile() :
LightVirtualFile(),
CoroutineScope by ApplicationThreadPoolScope("databases")
{
}
