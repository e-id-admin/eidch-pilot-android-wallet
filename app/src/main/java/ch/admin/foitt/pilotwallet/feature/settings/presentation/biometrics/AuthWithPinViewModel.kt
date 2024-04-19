package ch.admin.foitt.pilotwallet.feature.settings.presentation.biometrics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.authWithPin.domain.model.AuthWithPinError
import ch.admin.foitt.pilotwallet.platform.authWithPin.domain.usecase.AuthWithPin
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.ResetBiometrics
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.EnableBiometricsNavArg
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinCheckResult
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinInputResult
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.AuthWithPinScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.EnableBiometricsScreenDestination
import com.github.michaelbull.result.mapBoth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthWithPinViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val authWithPin: AuthWithPin,
    private val resetBiometrics: ResetBiometrics,
    setTopBarState: SetTopBarState,
    savedStateHandle: SavedStateHandle,
) : ScreenViewModel(setTopBarState) {

    override val topBarState = TopBarState.Details(navManager::navigateUp, R.string.change_biometrics_title)

    private val navArgs = AuthWithPinScreenDestination.argsFrom(savedStateHandle)
    val enableBiometrics = navArgs.enable

    private val _pinCheckResult = MutableStateFlow<PinCheckResult>(PinCheckResult.Reset)
    val pinCheckResult = _pinCheckResult.asStateFlow()

    fun onValidPin(pin: String) {
        viewModelScope.launch {
            _pinCheckResult.value = authWithPin(pin = pin).mapBoth(
                success = {
                    if (!enableBiometrics) {
                        resetBiometrics()
                    }
                    PinCheckResult.Success
                },
                failure = { error ->
                    if (error is AuthWithPinError.Unexpected) {
                        Timber.e(error.cause, "Authentication with pin for biometrics failed")
                    }
                    PinCheckResult.Error
                }
            )
        }
    }

    fun onPinInputResult(result: PinInputResult) {
        _pinCheckResult.value = PinCheckResult.Reset
        if (result is PinInputResult.Success) {
            handlePinSuccess(result.pin)
        }
    }

    private fun handlePinSuccess(pin: String) {
        if (enableBiometrics) {
            navManager.navigateToAndClearCurrent(
                EnableBiometricsScreenDestination(navArgs = EnableBiometricsNavArg(pin = pin))
            )
        } else {
            navManager.popBackStack()
        }
    }
}
