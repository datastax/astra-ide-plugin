package com.datastax.astra.jetbrains.utils

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.util.concurrency.AppExecutorUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlin.coroutines.CoroutineContext

fun getCoroutineUiContext(): CoroutineContext = EdtCoroutineDispatcher

private object EdtCoroutineDispatcher : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        ApplicationManager.getApplication().invokeLater(block, ModalityState.any())
    }
}

fun getCoroutineBgContext(): CoroutineContext = AppExecutorUtil.getAppExecutorService().asCoroutineDispatcher()
