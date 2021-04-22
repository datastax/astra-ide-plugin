rootProject.name = "intellij-plugin-template"

include("devops_v2")
include("stargate_v2")
project(":devops_v2").projectDir = file("api/devops_v2")
project(":stargate_v2").projectDir = file("api/stargate_v2")
