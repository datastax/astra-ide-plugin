package com.datastax.astra.jetbrains.utils

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.AppUIExecutor
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.impl.coroutineDispatchingContext

fun getCoroutineUiContext(
    modalityState: ModalityState = ModalityState.defaultModalityState(),
    disposable: Disposable? = null
) = AppUIExecutor.onUiThread(modalityState).let {
    if (disposable == null) {
        it
    } else {
        // This is not actually scheduled for removal in 2019.3
        it.expireWith(disposable)
    }
}.coroutineDispatchingContext()
