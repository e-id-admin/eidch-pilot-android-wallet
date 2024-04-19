package ch.admin.foitt.pilotwallet.platform.login.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricPromptWrapper
import ch.admin.foitt.pilotwallet.platform.login.domain.model.LoginWithBiometricsError
import ch.admin.foitt.pilotwallet.platform.navigation.domain.model.NavigationAction
import com.github.michaelbull.result.Result

fun interface LoginWithBiometrics {
    @CheckResult
    suspend operator fun invoke(promptWrapper: BiometricPromptWrapper): Result<NavigationAction, LoginWithBiometricsError>
}
