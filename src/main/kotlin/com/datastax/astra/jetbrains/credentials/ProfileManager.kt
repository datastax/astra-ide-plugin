package com.datastax.astra.jetbrains.credentials

import com.datastax.astra.jetbrains.utils.getLogger
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SimpleModificationTracker
import com.intellij.util.ExceptionUtil
import com.intellij.util.messages.Topic
import org.jetbrains.concurrency.AsyncPromise
import java.util.concurrent.atomic.AtomicReference

/** Plugin service that keeps track of profiles and provides tokens for other Astra plugin services
 *
 */
class ProfileManager(private val project: Project) : SimpleModificationTracker(), Disposable {
    private val profileMap = mutableMapOf<String,Profile>()
    private var activeTokenMap = mutableMapOf<String,ProfileToken>()

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

    // Internal state is visible ChangeProfileSettingsActionGroup
    internal var selectedProfile: Profile? = null
    internal var selectedToken: ProfileToken? = null

    init {

        validateAndGetProfiles().validProfiles.forEach { profileMap[it.key] = it.value }
        selectedProfile = profileMap["default"]
        selectedProfile?.let { buildTokenMap(it) }
        selectedToken = activeTokenMap.entries.first().value
    }

    fun changeProfile(nextProfile: Profile) {
        selectedProfile = nextProfile
        buildTokenMap(nextProfile)
        selectedToken = activeTokenMap.entries.first().value
    }

    fun changeToken(nextToken: ProfileToken) {
        selectedToken = nextToken
    }


    @Synchronized
    private fun changeFieldsAndNotify(fieldUpdateBlock: () -> Unit) {
        val isInitial = profileState is ProfileState.InitializingToolkit
        //connectionState = ConnectionState.ValidatingConnection

        // Grab the current state stamp
        val modificationStamp = this.modificationCount

        fieldUpdateBlock()

        val validateCredentialsResult = validateCredentials(selectedProfile, selectedToken, isInitial)
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

    private fun validateCredentials(profile: Profile?, profileToken: ProfileToken?, isInitial: Boolean): AsyncPromise<ProfileState> {
        val promise = AsyncPromise<ProfileState>()
        ApplicationManager.getApplication().executeOnPooledThread {
            if (profile == null || profileToken == null) {
                promise.setResult(ProfileState.IncompleteConfiguration(profile, profileToken))
                return@executeOnPooledThread
            }

            var success = true
            try {
                //validate(credentialsProvider, region)

                promise.setResult(ProfileState.ValidConnection(profile))
            } catch (e: Exception) {

            } finally {
            }
        }

        return promise
    }

    //Possibly refactor this part
    private fun buildTokenMap(profile: Profile){
        activeTokenMap.clear()
        profile.token_collection.forEach{ activeTokenMap[it.key] = ProfileToken(it.key,it.value)}
    }

    /**
     * Legacy method, should be considered deprecated and avoided since it loads defaults out of band
     */
    val activeToken: ProfileToken?
        get() = selectedToken

    /**
     * Legacy method, should be considered deprecated and avoided since it loads defaults out of band
     */
    val activeProfile: Profile?
        get() = selectedProfile

    val profiles: Map<String,Profile>
        get() = profileMap

    val tokens: Map<String,ProfileToken>
        get() = activeTokenMap

    override fun dispose() {
    }

    companion object {
        /***
         * MessageBus topic for when the active credential profile or region is changed
         */
        val CONNECTION_SETTINGS_STATE_CHANGED: Topic<ProfileStateChangeNotifier> = Topic.create(
            "AWS Account setting changed",
            ProfileStateChangeNotifier::class.java
        )


        @JvmStatic
        fun getInstance(project: Project): ProfileManager = ServiceManager.getService(project, ProfileManager::class.java)

        private val LOGGER = getLogger<ProfileManager>()
        private const val MAX_HISTORY = 5
        internal val ProfileManager.selectedToken get() = selectedToken
    }

}

sealed class ProfileState(val displayMessage: String, val isTerminal: Boolean) {
    //protected val editCredsAction: AnAction = ActionManager.getInstance().getAction("aws.settings.upsertCredentials")

    /**
     * An optional short message to display in places where space is at a premium
     */
    open val shortMessage: String = displayMessage

    open val actions: List<AnAction> = emptyList()

    object InitializingToolkit : ProfileState("Initializing", isTerminal = false)

    object ValidatingProfile : ProfileState("settings.states.validating", isTerminal = false) {
        override val shortMessage: String = "settings.states.validating.short"
    }
    class ValidConnection(internal val profile: Profile) :
        ProfileState("${profile.profile_name}@placehold", isTerminal = true) {
        override val shortMessage: String = "${profile.profile_name}@placeholdID"
    }

    class IncompleteConfiguration(profile: Profile?, profileToken: ProfileToken?) : ProfileState(
        when {
            profileToken == null && profile == null -> "settings.none_selected"
            profileToken == null -> "settings.regions.none_selected"
            profile == null -> "settings.credentials.none_selected"
            else -> throw IllegalArgumentException("At least one of regionId ($profileToken) or toolkitCredentialsIdentifier ($profile) must be null")
        },
        isTerminal = true
    ) {
        //override val actions: List<AnAction> = listOf(editCredsAction)
    }

    class InvalidProfile(private val cause: Exception) :
        ProfileState("settings.states.invalid", isTerminal = true) {
        override val shortMessage = "settings.states.invalid.short"

        //override val actions: List<AnAction> = listOf(RefreshConnectionAction(message("settings.retry")), editCredsAction)
    }
}

interface ProfileStateChangeNotifier {
    fun profileStateChanged(newState: ProfileState)
}