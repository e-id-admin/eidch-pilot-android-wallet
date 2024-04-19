package ch.admin.foitt.pilotwallet.feature.changeLogin.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.feature.changeLogin.domain.usecase.ChangePassphrase
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinCheckResult
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinInputResult
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.ConfirmNewPinScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.EnterNewPinScreenDestination
import com.github.michaelbull.result.mapBoth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ConfirmNewPinViewModel @Inject constructor(
    private val changePassphrase: ChangePassphrase,
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState,
    savedStateHandle: SavedStateHandle,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.Details(navManager::navigateUp, R.string.pin_change_title)

    private val originalPin = ConfirmNewPinScreenDestination.argsFrom(savedStateHandle).pin

    private val _pinCheckResult = MutableStateFlow<PinCheckResult>(PinCheckResult.Reset)
    val pinCheckResult = _pinCheckResult.asStateFlow()

    fun onValidPin(pin: String) {
        viewModelScope.launch {
            _pinCheckResult.value = if (pin != originalPin) {
                PinCheckResult.Error
            } else {
                changePassphrase(pin).mapBoth(
                    success = { PinCheckResult.Success },
                    failure = { error ->
                        Timber.e(error.throwable, "Could not change password")
                        PinCheckResult.Error
                    }
                )
            }
        }
    }

    fun onPinInputResult(result: PinInputResult) {
        _pinCheckResult.value = PinCheckResult.Reset
        if (result is PinInputResult.Success) {
            navManager.popBackStackTo(EnterNewPinScreenDestination, true)
        }
    }
}
