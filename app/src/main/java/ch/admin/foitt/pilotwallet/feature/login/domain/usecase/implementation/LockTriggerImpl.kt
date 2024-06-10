package ch.admin.foitt.pilotwallet.feature.login.domain.usecase.implementation

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.IsDevicePinSet
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.LockTrigger
import ch.admin.foitt.pilotwallet.platform.appLifecycleRepository.domain.model.AppLifecycleState
import ch.admin.foitt.pilotwallet.platform.appLifecycleRepository.domain.usecase.GetAppLifecycleState
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.CloseAppDatabase
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.IsAppDatabaseOpen
import ch.admin.foitt.pilotwallet.platform.di.IoDispatcherScope
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.navigation.domain.model.NavigationAction
import ch.admin.foitt.pilotwallet.platform.navigation.utils.blackListedDestinationsLockScreen
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.Destination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.LockScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.NoDevicePinSetScreenDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

internal class LockTriggerImpl @Inject constructor(
    private val navManager: NavigationManager,
    private val closeAppDatabase: CloseAppDatabase,
    private val getAppLifecycleState: GetAppLifecycleState,
    private val isAppDatabaseOpen: IsAppDatabaseOpen,
    private val isDevicePinSet: IsDevicePinSet,
    @IoDispatcherScope private val ioDispatcherScope: CoroutineScope,
) : LockTrigger {
    @CheckResult
    override suspend fun invoke(): StateFlow<NavigationAction> = combine(
        getAppLifecycleState(),
        navManager.currentDestinationFlow,
    ) { appLifecycleState, currentDestination ->
        currentDestination ?: return@combine NavigationAction { }

        when {
            !isDevicePinSet() -> {
                closeAppDatabase()
                navigateToNoDevicePinSet()
            }
            isScreenBlackListed(currentDestination) -> {
                closeAppDatabase()
                NavigationAction {}
            }
            appLifecycleState is AppLifecycleState.Foreground && isAppDatabaseOpen() -> NavigationAction {}
            else -> {
                // The app is in background, or is in foreground in an inconsistent state.
                closeAppDatabase()
                navigateToLockScreen()
            }
        }
    }.stateIn(
        scope = ioDispatcherScope,
    )

    private fun navigateToLockScreen() = NavigationAction {
        Timber.d("LockTrigger: lock navigation triggered")
        navManager.navigateTo(
            LockScreenDestination
        )
    }

    private fun navigateToNoDevicePinSet() = NavigationAction {
        if (navManager.currentDestination != NoDevicePinSetScreenDestination) {
            navManager.navigateTo(NoDevicePinSetScreenDestination)
        }
    }

    private fun isScreenBlackListed(destination: Destination): Boolean {
        return blackListedDestinationsLockScreen.contains(destination)
    }
}
