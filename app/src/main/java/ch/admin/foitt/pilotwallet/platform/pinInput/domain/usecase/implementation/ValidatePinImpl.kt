package ch.admin.foitt.pilotwallet.platform.pinInput.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinConstraints
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinValidationState
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.usecase.ValidatePin
import javax.inject.Inject

class ValidatePinImpl @Inject constructor(
    private val constraints: PinConstraints,
) : ValidatePin {

    override fun invoke(pin: String): PinValidationState {
        val state =
            if (pin.length == constraints.length && constraints.charSet.matches(pin)) {
                PinValidationState.VALID
            } else if (pin.length < constraints.length && constraints.charSet.matches(pin)) {
                PinValidationState.INCOMPLETE
            } else {
                PinValidationState.INVALID
            }

        return state
    }
}
