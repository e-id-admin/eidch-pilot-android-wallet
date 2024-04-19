package ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.model

import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.GetCipherForEncryptionError

sealed interface PepperPassphraseError {
    data class Unexpected(val throwable: Throwable) : PepperPassphraseError
}

//region Error to Error mappings
fun GetCipherForEncryptionError.toPepperPassphraseError(): PepperPassphraseError = when (this) {
    is GetCipherForEncryptionError.InvalidKeyError -> PepperPassphraseError.Unexpected(Exception(""))
    is GetCipherForEncryptionError.Unexpected -> PepperPassphraseError.Unexpected(this.throwable)
}
//endregion
