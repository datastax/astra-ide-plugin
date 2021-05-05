package com.datastax.astra.jetbrains.credentials

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec

class CredentialsClient {
    object ClientConfig: ConfigSpec() {
        val token by required<String>()
    }
    var client = Config{addSpec(ClientConfig)}
        .from.file("${System.getProperty("user.home")}/.astra/config.toml")

    fun token():String = this.client[ClientConfig.token]

}

