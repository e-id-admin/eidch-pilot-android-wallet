package ch.admin.foitt.pilotwallet.feature.changeLogin.presentation

import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinCheckResult
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinInputResult
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.ConfirmNewPinScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EnterNewPinViewModel @Inject constructor(
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.Details(navManager::navigateUp, R.string.pin_change_title)

    private val _pinCheckResult = MutableStateFlow<PinCheckResult>(PinCheckResult.Reset)
    val pinCheckResult = _pinCheckResult.asStateFlow()

    fun onValidPin() {
        _pinCheckResult.value = PinCheckResult.Success
    }

    fun onPinInputResult(result: PinInputResult) {
        _pinCheckResult.value = PinCheckResult.Reset
        if (result is PinInputResult.Success) {
            navManager.navigateTo(
                ConfirmNewPinScreenDestination(
                    pin = result.pin,
                )
            )
        }
    }
}
