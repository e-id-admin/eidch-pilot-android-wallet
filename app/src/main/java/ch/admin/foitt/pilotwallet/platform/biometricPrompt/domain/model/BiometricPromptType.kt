package ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model

sealed interface BiometricPromptType {
    object Setup : BiometricPromptType
    object Login : BiometricPromptType
}
