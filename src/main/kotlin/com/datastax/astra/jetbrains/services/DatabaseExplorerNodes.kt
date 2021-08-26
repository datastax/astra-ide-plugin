package com.datastax.astra.jetbrains.explorer

import com.datastax.astra.devops_v2.models.Database
import com.datastax.astra.devops_v2.models.StatusEnum
import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.MessagesBundle.message
import com.datastax.astra.jetbrains.services.database.CollectionBrowserPanel
import com.datastax.astra.jetbrains.services.database.TableViewerPanel
import com.datastax.astra.jetbrains.utils.ApplicationThreadPoolScope
import com.datastax.astra.jetbrains.utils.editor.ui.EndpointTable
import com.datastax.astra.stargate_document_v2.models.DocCollection
import com.datastax.astra.stargate_rest_v2.models.Keyspace
import com.datastax.astra.stargate_rest_v2.models.Table
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
import retrofit2.HttpException
import java.util.UUID.randomUUID
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

val fetchDatabases: (suspend (key: String) -> List<Database>) = {
    val response = AstraClient.dbOperationsApi().listDatabases()
    if (response.isSuccessful) response.body()!! else throw HttpException(response)
}

val fetchKeyspaces: (suspend (database: Database) -> List<Keyspace>?) = {
    val response = AstraClient.schemasApiForDatabase(it).getKeyspaces(AstraClient.accessToken, null)
    if (response.isSuccessful) response.body()?.data else throw HttpException(response)
}

val fetchTables: (suspend (dataBKeySPair: Pair<Database, Keyspace>) -> List<Table>?) = {
    val response = AstraClient.schemasApiForDatabase(it.first).getTables(AstraClient.accessToken, it.second.name, null)
    if (response.isSuccessful) response.body()?.data else throw HttpException(response)
}

// TODO: Determine what UUID should be used (random is probably not a good choice)
val fetchCollections: (suspend (dataBKeySPair: Pair<Database, Keyspace>) -> List<DocCollection>?) = {
    val response = AstraClient.documentApiForDatabase(it.first).listCollections(randomUUID(), AstraClient.accessToken, it.second.name, null)
    if (response.isSuccessful) response.body()?.data else throw HttpException(response)
}

class DatabaseParentNode(project: Project) :
    ExplorerNode<String>(project, "Databases", null),
    ResourceActionNode,
    ResourceParentNode {
    init {
        // Pass project object to astraclient so it can acquire tokens from profile manager
        AstraClient.project = project
    }

    private var children = mutableMapOf<String, DatabaseNode>()

    override fun actionGroupName(): String = "astra.explorer.databases"
    override fun getChildren(): List<ExplorerNode<*>> = super.getChildren()
    override fun getChildrenInternal(): List<ExplorerNode<*>> = runBlocking {
        try {
            val dbList = cached("", loader = fetchDatabases)
            children = children.filterKeys { key ->
                dbList.map { it.id }.contains(key)
            } as MutableMap<String, DatabaseNode>
            dbList.forEach {
                val node = children.get(it.id)
                if (node != null) {
                    if (!node.database.equals(it)) {
                        // Update
                        node.database = it
                    }
                } else {
                    // Add
                    children[it.id] = DatabaseNode(nodeProject, it)
                }
            }
            children.toList().map { it.second }
        } catch (e: Exception) {
            children.clear()
            throw e
        }
    }

    fun clearCache() = run {
        // This is how to force removal
        // Need the lambda not 'inline' though because it's used as the key in the map
        val cacheMap = cacheMap as MutableMap<KClass<out suspend (String) -> Any?>, AsyncLoadingCache<*, *>>
        cacheMap[fetchDatabases::class].also {
            it?.asMap()?.remove("")
        }
    }
}

class DatabaseNode(nodeProject: Project, database: Database) :
    ExplorerNode<String>(nodeProject, database.info.name.orEmpty(), null),
    ResourceActionNode,
    ResourceParentNode {

    var pollCount = 0
    var polling: Job? = null

    fun pollForUpdatesIfNeeded() {
        if (polling?.isActive != true && database.status.isProcessing()) {
            polling = pollForUpdates()
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    fun pollForUpdates(): Job {
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
                    // Terminated nodes won't show up in the listDatabases.
                    // Schedule an update of the Databases tree to remove this node
                    (parent as DatabaseParentNode).clearCache()
                    nodeProject.refreshTree(parent, true)
                } else {
                    nodeProject.refreshTree(this@DatabaseNode, true)
                }
                emit(it)
            } else {
                // Force an update of the view
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
            if (database.status.isProcessing()) {
                it.addText(
                    ColoredFragment(
                        " ${database.status.value}${".".repeat(pollCount % 4)}",
                        SimpleTextAttributes.GRAYED_BOLD_ATTRIBUTES
                    )
                )
            }
        }
    }

    override fun actionGroupName(): String = "astra.explorer.databases.database"
    override fun emptyChildrenNode(): ExplorerEmptyNode =
        ExplorerEmptyNode(nodeProject, message("astra.no_keyspaces_in_database"))

    override fun getChildren(): List<ExplorerNode<*>> = super.getChildren()
    override fun getChildrenInternal(): List<ExplorerNode<*>> = runBlocking {
        cached(database, loader = fetchKeyspaces)?.map {
            KeyspaceNode(nodeProject, it, database)
        } ?: emptyList()
    }
}

class KeyspaceNode(project: Project, val keyspace: Keyspace, val database: Database) :
    ExplorerNode<String>(project, keyspace.name, null),
    ResourceActionNode,
    ResourceParentNode {

    override fun actionGroupName(): String = "astra.explorer.databases.keyspace"
    override fun emptyChildrenNode(): ExplorerEmptyNode =
        ExplorerEmptyNode(nodeProject, message("astra.no_tables_or_collections_in_keyspace"))

    override fun getChildren(): List<ExplorerNode<*>> = super.getChildren()
    override fun getChildrenInternal(): List<ExplorerNode<*>> {
        var cNode = CollectionParentNode(nodeProject, keyspace, database)
        cNode.getChildrenInternal()
        // Force getting children so that the elimination list can be built and used to filter tables
        var tNode = TableParentNode(nodeProject, keyspace, database, cNode.collectionList)

        // If both empty return empty so double empty drop down doesn't occur
        if (tNode.children.first().javaClass.name == cNode.children.first().javaClass.name) {
            return emptyList()
        }

        return listOf(tNode, cNode).reversed()
    }
}

class TableParentNode(project: Project, val keyspace: Keyspace, val database: Database, val collectionList: List<String>) :
    ExplorerNode<String>(project, "Tables", null),
    ResourceActionNode,
    ResourceParentNode {

    override fun actionGroupName(): String = "astra.explorer.tables"
    override fun emptyChildrenNode(): ExplorerEmptyNode =
        ExplorerEmptyNode(nodeProject, message("astra.no_tables_in_keyspace"))

    override fun getChildren(): List<ExplorerNode<*>> = super.getChildren()
    override fun getChildrenInternal(): List<ExplorerNode<*>> = runBlocking {
        cached(Pair(database, keyspace), loader = fetchTables)?.filter { !collectionList.contains(it.name) }?.map {
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
        TableViewerPanel(nodeProject, EndpointTable(database,table))
    }
}

class CollectionParentNode(project: Project, val keyspace: Keyspace, val database: Database) :
    ExplorerNode<String>(project, "Collections", null),
    ResourceActionNode,
    ResourceParentNode {
    var collectionList = emptyList<String>()

    override fun actionGroupName(): String = "astra.explorer.collections"
    override fun emptyChildrenNode(): ExplorerEmptyNode =
        ExplorerEmptyNode(nodeProject, message("astra.no_collections_in_keyspace"))

    override fun getChildren(): List<ExplorerNode<*>> = super.getChildren()
    // If upgrade is available then it's not really a document.
    override fun getChildrenInternal(): List<ExplorerNode<*>> = runBlocking {
        cached(Pair(database, keyspace), loader = fetchCollections)?.filter { it.upgradeAvailable == false }?.map {
            collectionList += it.name
            CollectionNode(nodeProject, it, keyspace, database)
        } ?: emptyList()
    }
}

class CollectionNode(project: Project, val collection: DocCollection, val keyspace: Keyspace, val database: Database) :
    ExplorerNode<String>(project, collection.name.orEmpty(), null),
    ResourceActionNode {

    override fun actionGroupName(): String = "astra.explorer.databases.collection"
    override fun getChildren(): List<AbstractTreeNode<*>> = emptyList()

    override fun onDoubleClick(): Unit = runBlocking {
        CollectionBrowserPanel(nodeProject, collection, keyspace, database)
    }
}

private val cacheContext = CoroutineScope(Dispatchers.Default + SupervisorJob())
private val cacheMap: MutableMap<KClass<suspend (Any?) -> Any?>, AsyncLoadingCache<*, *>> = HashMap()

fun clearCacheMap() {
    val cacheMap = cacheMap as MutableMap<KClass<out suspend (String) -> Any?>, AsyncLoadingCache<*, *>>
    cacheMap[fetchDatabases::class].also {
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

    (
        cacheMap[loader::class] ?: Caffeine.newBuilder()
            .cacheConfig()
            .buildAsync { key: K, _ -> cacheContext.future { loader(key) } }
            .also { cacheMap[loader::class] = it }
        )
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
