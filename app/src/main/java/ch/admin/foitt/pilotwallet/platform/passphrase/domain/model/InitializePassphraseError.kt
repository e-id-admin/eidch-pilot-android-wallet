package ch.admin.foitt.pilotwallet.platform.passphrase.domain.model

import ch.admin.foitt.pilotwallet.platform.crypto.domain.model.HashDataError
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CreateDatabaseError
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseError
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.model.PepperPassphraseError

sealed interface InitializePassphraseError {
    val throwable: Throwable?
    class Unexpected(override val throwable: Throwable?) : InitializePassphraseError
}

//region Error to Error mappings
fun PepperPassphraseError.toInitializePassphraseError(): InitializePassphraseError = when (this) {
    is PepperPassphraseError.Unexpected -> InitializePassphraseError.Unexpected(this.throwable)
}

fun HashDataError.toInitializePassphraseError(): InitializePassphraseError = when (this) {
    is HashDataError.Unexpected -> InitializePassphraseError.Unexpected(this.throwable)
}

fun CreateDatabaseError.toInitializePassphraseError(): InitializePassphraseError = when (this) {
    is DatabaseError.SetupFailed -> InitializePassphraseError.Unexpected(this.throwable)
    DatabaseError.AlreadyOpen -> InitializePassphraseError.Unexpected(null)
}
//endregion
