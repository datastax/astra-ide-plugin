package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.credentials.ProfileFileLocation.Companion.profileFilePath
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.KWatchChannel
import com.datastax.astra.jetbrains.utils.KWatchEvent
import com.datastax.astra.jetbrains.utils.asWatchChannel
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.source.toml
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.tools.ant.taskdefs.Execute.launch
import java.io.File
import java.io.FileNotFoundException

object ProfileReader : CoroutineScope by ApplicationThreadPoolScope("Credentials") {
    var validProfiles = mutableMapOf<String, ProfileToken>()
    var invalidProfiles = mutableMapOf<String, Exception>()
    lateinit var profileFile: File

    fun validateAndGetProfiles(): Profiles {
        validProfiles.clear()
        invalidProfiles.clear()

        try {
            // TODO: Probably a more elegant way to achieve this
            profileFile = profileFilePath()
            if (!profileFile.exists()) {
                throw FileNotFoundException("astra config file not found")
            }

            startFileWatcher()

            validateProfileFile()[AstraProfileFile.profiles]
                .forEach {
                    try {
                        // Go through each map entry and remap it to map of valid profiles if it can make simple rest call
                        validateProfile(it.value)
                        validProfiles[it.key] = ProfileToken(it.key, it.value)
                    } catch (e: Exception) {
                        invalidProfiles[it.key] = e
                    }
                }
        } catch (e: FileNotFoundException) {
            noProfilesFileNotification()
        } catch (e: Exception) {
            // TODO: Pass the exception to notify the user what line the error occurred on
            wrongProfilesFormatNotification()
        }

        if (invalidProfiles.isNotEmpty()) {
            invalidProfilesNotification(invalidProfiles)
        }

        // Return profile map, empty if none validated
        return Profiles(validProfiles)
    }

    private fun startFileWatcher() {
        val watchChannel = profileFile.asWatchChannel(KWatchChannel.Mode.SingleFile, scope = ApplicationThreadPoolScope("Credentials"))
        var fileChangeTriggered = false
        launch {
            watchChannel.consumeEach { event ->
                // Only send the message when the file is modified
                if (event.kind == KWatchEvent.Kind.Modified) {
                    if (!fileChangeTriggered) {
                        profileFileModifiedNotification()
                        fileChangeTriggered = true
                    }
                }
            }
        }
    }

    private fun validateProfileFile(): Config =
        // Check that file exists
        if (profileFile.exists()) {
            // Throws?
            Config { addSpec(AstraProfileFile) }
                .from.toml.file(profileFile)
        } else {
            throw FileNotFoundException("astra config file not found")
        }

    private fun validateProfile(token: String) {
        // Check that token is right format
        if (token.length == 97) {
            token.split(":").forEachIndexed { index, element ->
                when (index) {
                    0 -> if (element != "AstraCS") throw Exception("TokenWrongFormat")
                    1 -> if (!(element.length == 24 || element.all { it.isLetterOrDigit() })) throw Exception("WrongTokenFormat")
                    2 -> if (!(element.length == 64 || element.all { it.isLetterOrDigit() })) throw Exception("WrongTokenFormat")
                    else -> {
                        throw Exception("TokenWrongFormat")
                    }
                }
            }
        } else {
            throw Exception("TokenWrongFormat")
        }

        // If token has valid format check if it works on the wire
        runBlocking {
            if (!CredentialsClient.operationsApi(token).getCurrentOrganization().isSuccessful)
                throw Exception("TokenAuthFailed")
        }
    }
}

data class ProfileToken(
    val name: String,
    val token: String
)

data class Profiles(val validProfiles: Map<String, ProfileToken>)

object AstraProfileFile : ConfigSpec() {
    val profiles by required<Map<String, String>>()
}
