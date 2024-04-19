package ch.admin.foitt.pilotwallet.platform.passphrase.domain.model

import ch.admin.foitt.pilotwallet.platform.crypto.domain.model.HashDataError
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.model.PepperPassphraseError

sealed interface EncryptAndSavePassphraseError {
    val throwable: Throwable

    data class Unexpected(override val throwable: Throwable) : EncryptAndSavePassphraseError
}

fun EncryptAndSavePassphraseError.toInitializePassphraseError(): InitializePassphraseError = when (this) {
    is EncryptAndSavePassphraseError.Unexpected -> InitializePassphraseError.Unexpected(this.throwable)
}

sealed interface LoadAndDecryptPassphraseError {
    val throwable: Throwable

    data class Unexpected(override val throwable: Throwable) : LoadAndDecryptPassphraseError
}

sealed interface StorePassphraseError {
    val throwable: Throwable

    data class Unexpected(override val throwable: Throwable) : StorePassphraseError
}

fun HashDataError.toStorePassphraseError() = when (this) {
    is HashDataError.Unexpected -> StorePassphraseError.Unexpected(this.throwable)
}

fun PepperPassphraseError.toStorePassphraseError() = when (this) {
    is PepperPassphraseError.Unexpected -> StorePassphraseError.Unexpected(this.throwable)
}

fun EncryptAndSavePassphraseError.toStorePassphraseError() = when (this) {
    is EncryptAndSavePassphraseError.Unexpected -> StorePassphraseError.Unexpected(this.throwable)
}
