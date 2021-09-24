
package com.datastax.astra.jetbrains.services.database
/*
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.explorer.CollectionEndpoint
import com.datastax.astra.jetbrains.explorer.TableEndpoint
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AstraCollection {


}

class CollectionManager(
    val disposable: Disposable,
    val project: Project,
    val endpoint: CollectionEndpoint,
) : CoroutineScope by ApplicationThreadPoolScope("Collection") {
    protected val edtContext = getCoroutineUiContext(disposable = disposable)
    val collectionUI = CollectionUI(disposable, endpoint, ::changePage, ::changePageSize, ::changeWhereField)
    var pageSize = 10
    var pageCount = 0
    var whereFieldText = "{}"
    private val previousPages = mutableListOf<String>()
    private var nextPage = ""
    private var currentPage = ""

    init {

        launch {
            resetTable(AstraClient, "", pageSize, whereFieldText)
        }
    }
}

class CollectionUI(
    disposable: Disposable,
    endpoint: TableEndpoint,
    changePage: (Page) -> Unit,
    changePageSize: (Int) -> Unit,
    changeWhereQuery: (String) -> Unit,
) {

}*/
