import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.changelog.closure
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.4.32"
    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "0.7.2"
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "1.1.2"
    // detekt linter - read more: https://detekt.github.io/detekt/gradle.html
    id("io.gitlab.arturbosch.detekt") version "1.16.0"
    // ktlint linter - read more: https://github.com/JLLeitschuh/ktlint-gradle
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
    //from: https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-gradle-plugin
    //id( "org.openapi.generator") version "5.1.0"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure project's dependencies
repositories {
    mavenCentral()
    jcenter()
}
dependencies {
    val kotlin_version ="1.3.61"
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.16.0")
    /*implementation( "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation( "org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
    implementation( "com.squareup.moshi:moshi-kotlin:1.11.0")*/
    implementation( "com.squareup.okhttp3:okhttp:4.9.0")
}

sourceSets {
    main {
        //java.srcDirs("$rootDir/gen/openapistubs/src/main")
        java.srcDir("gen/devops_v2/src/main")
    }
    test {
        //java.srcDirs("$rootDir/gen/openapistubs/src/main")
        java.srcDir("gen/devops_v2/src/main")
    }
}

// Configure gradle-intellij-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    pluginName = properties("pluginName")
    version = properties("platformVersion")
    type = properties("platformType")
    downloadSources = properties("platformDownloadSources").toBoolean()
    updateSinceUntilBuild = true

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    setPlugins(*properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty).toTypedArray())
}

// Configure gradle-changelog-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    version = properties("pluginVersion")
    groups = emptyList()
}

// Configure detekt plugin.
// Read more: https://detekt.github.io/detekt/kotlindsl.html
detekt {
    config = files("./detekt-config.yml")
    buildUponDefaultConfig = true

    reports {
        html.enabled = false
        xml.enabled = false
        txt.enabled = false
    }
}

/*
//This defines the generator
openApiGenerate {
    generatorName.set("kotlin")
    inputSpec.set("$rootDir/src/main/resources/apis/devops_v2_openapi.json".toString())
    //This is a randomly choosen location and can (probably should be) be changed
    outputDir.set("$rootDir/gen/openapistubs".toString())
    //Might need to change this to a different package
    apiPackage.set("org.openapi.example.api")
    invokerPackage.set("org.openapi.example.invoker")
    modelPackage.set("org.openapi.example.model")
    configOptions.set(mapOf(
        "dateLibrary" to "java8",
        "library" to "jvm-okhttp4"
    ))
    //Turn off debug in production
    globalProperties.set(mapOf(
        "debugOpenAPI" to "true"))
    logToStderr.set(true)
    generateAliasAsModel.set(true)
    // set to true and set environment variable {LANG}_POST_PROCESS_FILE
    // (e.g. SCALA_POST_PROCESS_FILE) to the linter/formatter to be processed.
    // This command will be passed one file at a time for most supported post processors.
    enablePostProcessFile.set(false)
}
*/


tasks {
    //This calls the generator
    //val openApiGenerate by getting

    // Set the compatibility versions to 1.8
    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
        //Don't run this until the files are generated
        //dependsOn(openApiGenerate)
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.useIR = true
    }

    withType<Detekt> {
        jvmTarget = "1.8"
    }

    patchPluginXml {
        version(properties("pluginVersion"))
        sinceBuild(properties("pluginSinceBuild"))
        untilBuild(properties("pluginUntilBuild"))

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription(
            closure {
                File(projectDir, "README.md").readText().lines().run {
                    val start = "<!-- Plugin description -->"
                    val end = "<!-- Plugin description end -->"

                    if (!containsAll(listOf(start, end))) {
                        throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                    }
                    subList(indexOf(start) + 1, indexOf(end))
                }.joinToString("\n").run { markdownToHTML(this) }
            }
        )

        // Get the latest available change notes from the changelog file
        changeNotes(
            closure {
                changelog.getLatest().toHTML()
            }
        )
    }

    runPluginVerifier {
        ideVersions(properties("pluginVerifierIdeVersions"))
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token(System.getenv("PUBLISH_TOKEN"))
        // pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels(properties("pluginVersion").split('-').getOrElse(1) { "default" }.split('.').first())
    }
    //Trying to use openapigenerate function to create rest stubs
}
