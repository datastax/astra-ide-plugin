package com.datastax.astra.jetbrains.utils

import com.intellij.ui.jcef.JBCefAppRequiredArgumentsProvider

class JcefProvider : JBCefAppRequiredArgumentsProvider {
    override val options: List<String>
        get() = listOf<String>("--disable-features=OutOfBlinkCors", "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0")
}
