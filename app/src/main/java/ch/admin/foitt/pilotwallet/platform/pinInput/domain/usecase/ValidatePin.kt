package ch.admin.foitt.pilotwallet.platform.pinInput.domain.usecase

import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinValidationState

interface ValidatePin {
    operator fun invoke(pin: String): PinValidationState
}
