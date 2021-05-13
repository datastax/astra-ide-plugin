package com.datastax.astra.jetbrains.credentials

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.source.toml
import java.io.File
import java.io.FileNotFoundException

//import com.datastax.astra.jetbrains.credentials.ProfileFileLocation

//TODO: Seperate invalid profile message into two: Authentication and Format
//TODO: Add profile validation fun and use in getting profiles
//TODO: Add watching of file

data class Profiles(val validProfiles: Map<String, ProfileToken>)

fun validateAndGetProfiles(): Profiles{
    val validProfiles = mutableMapOf<String, ProfileToken>()
    val invalidProfiles = mutableMapOf<String, Exception>()

    try{
        validateProfileFile(ProfileFileLocation.profileFilePath().toFile())[AstraProfileFile.profiles]
        .forEach {
            try {
                //Go through each map entry and remap it to map of valid profiles if it can make simple rest call
                validateProfile(it.value)
                validProfiles[it.key] = ProfileToken(it.key, it.value)
            } catch (e: Exception) {
                invalidProfiles[it.key] = e
            }
        }
    } catch (e: FileNotFoundException){
        noProfilesFileNotification()
    } catch (e: Exception){
        //TODO: Pass the exception to notify the user what line the error occured on
        wrongProfilesFormatNotification()
    }

    if(invalidProfiles.isNotEmpty())
        invalidProfilesNotification(invalidProfiles)

    //TODO: Add "Empty profile file" notification

    //Return empty profile map if none validate
    return Profiles(validProfiles)
}

private fun validateProfileFile(profileFile: File): Config =
    //Check that file exists
    if (profileFile.exists()){
        //Throws?
        Config { addSpec(AstraProfileFile) }
            .from.toml.file(profileFile)
    }
    else
        throw FileNotFoundException("astra config file not found")



//TODO: Create this validation function
//TODO: Call dialog for each of the failed checks
private fun validateProfile(profileToken: String) {
    //TODO: Check that token is right format
    //TODO: Check that token can hit DataStax getOrgID on wire
}

data class ProfileToken(
    val name: String,
    val token: String
)

object AstraProfileFile: ConfigSpec(){
    val profiles by required<Map<String,String>>()
}