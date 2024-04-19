package ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model

sealed interface BiometricManagerResult {
    data object Available : BiometricManagerResult
    data object CanEnroll : BiometricManagerResult
    data object Disabled : BiometricManagerResult
    data object Unsupported : BiometricManagerResult
}
