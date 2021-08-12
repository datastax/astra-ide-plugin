package com.datastax.astra.jetbrains.utils

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

class AstraIcons {

    object UI {
        val InsertDoc = IconLoader.getIcon("/icons/insert.svg", AstraIcons::class.java)

    }
    object IntelliJ{
        val Dbms = IconLoader.getIcon("/icons/dbms.svg", AstraIcons::class.java)
        val ColBlueKeyIndex = IconLoader.getIcon("/icons/colBlueKeyIndex.svg", AstraIcons::class.java)
    }


}