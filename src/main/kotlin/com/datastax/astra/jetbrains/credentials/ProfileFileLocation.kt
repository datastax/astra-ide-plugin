package com.datastax.astra.jetbrains.credentials

import java.io.File
import java.nio.file.Path

class ProfileFileLocation {
    companion object{
        fun profileFilePath(): Path{
            return File("${System.getProperty("user.home")}/.astra/config").toPath()
        }
    }
}
