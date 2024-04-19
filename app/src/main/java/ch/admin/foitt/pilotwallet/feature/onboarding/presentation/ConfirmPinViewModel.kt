package ch.admin.foitt.pilotwallet.feature.onboarding.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.feature.onboarding.navigation.OnboardingNavGraph
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricManagerResult
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.usecase.BiometricsStatus
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.RegisterBiometricsNavArg
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.onboardingState.domain.SaveOnboardingState
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.InitializePassphrase
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinCheckResult
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinInputResult
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.ConfirmPinScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.HomeScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.RegisterBiometricsScreenDestination
import com.github.michaelbull.result.mapBoth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ConfirmPinViewModel @Inject constructor(
    private val biometricsStatus: BiometricsStatus,
    private val initializePassphrase: InitializePassphrase,
    private val saveOnboardingState: SaveOnboardingState,
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState,
    savedStateHandle: SavedStateHandle,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.SystemBarPadding

    private val originalPin = ConfirmPinScreenDestination.argsFrom(savedStateHandle).pin

    private val _pinCheckResult = MutableStateFlow<PinCheckResult>(PinCheckResult.Reset)
    val pinCheckResult = _pinCheckResult.asStateFlow()

    private val isBiometricAuthenticationAvailable: Boolean by lazy {
        biometricsStatus() != BiometricManagerResult.Unsupported
    }

    fun onValidPin(pin: String) {
        viewModelScope.launch {
            _pinCheckResult.value = when {
                pin != originalPin -> PinCheckResult.Error
                isBiometricAuthenticationAvailable -> PinCheckResult.Success
                else -> initializePassphrase(pin)
            }
        }
    }

    private suspend fun initializePassphrase(pin: String): PinCheckResult =
        initializePassphrase(pin, null).mapBoth(
            success = {
                saveOnboardingState.invoke(isCompleted = true)
                PinCheckResult.Success
            },
            failure = { error ->
                Timber.e(error.throwable, "Could not initialize passphrase with pin")
                PinCheckResult.Error
            }
        )

    fun onPinInputResult(result: PinInputResult) {
        _pinCheckResult.value = PinCheckResult.Reset
        if (result is PinInputResult.Success) {
            handlePinSuccess(result.pin)
        }
    }

    private fun handlePinSuccess(pin: String) {
        if (isBiometricAuthenticationAvailable) {
            navigateToBiometrics(pin)
        } else {
            navigateToSuccess()
        }
    }

    private fun navigateToBiometrics(pin: String) {
        navManager.navigateToAndClearCurrent(
            RegisterBiometricsScreenDestination(
                navArgs = RegisterBiometricsNavArg(pin = pin)
            )
        )
    }

    private fun navigateToSuccess() {
        navManager.navigateToAndPopUpTo(
            direction = HomeScreenDestination,
            route = OnboardingNavGraph.NAME,
        )
    }
}
