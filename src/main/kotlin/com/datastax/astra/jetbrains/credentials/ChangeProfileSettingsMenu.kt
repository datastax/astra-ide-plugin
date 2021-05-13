package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.actions.ComputableActionGroup
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.util.CachedValueProvider
import javax.swing.JComponent

class ChangeProfileSettingsActionGroup(project: Project) : ComputableActionGroup(), DumbAware {
    private val profileSettingsManager = ProfileManager.getInstance(project)

    private val profileSelector = ChangeProfilesActionGroup(project)

    //TODO: Figure out why default isn't always at the top
    override fun createChildrenProvider(actionManager: ActionManager?): CachedValueProvider<Array<AnAction>> = CachedValueProvider {
        val actions = mutableListOf<AnAction>()

        actions.add(Separator.create("Astra Profiles"))
        actions.add(profileSelector)
        actions.add(Separator.create())
        actions.add(ActionManager.getInstance().getAction("credentials.upsertCredentials"))

        CachedValueProvider.Result.create(actions.toTypedArray(),profileSettingsManager)
    }
}

private class ChangeProfilesActionGroup(project: Project) : ComputableActionGroup(), DumbAware {
    private val profileManager = ProfileManager.getInstance(project)
    override fun createChildrenProvider(actionManager: ActionManager?): CachedValueProvider<Array<AnAction>> =CachedValueProvider{

        val actions = mutableListOf<AnAction>()
        profileManager.profiles.forEach {
            actions.add(ChangeProfileAction(it.value))
        }
        CachedValueProvider.Result.create(actions.toTypedArray(), profileManager)
    }
}

private fun getAccountSetting(e: AnActionEvent): ProfileManager =
    ProfileManager.getInstance(e.getRequiredData(PlatformDataKeys.PROJECT))

internal class ChangeProfileAction(private val nextProfile: ProfileToken) :
    ToggleAction(nextProfile.name),
    DumbAware {
    override fun isSelected(e: AnActionEvent): Boolean = getAccountSetting(e).selectedProfile == nextProfile

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        if (state) {
            getAccountSetting(e).changeProfile(nextProfile)
        }
    }
}

class SettingsSelectorComboBoxAction(
    private val project: Project,
) : ComboBoxAction(), DumbAware {
    private val profileSettingsManager = ProfileManager.getInstance(project)

    init {
        updatePresentation(templatePresentation)
    }

    override fun createPopupActionGroup(button: JComponent?) = DefaultActionGroup(ChangeProfileSettingsActionGroup(project))

    private fun updatePresentation(presentation: Presentation) {
        val (short, long) = profileSettingsManager.selectedProfile?.let {
            it.name to "Profile:${it.name}"
        } ?: "No token selected" to null
        presentation.text = short
        presentation.description = long
    }
}