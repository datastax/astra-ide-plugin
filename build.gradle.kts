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
    id("org.jetbrains.intellij") version "0.7.3"
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "1.1.2"
    // detekt linter - read more: https://detekt.github.io/detekt/gradle.html
    id("io.gitlab.arturbosch.detekt") version "1.16.0"
    // ktlint linter - read more: https://github.com/JLLeitschuh/ktlint-gradle
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure project's dependencies
repositories {
    mavenCentral()
    jcenter()
}

sourceSets {
    main {
        java {
            srcDir("api/devops_v2/src/main")
            srcDir("api/stargate_v2/src/main")
        }
    }
}

val retrofitVersion = "2.7.2"
dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.16.0")

    //Dependencies for OpenApi clients
    //Must use the older versions that have a runtime dependency on the kotlin-stdlib:1.3.70
    //or have issues with Linkage errors etc since IntelliJ ships with older kotlin libs
    implementation("com.squareup.moshi:moshi-kotlin:1.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.8.1")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-scalars:$retrofitVersion")

    implementation("com.github.ben-manes.caffeine:caffeine:3.0.2")

    // Use JUnit Jupiter API for testing.
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    // Use JUnit Jupiter Engine for testing.
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.1")
    implementation("com.uchuhimo:konf-toml:0.23.0")
}

configurations {
    runtimeClasspath {
        // Exclude dependencies that ship with iDE
        //exclude(group = "org.slf4j")
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
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

tasks {
    // Set the compatibility versions to 1.8
    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
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
}
