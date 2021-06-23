package com.datastax.astra.jetbrains.credentials

import java.io.File
import java.nio.file.Path

class ProfileFileLocation {
    companion object{
        fun profileFilePath(): File{
            return File("${System.getProperty("user.home")}/.astra/config")
        }
    }
}
