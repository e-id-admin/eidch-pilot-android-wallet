package ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.usecase

import androidx.annotation.CheckResult

fun interface GetAuthenticators {
    @CheckResult
    operator fun invoke(): Int
}
