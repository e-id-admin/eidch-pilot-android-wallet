package ch.admin.foitt.pilotwallet.platform.login.domain.model

import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricAuthenticationError
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.model.BiometricsError
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.model.GetBiometricsCipherError
import ch.admin.foitt.pilotwallet.platform.crypto.domain.model.HashDataError
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseError
import ch.admin.foitt.pilotwallet.platform.database.domain.model.OpenDatabaseError
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.model.LoadAndDecryptPassphraseError
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.model.PepperPassphraseError

interface LoginError {
    data object InvalidPassphrase : LoginWithPinError, LoginWithBiometricsError
    data object BiometricsChanged : LoginWithBiometricsError
    data object Cancelled : LoginWithBiometricsError
    data object BiometricsLocked : LoginWithBiometricsError
    data class Unexpected(val cause: Throwable?) : LoginWithPinError, LoginWithBiometricsError
}

sealed interface LoginWithPinError : LoginError
sealed interface LoginWithBiometricsError : LoginError

//region Error to Error mappings

fun OpenDatabaseError.toLoginWithPinError(): LoginWithPinError = when (this) {
    is DatabaseError.WrongPassphrase -> LoginError.InvalidPassphrase
    DatabaseError.AlreadyOpen,
    is DatabaseError.SetupFailed -> LoginError.Unexpected(null)
}

fun OpenDatabaseError.toLoginWithBiometricsError(): LoginWithBiometricsError = when (this) {
    is DatabaseError.WrongPassphrase -> LoginError.InvalidPassphrase
    DatabaseError.AlreadyOpen,
    is DatabaseError.SetupFailed -> LoginError.Unexpected(null)
}

fun PepperPassphraseError.toLoginWithPinError(): LoginWithPinError = when (this) {
    is PepperPassphraseError.Unexpected -> LoginError.Unexpected(this.throwable)
}

fun BiometricAuthenticationError.toLoginWithBiometricsError(): LoginWithBiometricsError = when (this) {
    BiometricAuthenticationError.PromptCancelled -> LoginError.Cancelled
    BiometricAuthenticationError.PromptLocked -> LoginError.BiometricsLocked
    is BiometricAuthenticationError.PromptFailure -> LoginError.Unexpected(this.throwable)
    is BiometricAuthenticationError.Unexpected -> LoginError.Unexpected(this.throwable)
}

fun HashDataError.toLoginWithPinError(): LoginWithPinError = when (this) {
    is HashDataError.Unexpected -> LoginError.Unexpected(this.throwable)
}

fun GetBiometricsCipherError.toLoginWithBiometricsError(): LoginWithBiometricsError = when (this) {
    BiometricsError.InvalidatedKey -> LoginError.BiometricsChanged
    is BiometricsError.Unexpected -> LoginError.Unexpected(cause)
}

fun LoadAndDecryptPassphraseError.toLoginWithBiometricsError(): LoginWithBiometricsError = when (this) {
    is LoadAndDecryptPassphraseError.Unexpected -> LoginError.Unexpected(this.throwable)
}
//endregion
