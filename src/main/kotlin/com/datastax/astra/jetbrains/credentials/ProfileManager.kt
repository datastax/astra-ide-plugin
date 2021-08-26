package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.AstraClient
import com.datastax.astra.jetbrains.credentials.ProfileReader.validateAndGetProfiles
import com.datastax.astra.jetbrains.utils.getLogger
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SimpleModificationTracker
import com.intellij.util.messages.Topic
import org.jetbrains.concurrency.AsyncPromise
import java.util.concurrent.atomic.AtomicReference

/**
 * Plugin service that keeps track of profiles and provides tokens for other Astra plugin classes/objects
 */
class ProfileManager(private val project: Project) : SimpleModificationTracker(), Disposable {
    private var profileMap = mapOf<String, ProfileToken>()

    // Internal state is visible ChangeProfileSettingsActionGroup
    internal var selectedProfile: ProfileToken? = null
    private val validationJob = AtomicReference<AsyncPromise<ProfileState>>()

    @Volatile
    var profileState: ProfileState = ProfileState.InitializingToolkit
        internal set(value) {
            field = value
            incModificationCount()

            if (!project.isDisposed) {
                project.messageBus.syncPublisher(CONNECTION_SETTINGS_STATE_CHANGED).profileStateChanged(value)
            }
        }

    init {

        loadProfiles()
    }

    // TODO: Revisit this and do something less clunky
    fun loadProfiles() {
        profileMap = validateAndGetProfiles().validProfiles

        // Check if any valid profiles exist. If so check for default in valid profiles. If not use first valid profile
        if (profileMap.isNotEmpty()) {
            if (profileMap.containsKey("default")) {
                selectedProfile = profileMap["default"]
            } else {
                selectedProfile = profileMap.entries.first().value
            }
            AstraClient.accessToken = selectedProfile!!.token

            // validateProfileAndSetState(selectedProfile)
            changeProfile(selectedProfile!!)
        } else {
            // Null if no valid profiles on reload. A reload without doing so will result in residual profile being used
            selectedProfile = null
            AstraClient.accessToken = ""
            profileState = ProfileState.IncompleteConfiguration(selectedProfile)
        }
    }

    fun changeProfile(nextProfile: ProfileToken) {
        changeFieldsAndNotify {
            selectedProfile = nextProfile
        }
    }

    @Synchronized
    private fun changeFieldsAndNotify(fieldUpdateBlock: () -> Unit) {
        profileState = ProfileState.ValidatingProfile

        // Grab the current state stamp
        val modificationStamp = this.modificationCount

        fieldUpdateBlock()

        val validateCredentialsResult = validateCredentials(selectedProfile)
        validationJob.getAndSet(validateCredentialsResult)?.cancel()

        validateCredentialsResult.onSuccess {
            // Validate we are still operating in the latest view of the world
            if (modificationStamp == this.modificationCount) {
                profileState = it
            } else {
                LOGGER.warn("validateCredentials returned but the account manager state has been manipulated before results were back, ignoring")
            }
        }
    }

    private fun validateProfileAndSetState(profile: ProfileToken?) {
        profileState = if (profile != null) ProfileState.ValidConnection(profile) else ProfileState.IncompleteConfiguration(profile)
    }

    private fun validateCredentials(profile: ProfileToken?): AsyncPromise<ProfileState> {
        val promise = AsyncPromise<ProfileState>()
        ApplicationManager.getApplication().executeOnPooledThread {
            if (profile == null) {
                promise.setResult(ProfileState.IncompleteConfiguration(profile))
                return@executeOnPooledThread
            }

            try {
                promise.setResult(ProfileState.ValidConnection(profile))
            } catch (e: Exception) {
            }
        }
        return promise
    }

    /**
     * Legacy method, should be considered deprecated and avoided since it loads defaults out of band
     */
    val activeProfile: ProfileToken?
        get() = selectedProfile

    /**
     * Legacy method, should be considered deprecated and avoided since it loads defaults out of band
     */
    val profiles: Map<String, ProfileToken>
        get() = profileMap

    override fun dispose() {
    }

    companion object {
        /***
         * MessageBus topic for when the active credential profile or region is changed
         * Defining it here lets us not define it in the plugin.xml
         * TODO: Find out if defining a notifier this way is still acceptable per IntelliJ best practices
         */
        val CONNECTION_SETTINGS_STATE_CHANGED: Topic<ProfileStateChangeNotifier> = Topic.create(
            "DataStax Astra profile changed",
            ProfileStateChangeNotifier::class.java
        )

        @JvmStatic
        fun getInstance(project: Project): ProfileManager = ServiceManager.getService(project, ProfileManager::class.java)

        private val LOGGER = getLogger<ProfileManager>()
        private const val MAX_HISTORY = 5
        internal val ProfileManager.selectedProfile get() = selectedProfile
    }
}

// TODO:Rebase this whole section
// Fix messages
// Add verification to make sure token wasn't removed since loading check occurred
sealed class ProfileState(val displayMessage: String, val isTerminal: Boolean) {
    protected val editProfiles: AnAction = ActionManager.getInstance().getAction("credentials.upsert")

    /**
     * An optional short message to display in places where space is at a premium
     */
    open val shortMessage: String = displayMessage

    open val actions: List<AnAction> = emptyList()

    object InitializingToolkit : ProfileState("Initializing", isTerminal = false)

    object ValidatingProfile : ProfileState("settings.states.validating", isTerminal = false) {
        override val shortMessage: String = "settings.states.validating.short"
    }

    class ValidConnection(internal val profile: ProfileToken) :
        ProfileState("${profile.name}@placehold", isTerminal = true) {
        override val shortMessage: String = "${profile.name}@placeholdID"
    }

    class IncompleteConfiguration(profile: ProfileToken?) : ProfileState(
        when {
            profile == null -> "settings.credentials.none_selected"
            else -> throw IllegalArgumentException("($profile) must be null")
        },
        isTerminal = true
    ) {
        override val actions: List<AnAction> = listOf(editProfiles)
    }
}

interface ProfileStateChangeNotifier {
    fun profileStateChanged(newState: ProfileState)
}
