package com.datastax.astra.jetbrains.credentials

import java.io.File

class ProfileFileLocation {
    companion object {
        fun profileFilePath(): File {
            return File("${System.getProperty("user.home")}/.astra/config")
        }
    }
}
