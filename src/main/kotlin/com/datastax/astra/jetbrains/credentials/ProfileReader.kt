package com.datastax.astra.jetbrains.credentials

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec

//TODO: Add profile validation fun and use in getting profiles
//TODO: Add watching of file

data class Profiles(val validProfiles: Map<String, Profile>)

fun validateAndGetProfiles(): Profiles{
    val profileFile = "${System.getProperty("user.home")}/.astra/config"
    val profileConfig = Config{addSpec(AstraProfileFile)}
        .from.json.file(profileFile)

    val allProfilesSet = profileConfig[AstraProfileFile.profiles]
    //Convert the set into a map
    val allProfiles = Profiles(validProfiles = allProfilesSet.associateBy({it.profile_name},{it}))

    //TODO:Validate Profiles
    /*val validProfiles = mutableMapOf<String, Profile>()
    val invalidProfiles = mutableMapOf<String, Exception>()

    allProfiles.forEach {
        try {
            validateProfile(it, allProfiles)
            validProfiles[it.name()] = it
        } catch (e: Exception) {
            invalidProfiles[it.name()] = e
        }
    }*/
    return allProfiles
}

//TODO: Create this validation function
private fun validateProfile(profile: Profile, allProfiles: Map<String, Profile>) {
}

data class Profile(
    val profile_name: String,
    val token_collection: Map<String,String>
)

data class ProfileToken(
    val name: String,
    val key: String
)

object AstraProfileFile: ConfigSpec(){
    val profiles by required<Set<Profile>>()
}