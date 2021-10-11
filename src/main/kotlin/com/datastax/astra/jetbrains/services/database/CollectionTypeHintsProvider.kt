package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.jetbrains.services.database.editor.CollectionVirtualFile
import com.datastax.astra.jetbrains.services.database.editor.DocumentManager
import com.datastax.astra.jetbrains.services.database.editor.DocumentVirtualFile
import com.intellij.codeInsight.hints.*
import com.intellij.codeInsight.hints.presentation.MouseButton
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.intellij.ui.layout.panel
import javax.swing.JComponent

class CollectionTypeHintsProvider : InlayHintsProvider<NoSettings> {

    /**
     * Used for persistence of settings
     */
    override val key: SettingsKey<NoSettings> = SettingsKey<NoSettings>("astra.collection.hints")

    /**
     * Name of this kind of hints. It will be used in settings and in context menu.
     * Please, do not use word "hints" to avoid duplication
     */
    override val name: String = "Collection Documents"

    /**
     * Text, that will be used in the settings as a preview
     */
    override val previewText: String? = null

    /**
     * Settings must be plain java object, fields of this settings will be copied via serialization.
     * Must implement equals method, otherwise settings won't be able to track modification.
     * Returned object will be used to create configurable and collector.
     * It persists automatically.
     */
    override fun createSettings(): NoSettings = NoSettings()

    /**
     * Creates configurable, that immediately applies changes from UI to [settings]
     */
    override fun createConfigurable(settings: NoSettings): ImmediateConfigurable = object : ImmediateConfigurable {
        override fun createComponent(listener: ChangeListener): JComponent = panel {}
    }


    /**
     * If this method is called, provider is enabled for this file
     * Warning! Your collector should not use any settings besides [settings]
     */
    override fun getCollectorFor(
        file: PsiFile,
        editor: Editor,
        settings: NoSettings,
        sink: InlayHintsSink
    ): InlayHintsCollector? {
        if (file.virtualFile is CollectionVirtualFile) {
            return object : FactoryInlayHintsCollector(editor) {
                /**
                 * Explores [element] and adds some hints to [sink] if necessary.
                 * Implementors must handle dumb mode themselves.
                 * Runs inside read action
                 * @return false if it is not necessary to traverse child elements (but implementors should not rely on it)
                 */
                override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
                    when (element) {
                        is JsonFile -> {
                            if (element.topLevelValue is JsonObject) {
                                val docs = (element.topLevelValue as JsonObject).propertyList
                                docs.forEach { jsonProperty ->

                                    val link = factory.onClick(
                                        factory.smallTextWithoutBackground("Edit"), MouseButton.Left
                                    ) { _, _ ->
                                        (file.virtualFile as CollectionVirtualFile).let {
                                            PsiDocumentManager.getInstance(file.project).getDocument(file)
                                                ?.let { collectionDoc ->
                                                    val fragment = collectionDoc.charsSequence.subSequence(
                                                        jsonProperty.lastChild.startOffset,
                                                        jsonProperty.lastChild.endOffset
                                                    )
                                                    val documentFile = DocumentVirtualFile(
                                                        it.database,
                                                        it.keyspaceName,
                                                        it.collectionName,
                                                        (jsonProperty.firstChild as JsonStringLiteral).value,
                                                        fragment.toString()
                                                    )
                                                    DocumentManager.openEditor(file.project, documentFile)
                                                }
                                        }
                                    }
                                    sink.addInlineElement(
                                        jsonProperty.startOffset, false, link, true
                                    )
                                }
                            }
                            return false
                        }
                        else -> {
                            return true
                        }
                    }
                }
            }
        } else return null
    }
}
