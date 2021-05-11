package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.actions.ComputableActionGroup
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.util.CachedValueProvider
import javax.swing.JComponent
import com.datastax.astra.jetbrains.credentials.ChangeProfileSettingsMode.PROFILE
import com.datastax.astra.jetbrains.credentials.ChangeProfileSettingsMode.TOKEN

class ChangeProfileSettingsActionGroup(project: Project, private val mode: ChangeProfileSettingsMode) : ComputableActionGroup(), DumbAware {
    private val profileSettingsManager = ProfileManager.getInstance(project)

    private val profileSelector = ChangeProfilesActionGroup(project)
    private val tokenSelector = ChangeTokensActionGroup(project)

    override fun createChildrenProvider(actionManager: ActionManager?): CachedValueProvider<Array<AnAction>> = CachedValueProvider {
        val actions = mutableListOf<AnAction>()

        if (mode.showProfiles) {
            actions.add(Separator.create("Astra Profiles"))
            actions.add(profileSelector)
        }
        if (mode.showTokens){
            actions.add(Separator.create("${profileSettingsManager.selectedProfile?.profile_name}'s Tokens"))
            actions.add(tokenSelector)
        }

        CachedValueProvider.Result.create(actions.toTypedArray(),profileSettingsManager)
    }
}

enum class ChangeProfileSettingsMode(
    internal val showProfiles: Boolean,
    internal val showTokens: Boolean
){
    PROFILE(true,false),
    TOKEN(false,true)
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

private class ChangeTokensActionGroup(project: Project) : ComputableActionGroup(), DumbAware {
    private val profileManager = ProfileManager.getInstance(project)
    override fun createChildrenProvider(actionManager: ActionManager?): CachedValueProvider<Array<AnAction>> =CachedValueProvider{

        val actions = mutableListOf<AnAction>()
        profileManager.tokens.forEach {
            actions.add(ChangeTokenAction(it.value))
        }
        CachedValueProvider.Result.create(actions.toTypedArray(), profileManager)
    }
}

private fun getAccountSetting(e: AnActionEvent): ProfileManager =
    ProfileManager.getInstance(e.getRequiredData(PlatformDataKeys.PROJECT))

internal class ChangeProfileAction(private val nextProfile: Profile) :
    ToggleAction(nextProfile.profile_name),
    DumbAware {
    override fun isSelected(e: AnActionEvent): Boolean = getAccountSetting(e).selectedProfile == nextProfile

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        if (state) {
            getAccountSetting(e).changeProfile(nextProfile)
        }
    }
}

internal class ChangeTokenAction(private val nextToken: ProfileToken) :
    ToggleAction(nextToken.name),
    DumbAware {
    override fun isSelected(e: AnActionEvent): Boolean = getAccountSetting(e).selectedToken == nextToken

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        if (state) {
            getAccountSetting(e).changeToken(nextToken)
        }
    }
}

class SettingsSelectorComboBoxAction(
    private val project: Project,
    private val mode: ChangeProfileSettingsMode
) : ComboBoxAction(), DumbAware {
    private val profileSettingsManager = ProfileManager.getInstance(project)

    init {
        updatePresentation(templatePresentation)
    }

    override fun createPopupActionGroup(button: JComponent?) = DefaultActionGroup(ChangeProfileSettingsActionGroup(project,mode))


    private fun updatePresentation(presentation: Presentation) {
        val (short, long) = when (mode) {
            PROFILE -> profileText()
            TOKEN -> tokenText()
        }
        presentation.text = short
        presentation.description = long
    }
    private fun profileText() = profileSettingsManager.selectedProfile?.let {
        it.profile_name to "Profile:${it.profile_name}"
    } ?: "No token selected" to null

    private fun tokenText() = profileSettingsManager.selectedToken?.let {
        "${it.name}:${it.key.takeLast(5)}" to it.key
    } ?: "No profile selected" to null

}