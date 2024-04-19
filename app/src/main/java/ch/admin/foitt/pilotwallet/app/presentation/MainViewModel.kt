package ch.admin.foitt.pilotwallet.app.presentation

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.LockTrigger
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
    private val afterLoginWork: AfterLoginWork,
    private val closeAppDatabase: CloseAppDatabase,
    private val setDeepLinkIntent: SetDeepLinkIntent,
    private val navManager: NavigationManager,
    @IoDispatcherScope private val ioDispatcherScope: CoroutineScope,
) : ViewModel() {
    private var lockTriggerJob: Job? = null
    private var afterLoginWorkJob: Job? = null

    fun initNavHost(navHostController: NavHostController) {
        navManager.setNavHost(navHostController)
    }

    fun parseIntent(intent: Intent) {
        Timber.d("Intent received,\naction: ${intent.action},\ndata: ${intent.dataString}")
        val isLauncherIntent = intent.action == Intent.ACTION_MAIN && intent.categories != null &&
            intent.categories.contains(Intent.CATEGORY_LAUNCHER)
        // when we come back to the app after a deeplink was opened we get an intent that just contains the package name and main activity
        val intentHasNullProperties = intent.action == null || intent.scheme == null || intent.dataString == null

        if (!isLauncherIntent && !intentHasNullProperties) {
            setDeepLinkIntent(intent.dataString)
        } else {
            Timber.d("Intent considered useless")
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
        afterLoginWorkJob = ioDispatcherScope.launch {
            afterLoginWork()
        }
    }

    override fun onCleared() {
        afterLoginWorkJob?.cancel()
        afterLoginWorkJob = null

        ioDispatcherScope.launch {
            closeAppDatabase()
        }
        lockTriggerJob?.cancel()
        lockTriggerJob = null
        Timber.d("MainViewModel cleared")
        super.onCleared()
    }
}
