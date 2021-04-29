package com.datastax.astra.jetbrains.utils

import com.intellij.application.ApplicationThreadPool
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

class ApplicationThreadPoolScope(coroutineName: String) : CoroutineScope {
    override val coroutineContext: CoroutineContext = /*SupervisorJob()*/ /*+ Dispatchers.ApplicationThreadPool + */CoroutineName(coroutineName)
}
