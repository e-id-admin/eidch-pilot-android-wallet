package ch.admin.foitt.pilotwallet.feature.login.domain.usecase.implementation

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.IsDevicePinSet
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.LockTrigger
import ch.admin.foitt.pilotwallet.platform.appLifecycleRepository.domain.model.AppLifecycleState
import ch.admin.foitt.pilotwallet.platform.appLifecycleRepository.domain.repository.AppLifecycleRepository
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.CloseAppDatabase
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.IsAppDatabaseOpen
import ch.admin.foitt.pilotwallet.platform.di.IoDispatcherScope
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.navigation.domain.model.NavigationAction
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.BiometricLoginScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.ConfirmPinScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.Destination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.EnterPinScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.LockScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.NoDevicePinSetScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.OnboardingIntroScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.OnboardingQRScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PinLoginScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.RegisterBiometricsScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.StartScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.UserPrivacyPolicyScreenDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

internal class LockTriggerImpl @Inject constructor(
    private val navManager: NavigationManager,
    private val closeAppDatabase: CloseAppDatabase,
    private val appLifecycleRepo: AppLifecycleRepository,
    private val isAppDatabaseOpen: IsAppDatabaseOpen,
    private val isDevicePinSet: IsDevicePinSet,
    @IoDispatcherScope private val ioDispatcherScope: CoroutineScope,
) : LockTrigger {
    @CheckResult
    override suspend fun invoke(): StateFlow<NavigationAction> = combine(
        appLifecycleRepo.state,
        navManager.currentDestinationFlow,
    ) { appLifecycleState, currentDestination ->
        currentDestination ?: return@combine NavigationAction { }

        when {
            !isDevicePinSet() -> {
                closeAppDatabase()
                navigateToNoDevicePinSet()
            }
            isScreenWhiteListed(currentDestination) -> {
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

    private fun isScreenWhiteListed(destination: Destination): Boolean {
        return whiteListedDestinations.contains(destination)
    }

    // Destinations that should not trigger a lock screen
    // it implicitly means that the database should either not exist, or be closed and encrypted, on these destinations
    private val whiteListedDestinations = listOf(
        StartScreenDestination,
        NoDevicePinSetScreenDestination,
        LockScreenDestination,
        OnboardingIntroScreenDestination,
        OnboardingQRScreenDestination,
        PinLoginScreenDestination,
        BiometricLoginScreenDestination,
        UserPrivacyPolicyScreenDestination,
        EnterPinScreenDestination,
        ConfirmPinScreenDestination,
        RegisterBiometricsScreenDestination,
    )
}
