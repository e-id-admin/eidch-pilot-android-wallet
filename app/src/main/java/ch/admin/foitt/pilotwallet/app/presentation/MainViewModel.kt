package ch.admin.foitt.pilotwallet.app.presentation

import android.content.Intent
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.LockTrigger
import ch.admin.foitt.pilotwallet.feature.sessionTimeout.domain.SessionTimeoutNavigation
import ch.admin.foitt.pilotwallet.feature.sessionTimeout.domain.UserInteractionFlow
import ch.admin.foitt.pilotwallet.platform.appLifecycleRepository.domain.model.AppLifecycleState
import ch.admin.foitt.pilotwallet.platform.appLifecycleRepository.domain.usecase.GetAppLifecycleState
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.CloseAppDatabase
import ch.admin.foitt.pilotwallet.platform.deeplink.domain.usecase.SetDeepLinkIntent
import ch.admin.foitt.pilotwallet.platform.di.IoDispatcherScope
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.AfterLoginWork
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val lockTriggerUseCase: LockTrigger,
    private val userInteractionFlowUseCase: UserInteractionFlow,
    private val sessionTimeoutNavigation: SessionTimeoutNavigation,
    private val getAppLifecycleState: GetAppLifecycleState,
    private val afterLoginWork: AfterLoginWork,
    private val closeAppDatabase: CloseAppDatabase,
    private val setDeepLinkIntent: SetDeepLinkIntent,
    private val navManager: NavigationManager,
    @IoDispatcherScope private val ioDispatcherScope: CoroutineScope,
) : ViewModel() {
    private var lockTriggerJob: Job? = null
    private var sessionTimeoutJob: Job? = null
    private var appLifecycleJob: Job? = null
    private var afterLoginWorkJob: Job? = null

    private val countdown = object : CountDownTimer(SESSION_TIMEOUT, 1000) {
        @Suppress("EmptyFunctionBlock")
        override fun onTick(millisUntilFinished: Long) {}

        override fun onFinish() {
            ioDispatcherScope.launch {
                withContext(Dispatchers.Main) {
                    sessionTimeoutNavigation()?.let { direction ->
                        navManager.navigateTo(direction)
                    }
                }
            }
        }
    }

    fun initNavHost(navHostController: NavHostController) {
        navManager.setNavHost(navHostController)
    }

    fun parseIntent(intent: Intent) {
        Timber.d("Intent received,\naction: ${intent.action},\ndata: ${intent.dataString}")
        val isLauncherIntent = intent.action == Intent.ACTION_MAIN && intent.categories != null &&
            intent.categories.contains(Intent.CATEGORY_LAUNCHER)
        // when we come back to the app after a deeplink was opened we get an intent that just contains the package name and main activity
        val intentHasNullProperties = intent.action == null || intent.scheme == null || intent.dataString == null

        if (isLauncherIntent || intentHasNullProperties) {
            Timber.d("Intent considered useless")
        } else {
            intent.dataString?.let { intentDataString ->
                setDeepLinkIntent(intentDataString)
            } ?: Timber.w("Intent dataString null")
        }
    }

    init {
        Timber.d("MainViewModel initialized")
        lockTriggerJob = ioDispatcherScope.launch {
            lockTriggerUseCase().collect { newNavigationAction ->
                withContext(Dispatchers.Main) {
                    newNavigationAction.navigate()
                }
            }
        }

        appLifecycleJob = ioDispatcherScope.launch {
            getAppLifecycleState().collect { state ->
                when (state) {
                    AppLifecycleState.Foreground -> setupSessionTimeout()
                    AppLifecycleState.Background -> cancelSessionTimeout()
                }
            }
        }

        setupSessionTimeout()

        afterLoginWorkJob = ioDispatcherScope.launch {
            afterLoginWork()
        }
    }

    private fun setupSessionTimeout() {
        if (sessionTimeoutJob == null) {
            sessionTimeoutJob = ioDispatcherScope.launch {
                userInteractionFlowUseCase().collect { _ ->
                    countdown.cancel()
                    countdown.start()
                }
            }
        }
    }

    private fun cancelSessionTimeout() {
        countdown.cancel()
        sessionTimeoutJob?.cancel()
        sessionTimeoutJob = null
    }

    override fun onCleared() {
        afterLoginWorkJob?.cancel()
        afterLoginWorkJob = null

        ioDispatcherScope.launch {
            closeAppDatabase()
        }
        lockTriggerJob?.cancel()
        lockTriggerJob = null

        cancelSessionTimeout()

        appLifecycleJob?.cancel()
        appLifecycleJob = null

        Timber.d("MainViewModel cleared")
        super.onCleared()
    }

    companion object {
        private const val SESSION_TIMEOUT = 2 * 60 * 1000L
    }
}
