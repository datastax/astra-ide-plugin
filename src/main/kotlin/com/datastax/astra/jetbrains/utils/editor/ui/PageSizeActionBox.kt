package com.datastax.astra.jetbrains.utils.editor.ui

import com.datastax.astra.jetbrains.actions.ComputableActionGroup
import com.datastax.astra.jetbrains.services.database.CollectionVirtualFile
import com.datastax.astra.jetbrains.services.database.TableVirtualFile
import com.datastax.astra.jetbrains.utils.editor.PagedVirtualFile
import com.datastax.astra.jetbrains.utils.getCoroutineUiContext
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.util.CachedValueProvider
import javax.swing.JComponent

class SetPageSizeComboBoxAction(
    private val project: Project,
    private val file: PagedVirtualFile,
) : ComboBoxAction(), DumbAware {
    val collectionPageSizeOptions = intArrayOf(1, 5, 10, 20)
    val tablePageSizeOptions = intArrayOf(10, 20, 50, 100)
    var customSelected = false

    init {
        updatePresentation(templatePresentation)
    }

    override fun update(e: AnActionEvent) {
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        if (virtualFile is PagedVirtualFile) {
            if (virtualFile.isLocked()) {
                loadingPresentation(e.presentation)
            } else {
                e.presentation.isEnabled = true
                updatePresentation(e.presentation)
            }
        } else {
            e.presentation.isEnabled = false
        }
    }

    override fun useSmallerFontForTextInToolbar() = true

    override fun getMaxRows(): Int {
        return 7
    }

    override fun createPopupActionGroup(button: JComponent?): DefaultActionGroup {
        return DefaultActionGroup(SetPageSizeActionGroup(project, file))
    }

    private fun loadingPresentation(presentation: Presentation) {
        presentation.isEnabled = false
        presentation.description = "Loading pages..."
    }

    private fun updatePresentation(presentation: Presentation) {
        when (file) {
            is CollectionVirtualFile -> {
                presentation.text = if (file.pageSize > 1) {
                    "${file.pageSize} docs"
                } else {
                    "${file.pageSize} doc"
                }
                presentation.description = "Page Size: ${presentation.text}"
            }
            is TableVirtualFile -> {
                presentation.text = if (file.pageSize > 1) {
                    "${file.pageSize} rows"
                } else {
                    "${file.pageSize} row"
                }
                presentation.description = "Page Size: ${presentation.text}"
            }
            else -> {
                presentation.text = if (file.pageSize > 1) {
                    "${file.pageSize} elements"
                } else {
                    "${file.pageSize} element"
                }
                presentation.description = "Page Size: ${presentation.text}"
            }
        }
        // TODO: Ask if there's something better to set as the description?
    }

    inner class SetPageSizeActionGroup(val project: Project, val file: PagedVirtualFile) : ComputableActionGroup(),
        DumbAware {

        override fun createChildrenProvider(actionManager: ActionManager?): CachedValueProvider<Array<AnAction>> =
            CachedValueProvider {

                val actions = mutableListOf<AnAction>()
                // Can check this inside the for loop but that would check the file type each loop
                actions.add(Separator.create("Page Size"))
                when (file) {
                    is CollectionVirtualFile -> {
                        for (pageSize in collectionPageSizeOptions) {
                            actions.add(SetPageSizeAction(pageSize))
                        }
                    }
                    is TableVirtualFile -> {
                        for (pageSize in tablePageSizeOptions) {
                            actions.add(SetPageSizeAction(pageSize))
                        }
                    }
                }
                actions.add(SetPageSizeAllAction())
                actions.add(Separator.create())
                actions.add(CustomPageSizeAction())
                CachedValueProvider.Result.create(actions.toTypedArray(), file)
            }
    }

    inner class SetPageSizeAction(val pageSize: Int) :
        ToggleAction("$pageSize"),
        DumbAware {
        override fun isSelected(e: AnActionEvent): Boolean = !customSelected && (file.pageSize == pageSize)

        override fun setSelected(e: AnActionEvent, state: Boolean) {
            if (state) {
                customSelected = false
                file.setVirtualPageSize(pageSize)
            }
        }
    }

    inner class SetPageSizeAllAction :
        ToggleAction("All"),
        DumbAware {
        override fun isSelected(e: AnActionEvent): Boolean =
            //Only show All selected if it's not a possible choice in the default choices or a custom choice
            file.pageSize == file.getSize() && !customSelected &&
                    when (file) {
                        is CollectionVirtualFile -> {
                            !collectionPageSizeOptions.contains(file.pageSize)
                        }
                        is TableVirtualFile -> {
                            !tablePageSizeOptions.contains(file.pageSize) && file.pageSize == file.getSize()
                        }
                        else -> false
                    }

        override fun setSelected(e: AnActionEvent, state: Boolean) {
            if (state) {
                customSelected = false
                file.setVirtualPageSize(file.getSize())
            }
        }
    }

    inner class CustomPageSizeAction() :
        //If custom size being used show the size in the dropdown
        ToggleAction("Custom${if(customSelected) ": ${file.pageSize}" else ""}"),
        DumbAware {
        override fun isSelected(e: AnActionEvent): Boolean = customSelected

        override fun beforeActionPerformedUpdate(e: AnActionEvent) {
            this.isEnabledInModalContext = true
        }

        //This is called every click, if you only want to perform the related behavior check the state as above.
        override fun setSelected(e: AnActionEvent, state: Boolean) {
                customSelected = CustomPageSizeDialog(project, file).showAndGet()
        }
    }
}


