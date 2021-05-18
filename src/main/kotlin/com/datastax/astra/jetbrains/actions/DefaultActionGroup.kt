package com.datastax.astra.jetbrains.actions

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.util.CachedValueImpl

abstract class ComputableActionGroup : ActionGroup {
    constructor()
    constructor(shortName: String, popup: Boolean) : super(shortName, popup) {}

    private lateinit var children: CachedValue<Array<AnAction>>

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        if (!this::children.isInitialized) {
            children = CachedValueImpl(createChildrenProvider(e?.actionManager))
        }
        return children.value
    }

    protected abstract fun createChildrenProvider(actionManager: ActionManager?): CachedValueProvider<Array<AnAction>>
}
