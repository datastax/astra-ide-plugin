package com.datastax.astra.jetbrains.utils.editor.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ComponentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

// Dont make this dumbaware so it starts after other stuff
class FileEditorUIServiceStartupActivity : StartupActivity {
    override fun runActivity(project: Project) {
        if (ApplicationManager.getApplication().isUnitTestMode) {
            // don't create the UI when unit testing
            return
        }
        // startup the UI service
        AstraFileEditorUIService.getService(project)
    }
}
