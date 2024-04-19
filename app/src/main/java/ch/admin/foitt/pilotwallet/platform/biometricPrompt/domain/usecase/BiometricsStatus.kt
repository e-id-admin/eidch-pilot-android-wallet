package ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.usecase

import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricManagerResult

interface BiometricsStatus {
    operator fun invoke(): BiometricManagerResult
}
