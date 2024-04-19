package ch.admin.foitt.pilotwallet.platform.authWithPin.domain.model

import ch.admin.foitt.pilotwallet.platform.crypto.domain.model.HashDataError
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseError
import ch.admin.foitt.pilotwallet.platform.database.domain.model.OpenDatabaseError
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.model.PepperPassphraseError

sealed interface AuthWithPinError {
    object InvalidPassphrase : AuthWithPinError
    data class Unexpected(val cause: Throwable?) : AuthWithPinError
}

//region Error to Error mappings

fun OpenDatabaseError.toAuthWithPinError(): AuthWithPinError = when (this) {
    is DatabaseError.WrongPassphrase -> AuthWithPinError.InvalidPassphrase
    DatabaseError.AlreadyOpen,
    is DatabaseError.SetupFailed -> AuthWithPinError.Unexpected(null)
}

fun PepperPassphraseError.toAuthWithPinError(): AuthWithPinError = when (this) {
    is PepperPassphraseError.Unexpected -> AuthWithPinError.Unexpected(this.throwable)
}

fun HashDataError.toAuthWithPinError(): AuthWithPinError = when (this) {
    is HashDataError.Unexpected -> AuthWithPinError.Unexpected(this.throwable)
}
//endregion
