package ch.admin.foitt.pilotwallet.feature.onboarding.presentation

import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinCheckResult
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinInputResult
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.ConfirmPinScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EnterPinViewModel @Inject constructor(
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.SystemBarPadding

    private val _pinCheckResult = MutableStateFlow<PinCheckResult>(PinCheckResult.Reset)
    val pinCheckResult = _pinCheckResult.asStateFlow()

    fun onValidPin() {
        _pinCheckResult.value = PinCheckResult.Success
    }

    fun onPinInputResult(result: PinInputResult) {
        _pinCheckResult.value = PinCheckResult.Reset
        if (result is PinInputResult.Success) {
            navManager.navigateTo(
                ConfirmPinScreenDestination(
                    pin = result.pin
                )
            )
        }
    }
}
