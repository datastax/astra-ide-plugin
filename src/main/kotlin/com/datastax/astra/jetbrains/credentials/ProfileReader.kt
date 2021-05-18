package com.datastax.astra.jetbrains.credentials

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.source.toml
import java.io.File
import java.io.FileNotFoundException

//TODO: Seperate invalid profile message into two: Authentication and Format
//TODO: Add checking authorization to profile validation
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
private fun validateProfile(token: String) {
    //Check that token is right format
    if(token.length==97)
        token.split(":").forEachIndexed{ index, element->
            when (index) {
                0 -> if (element != "AstraCS") throw Exception("WrongTokenFormat")
                1 -> if (!(element.length == 24 || element.all{it.isLetterOrDigit()})) throw Exception("WrongTokenFormat")
                2 -> if (!(element.length == 64 || element.all{it.isLetterOrDigit()})) throw Exception("WrongTokenFormat")
                else -> {throw Exception("WrongTokenFormat")}
            }
        }
    else
        throw Exception("WrongTokenLength")

    //TODO: Check that token can hit DataStax getOrgID on wire
    //TODO: Implement once this method is added to client
    //CredentialsClient.operationsApi(token).GETORGIDMETHOD()
}

data class ProfileToken(
    val name: String,
    val token: String
)

object AstraProfileFile: ConfigSpec(){
    val profiles by required<Map<String,String>>()
}