package com.datastax.astra.jetbrains.credentials

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

//Not sure why constructor is test only
class CreateOrUpdateProfilesFileAction @TestOnly constructor(
    private val writer: ConfigFileWriter,
    private val configFile: File
) : AnAction("Edit DataStax Profiles File"), DumbAware {
    @Suppress("unused")
    constructor() : this(
        DefaultConfigFileWriter,
        ProfileFileLocation.profileFilePath().toFile()
    )

    private val localFileSystem = LocalFileSystem.getInstance()

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getRequiredData(PlatformDataKeys.PROJECT)

        // if both config does not exist, (try to)create a new config file
        if (!configFile.exists()) {
            if (confirm(project, configFile)) {
                try {
                    writer.createFile(configFile)
                } finally {
                    //This had AWS telemetry in it
                }
            } else {
                return
            }
        }

        // open both config and credential files, if they exist
        // credential file is opened last since it takes precedence over the config file
        val virtualFiles = listOf(configFile).filter { it.exists() }.map {
            localFileSystem.refreshAndFindFileByIoFile(it) ?: throw RuntimeException("credentials.could_not_open $it")
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
                    throw RuntimeException("credentials.could_not_open $it")

                }

            }
        }
    }

    private fun confirm(project: Project, file: File): Boolean = Messages.showOkCancelDialog(
        project,
        "Credentials file $file does not exist. Create it?",
        "Create Credentials File",
        "Create",
        Messages.getCancelButton(),
        AllIcons.General.QuestionDialog,
        null
    ) == Messages.OK
}

interface ConfigFileWriter {
    fun createFile(file: File)
}

object DefaultConfigFileWriter : ConfigFileWriter {
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
        
        [astraProfileFile.profiles]
        # default profile is loaded on plugin startup
        default = "bearertoken"
        # profile_name = "applicationToken"
        # profile2_name = "applicationToken2"
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

        file.writeText(TEMPLATE)

        file.setReadable(false, false)
        file.setReadable(true)
        file.setWritable(false, false)
        file.setWritable(true)
        file.setExecutable(false, false)
        file.setExecutable(false)
    }
}
