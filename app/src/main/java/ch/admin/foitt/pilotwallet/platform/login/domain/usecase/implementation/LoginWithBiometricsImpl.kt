package ch.admin.foitt.pilotwallet.platform.login.domain.usecase.implementation

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricAuthenticationError
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricPromptWrapper
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.usecase.LaunchBiometricPrompt
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.model.GetBiometricsCipherError
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.GetBiometricsCipher
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.ResetBiometrics
import ch.admin.foitt.pilotwallet.platform.database.domain.model.OpenDatabaseError
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.OpenAppDatabase
import ch.admin.foitt.pilotwallet.platform.login.domain.model.LoginError
import ch.admin.foitt.pilotwallet.platform.login.domain.model.LoginWithBiometricsError
import ch.admin.foitt.pilotwallet.platform.login.domain.model.toLoginWithBiometricsError
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.LoginWithBiometrics
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.model.LoadAndDecryptPassphraseError
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.LoadAndDecryptPassphrase
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.onFailure
import timber.log.Timber
import javax.crypto.Cipher
import javax.inject.Inject

class LoginWithBiometricsImpl @Inject constructor(
    private val loadAndDecryptPassphrase: LoadAndDecryptPassphrase,
    private val launchBiometricPrompt: LaunchBiometricPrompt,
    private val getBiometricsCipher: GetBiometricsCipher,
    private val openAppDatabase: OpenAppDatabase,
    private val resetBiometrics: ResetBiometrics,
) : LoginWithBiometrics {
    @CheckResult
    override suspend fun invoke(promptWrapper: BiometricPromptWrapper): Result<Unit, LoginWithBiometricsError> {
        val result: Result<Unit, LoginWithBiometricsError> = coroutineBinding {
            val decryptionCipher: Cipher = getBiometricsCipher().mapError(
                GetBiometricsCipherError::toLoginWithBiometricsError
            ).bind()

            val initializedDecryptionCipher: Cipher = launchBiometricPrompt(
                promptWrapper = promptWrapper,
                cipher = decryptionCipher,
            ).mapError(
                BiometricAuthenticationError::toLoginWithBiometricsError
            ).bind()

            val decryptedPassphrase = loadAndDecryptPassphrase(
                decryptionCipher = initializedDecryptionCipher
            ).mapError(
                LoadAndDecryptPassphraseError::toLoginWithBiometricsError
            ).bind()

            openAppDatabase(
                passphrase = decryptedPassphrase
            ).onFailure {
                Timber.d("AppDatabase login failed")
            }.mapError(
                OpenDatabaseError::toLoginWithBiometricsError
            ).bind()

            Timber.d("Biometric Authentication succeeded")
        }

        return result
            .onFailure { loginWithBiometricsError ->
                onLoginFailure(loginWithBiometricsError)
            }
    }

    private suspend fun onLoginFailure(loginError: LoginWithBiometricsError) {
        when (loginError) {
            LoginError.InvalidPassphrase -> {
                resetBiometrics()
                Timber.w(message = "Passphrase was deemed invalid while login with biometrics")
            }
            LoginError.BiometricsChanged -> {
                resetBiometrics()
                Timber.w(message = "Biometrics changed, please re-enable them in the settings")
            }
            is LoginError.Unexpected -> {
                resetBiometrics()
                Timber.e(loginError.cause)
            }
            LoginError.Cancelled -> {}
            LoginError.BiometricsLocked -> {
                Timber.d(message = "Biometrics locked out")
            }
        }
    }
}
