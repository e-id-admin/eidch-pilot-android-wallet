package ch.admin.foitt.pilotwallet.platform.pinInput.presentation

import androidx.lifecycle.ViewModel
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinCheckResult
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinConstraints
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinInputFieldState
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinValidationState
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.usecase.ValidatePin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PinInputComponentViewModel @Inject constructor(
    val validatePin: ValidatePin,
    pinConstraints: PinConstraints,
) : ViewModel() {

    val pinLength: Int = pinConstraints.length

    private val _pin = MutableStateFlow("")
    val pin = _pin.asStateFlow()

    private val _pinInputFieldState = MutableStateFlow<PinInputFieldState>(PinInputFieldState.Typing)
    val pinInputFieldState = _pinInputFieldState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun updatePin(pin: String) {
        val state = validatePin(pin = pin)
        if (state != PinValidationState.INVALID) {
            _pin.value = pin
        }
        if (state == PinValidationState.VALID) {
            _pinInputFieldState.value = PinInputFieldState.Valid(pin)
            _isLoading.value = true
        }
    }

    fun onPinCheckResult(result: PinCheckResult) {
        _isLoading.value = false
        _pinInputFieldState.value = when (result) {
            PinCheckResult.Success -> PinInputFieldState.Success
            is PinCheckResult.Error -> PinInputFieldState.Error
            PinCheckResult.Reset -> {
                _pin.value = ""
                PinInputFieldState.Typing
            }
        }
    }
}
