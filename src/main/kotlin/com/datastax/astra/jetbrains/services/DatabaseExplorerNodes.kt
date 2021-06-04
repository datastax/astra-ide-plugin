package com.datastax.astra.jetbrains.explorer

import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.devops_v2.models.StatusEnum
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.services.database.openEditor
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.stargate_v2.models.Keyspace
import com.datastax.astra.stargate_v2.models.Table
import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.github.benmanes.caffeine.cache.Caffeine
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.ui.SimpleTextAttributes
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import retrofit2.Response
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

val fetchDB: (suspend (key: String) -> Response<List<Database>>) = {
    AstraClient.dbOperationsApi().listDatabases()
}

class DatabaseParentNode(project: Project) :
    ExplorerNode<String>(project, "Databases", null),
    ResourceActionNode, ResourceParentNode {
    init{
        //Pass project object to astraclient so it can acquire tokens from profile manager
        AstraClient.project=project
    }

    private var children = mutableMapOf<String, DatabaseNode>()

    override fun actionGroupName(): String = "astra.explorer.databases"
    override fun getChildren(): List<ExplorerNode<*>> = super.getChildren()
    override fun getChildrenInternal(): List<ExplorerNode<*>> = runBlocking {

        val response = cached("", loader = fetchDB)
        if (response.isSuccessful) {
            //Remove any children not present in response
            children = children.filterKeys { key ->
                response.body().orEmpty().map { it.id }.contains(key)
            } as MutableMap<String, DatabaseNode>
            response.body()?.forEach {
                val node = children.get(it.id)
                if (node != null) {
                    if (!node.database.equals(it)) {
                        //Update
                        node.database = it
                    }
                } else {
                    //Add
                    children[it.id] = DatabaseNode(nodeProject, it)
                }
            }
        } else {
            children.clear()
        }
        children.toList().map { it.second }
    }

    fun clearCache() = run {
        //This is how to force removal
        //Need the lambda not 'inline' though because it's used as the key in the map
        val cacheMap = cacheMap as MutableMap<KClass<out suspend (String) -> Any?>, AsyncLoadingCache<*, *>>
        cacheMap[fetchDB::class].also {
            it?.asMap()?.remove("")
        }
    }
}

class DatabaseNode(nodeProject: Project, database: Database) :
    ExplorerNode<String>(nodeProject, database.info.name.orEmpty(), null),
    ResourceActionNode, ResourceParentNode {

    var pollCount = 0
    var polling: Job? = null

    fun pollForUpdatesIfNeeded(): Unit {
        if (polling?.isActive != true && database.status.isProcessing()) {
            polling = pollForUpdates()
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    fun pollForUpdates(): Job {
        val explorerToolWindow = ExplorerToolWindow.getInstance(nodeProject)
        return flow {
            while (isActive) {
                emit(AstraClient.dbOperationsApi().getDatabase(database.id))
                delay(2000)
            }
        }.transform {
            it.body()?.let { db ->
                database = db
            }
            if (it.body()?.status?.isProcessing() != true) {
                pollCount = 0
                if (it.body()?.status == StatusEnum.TERMINATED) {
                    //Terminated nodes won't show up in the listDatabases.
                    //Schedule an update of the Databases tree to remove this node
                    (parent as DatabaseParentNode).clearCache()
                    nodeProject.refreshTree(parent, true)
                } else {
                    nodeProject.refreshTree(this@DatabaseNode, true)
                }
                emit(it)
            } else {
                //Force an update of the view
                pollCount++
                nodeProject.refreshTree(this@DatabaseNode, false)
            }
        }.take(1).launchIn(ApplicationThreadPoolScope("databases"))
    }

    var database = database
        set(value) {
            field = value
            pollForUpdatesIfNeeded()
        }

    init {
        pollForUpdatesIfNeeded()
    }

    override fun update(presentation: PresentationData) {
        presentation.let {
            it.addText(displayName(), SimpleTextAttributes.REGULAR_ATTRIBUTES)
            if (database.status.isProcessing())
                it.addText(
                    ColoredFragment(
                        " ${database.status.value}${".".repeat(pollCount % 4)}",
                        SimpleTextAttributes.GRAYED_BOLD_ATTRIBUTES
                    )
                )
        }
    }

    override fun actionGroupName(): String = "astra.explorer.databases.database"
    override fun emptyChildrenNode(): ExplorerEmptyNode =
        ExplorerEmptyNode(nodeProject, message("astra.no_keyspaces_in_database"))

    override fun getChildren(): List<ExplorerNode<*>> = super.getChildren()
    override fun getChildrenInternal(): List<ExplorerNode<*>> = runBlocking {
        AstraClient.schemasApiForDatabase(database)
            .getKeyspaces(AstraClient.accessToken, null).body()?.data?.map {
                KeyspaceNode(nodeProject, it, database)
            } ?: emptyList()
    }
}

class KeyspaceNode(project: Project, val keyspace: Keyspace, val database: Database) :
    ExplorerNode<String>(project, keyspace.name, null),
    ResourceActionNode, ResourceParentNode {

    override fun actionGroupName(): String = "astra.explorer.databases.keyspace"
    override fun emptyChildrenNode(): ExplorerEmptyNode =
        ExplorerEmptyNode(nodeProject, message("astra.no_tables_in_keyspace"))

    override fun getChildren(): List<ExplorerNode<*>> = super.getChildren()
    override fun getChildrenInternal(): List<ExplorerNode<*>> = runBlocking {
        AstraClient.schemasApiForDatabase(database)
            .getTables(AstraClient.accessToken, keyspace.name, null).body()?.data?.map {
                TableNode(nodeProject, it, database)
            } ?: emptyList()
    }
}

class TableNode(project: Project, val table: Table, val database: Database) :
    ExplorerNode<String>(project, table.name.orEmpty(), null),
    ResourceActionNode {

    override fun actionGroupName(): String = "astra.explorer.databases.table"
    override fun getChildren(): List<AbstractTreeNode<*>> = emptyList()

    override fun onDoubleClick() {
        openEditor(nodeProject, table, database)
    }
}

private val cacheContext = CoroutineScope(Dispatchers.Default + SupervisorJob())
private val cacheMap: MutableMap<KClass<suspend (Any?) -> Any?>, AsyncLoadingCache<*, *>> = HashMap()

fun clearCacheMap(){
    val cacheMap = cacheMap as MutableMap<KClass<out suspend (String) -> Any?>, AsyncLoadingCache<*, *>>
    cacheMap[fetchDB::class].also {
        it?.asMap()?.remove("")
    }
}

suspend fun <K, V> cached(
    key: K,
    cacheConfig: Caffeine<Any, Any>.() -> Caffeine<Any, Any> = {
        this.maximumSize(1000)
            .expireAfterAccess(5, TimeUnit.SECONDS)
    },
    loader: suspend (K) -> V
): V = withContext(cacheContext.coroutineContext) {

    val cacheMap = cacheMap as MutableMap<KClass<out suspend (K) -> V>, AsyncLoadingCache<K, V>>

    (cacheMap[loader::class] ?: Caffeine.newBuilder()
        .cacheConfig()
        .buildAsync { key: K, _ -> cacheContext.future { loader(key) } }
        .also { cacheMap[loader::class] = it })
        .get(key)
        .await()
}

fun StatusEnum.isProcessing(): Boolean {
    return when (this) {
        StatusEnum.INITIALIZING,
        StatusEnum.PARKING,
        StatusEnum.PREPARING,
        StatusEnum.PENDING,
        StatusEnum.RESIZING,
        StatusEnum.TERMINATING,
        StatusEnum.UNPARKING -> true
        else -> false
    }
}
