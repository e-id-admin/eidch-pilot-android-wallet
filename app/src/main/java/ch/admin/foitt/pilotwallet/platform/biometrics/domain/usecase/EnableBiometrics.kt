package ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase

import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricPromptWrapper
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.model.EnableBiometricsError
import com.github.michaelbull.result.Result

interface EnableBiometrics {
    suspend operator fun invoke(
        promptWrapper: BiometricPromptWrapper,
        pin: String,
        fromSetup: Boolean,
    ): Result<Unit, EnableBiometricsError>
}
