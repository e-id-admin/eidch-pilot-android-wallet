package ch.admin.foitt.pilotwallet.platform.biometrics.domain.model

import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.DeleteSecretKeyError

sealed interface ResetBiometricsError {
    data class Unexpected(val cause: Throwable?) : ResetBiometricsError
}

fun DeleteSecretKeyError.toResetBiometricsError(): ResetBiometricsError = when (this) {
    is DeleteSecretKeyError.Unexpected -> ResetBiometricsError.Unexpected(this.throwable)
}
