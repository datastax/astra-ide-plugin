package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.models.AvailableRegionCombination
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.credentials.ProfileManager
import com.intellij.openapi.project.Project
import kotlinx.coroutines.runBlocking
/*
Gets the list of available regions for the current profile
The majority of this class exists as a companion object
This can probably be slimmed down since anytime the profile is changed this class will be re-initialized anyways
Contains a blocking function for updating the regions
 */
class CreateDatabaseGetRegions(project: Project) {
    companion object {
        lateinit var currentProject: Project
        var recentProfile: String = ""
        lateinit var regionsList: List<AvailableRegionCombination>

        fun getRegions(): List<AvailableRegionCombination> {
            val activeProfile = ProfileManager.getInstance(AstraClient.project).activeProfile?.name.toString()
            if (recentProfile == activeProfile) {
                return regionsList
            }
            recentProfile = activeProfile
            updateRegions()
            return regionsList
        }

        fun updateRegions() {
            runBlocking { regionsList = AstraClient.dbOperationsApi().listAvailableRegions().body().orEmpty() }
        }
    }

    init {
        currentProject = project
        recentProfile = ProfileManager.getInstance(AstraClient.project).activeProfile?.name.toString()
        updateRegions()
    }
}
