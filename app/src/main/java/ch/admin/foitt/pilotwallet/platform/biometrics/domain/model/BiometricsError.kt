package ch.admin.foitt.pilotwallet.platform.biometrics.domain.model

import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricAuthenticationError
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.GetCipherForDecryptionError
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.GetCipherForEncryptionError
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.model.InitializePassphraseError
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.model.StorePassphraseError
import java.security.InvalidKeyException

interface BiometricsError {
    data object Cancelled : EnableBiometricsError
    data object Locked : EnableBiometricsError
    data object InvalidatedKey : GetBiometricsCipherError
    data class Unexpected(val cause: Throwable?) : EnableBiometricsError, GetBiometricsCipherError
}

sealed interface EnableBiometricsError
sealed interface GetBiometricsCipherError

//region Error to Error mappings
fun BiometricAuthenticationError.toEnableBiometricsError() = when (this) {
    BiometricAuthenticationError.PromptCancelled -> BiometricsError.Cancelled
    BiometricAuthenticationError.PromptLocked -> BiometricsError.Locked
    is BiometricAuthenticationError.PromptFailure -> BiometricsError.Unexpected(this.throwable)
    is BiometricAuthenticationError.Unexpected -> BiometricsError.Unexpected(this.throwable)
}

fun InitializePassphraseError.toEnableBiometricsError() = when (this) {
    is InitializePassphraseError.Unexpected -> BiometricsError.Unexpected(this.throwable)
}

fun GetCipherForEncryptionError.toEnableBiometricsError() = when (this) {
    is GetCipherForEncryptionError.InvalidKeyError -> BiometricsError.Unexpected(InvalidKeyException("Biometrics changed"))
    is GetCipherForEncryptionError.Unexpected -> BiometricsError.Unexpected(this.throwable)
}

fun StorePassphraseError.toEnableBiometricsError() = when (this) {
    is StorePassphraseError.Unexpected -> BiometricsError.Unexpected(this.throwable)
}

fun ResetBiometricsError.toEnableBiometricsError() = when (this) {
    is ResetBiometricsError.Unexpected -> BiometricsError.Unexpected(this.cause)
}

fun GetCipherForDecryptionError.toGetBiometricsCipherError() = when (this) {
    is GetCipherForDecryptionError.KeyInvalidated -> BiometricsError.InvalidatedKey
    is GetCipherForDecryptionError.Unexpected -> BiometricsError.Unexpected(throwable)
}
//endregion
