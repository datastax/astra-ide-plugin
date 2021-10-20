package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.telemetry.ClickTarget
import com.datastax.astra.jetbrains.telemetry.TelemetryManager.trackClick
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import org.jetbrains.annotations.TestOnly
import java.io.File

// Not sure why constructor is test only
class CreateOrUpdateProfilesFileAction @TestOnly constructor(
    private val writer: ConfigFileWriter,
    private val configFile: File
) : AnAction(message("credentials.file.edit")), DumbAware {
    @Suppress("unused")
    constructor() : this(
        DefaultConfigFileWriter,
        ProfileFileLocation.profileFilePath()
    )

    private val localFileSystem = LocalFileSystem.getInstance()

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getRequiredData(PlatformDataKeys.PROJECT)

        // if config does not exist, (try to) create a new config file
        if (!configFile.exists()) {
            if (confirm(project, configFile)) {
                trackClick(ClickTarget.BUTTON, "create profile file")
                try {
                    writer.createFile(configFile)
                } finally {
                }
            } else {
                return
            }
        }

        // This was a list of two file types changed to one, since we only have one profile file
        val virtualFiles = listOf(configFile).filter { it.exists() }.map {
            localFileSystem.refreshAndFindFileByIoFile(it) ?: throw RuntimeException(message("credentials.file.could_not_open", it))
        }

        val fileEditorManager = FileEditorManager.getInstance(project)

        localFileSystem.refreshFiles(virtualFiles, false, false) {
            virtualFiles.forEach {
                if (it.fileType == FileTypes.UNKNOWN) {
                    ApplicationManager.getApplication().runWriteAction {
                        FileTypeManagerEx.getInstanceEx().associatePattern(
                            FileTypes.PLAIN_TEXT,
                            it.name
                        )
                    }
                }

                if (fileEditorManager.openTextEditor(OpenFileDescriptor(project, it), true) == null) {
                    throw RuntimeException(message("credentials.file.could_not_open", it))
                }
            }
        }
    }

    private fun confirm(project: Project, file: File): Boolean = Messages.showOkCancelDialog(
        project,
        message("credentials.file.confirm_create", file),
        message("credentials.file.confirm_create.title"),
        message("credentials.file.confirm_create.okay"),
        Messages.getCancelButton(),
        AllIcons.General.QuestionDialog,
        null
    ) == Messages.OK

    fun createWithGenToken(project: Project, token: String) {
        DefaultConfigFileWriter.token = token
        // if config does not exist, (try to) create a new config file
        if (!configFile.exists()) {
            try {
                writer.createFile(configFile)
            } finally {
                trackClick(ClickTarget.BUTTON, "create profile file")
            }
        }

        // Reset so token isnt lingering somewhere
        DefaultConfigFileWriter.token = ""

        // This was a list of two file types changed to one, since we only have one profile file
        val virtualFiles = listOf(configFile).filter { it.exists() }.map {
            localFileSystem.refreshAndFindFileByIoFile(it) ?: throw RuntimeException(message("credentials.file.could_not_open", it))
        }

        val fileEditorManager = FileEditorManager.getInstance(project)

        localFileSystem.refreshFiles(virtualFiles, false, false) {
            virtualFiles.forEach {
                if (it.fileType == FileTypes.UNKNOWN) {
                    ApplicationManager.getApplication().runWriteAction {
                        FileTypeManagerEx.getInstanceEx().associatePattern(
                            FileTypes.PLAIN_TEXT,
                            it.name
                        )
                    }
                }

                if (fileEditorManager.openTextEditor(OpenFileDescriptor(project, it), true) == null) {
                    throw RuntimeException(message("credentials.file.could_not_open", it))
                }
            }
        }
    }
}

interface ConfigFileWriter {
    fun createFile(file: File)
}

object DefaultConfigFileWriter : ConfigFileWriter {
    internal var token = ""
    val TEMPLATE =
        """
        # Astra Config File used by DataStax Astra tools
        # This file was created by the Astra Explorer for JetBrains plugin.
        #
        # Your Astra credentials are represented by application tokens associated with a user
        # role. For information about how to create and manage DataStax Astra tokens, see:
        # https://docs.datastax.com/en/astra/docs/manage-application-tokens.html
        #
        # This config file can store multiple tokens by placing each one in a uniquely 
        # named "profile". For information about permissions for default user roles, see:
        # https://docs.datastax.com/en/astra/docs/user-permissions.html
        # For information about how to create custom roles with different permissions, see: 
        # https://docs.datastax.com/en/astra/docs/manage-custom-user-roles.html

        # The application token identifies your user role and grants access to DataStax Astra
        # based on the permissions associated with that role.
        # Treat your application token like a password. Never share your token with anyone. Do
        # not post it in online forums, or store it in a source control system. If your token
        # is ever disclosed, immediately use the Token Management interface in Organization 
        # Settings to delete the token, or inform your organization administrator and request a 
        # new token. Then, update this file with the replacement token.
        
        # NOTE: This file must be saved for modifications to be read! (Ctrl+S)
        # NOTE: Profile names must be unique and contain no spaces!
        
        [astraProfileFile.profiles]
        # default profile is loaded on plugin startup
        default = "bearertoken"
        # profile_name = "applicationToken"
        # "profile with spaces" = "applicationToken2"
        """.trimIndent()

    override fun createFile(file: File) {
        val parent = file.parentFile
        if (parent.mkdirs()) {
            parent.setReadable(false, false)
            parent.setReadable(true)
            parent.setWritable(false, false)
            parent.setWritable(true)
            parent.setExecutable(false, false)
            parent.setExecutable(true)
        }
        if (token == "") {
            file.writeText(TEMPLATE)
        } else {
            val modifyTemplate = TEMPLATE.replace("bearertoken", token)
            file.writeText(modifyTemplate)
        }

        file.setReadable(false, false)
        file.setReadable(true)
        file.setWritable(false, false)
        file.setWritable(true)
        file.setExecutable(false, false)
        file.setExecutable(false)
    }
}
