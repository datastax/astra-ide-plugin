plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm")
}

repositories {
    mavenCentral()
}

val retrofitVersion = "2.7.2"
dependencies {
    api("com.squareup.moshi:moshi-kotlin:1.11.0")
    api("com.squareup.okhttp3:logging-interceptor:4.9.0")
    api("com.squareup.retrofit2:retrofit:$retrofitVersion")
    api("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
    api("com.squareup.retrofit2:converter-scalars:$retrofitVersion")
}
