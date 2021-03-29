package com.github.gconaty.intellijplugintemplate.services

import com.github.gconaty.intellijplugintemplate.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
