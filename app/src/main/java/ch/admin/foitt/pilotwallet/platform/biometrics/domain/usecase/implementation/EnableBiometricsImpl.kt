package ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.appSetupState.domain.repository.UseBiometricLoginRepository
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricAuthenticationError
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricPromptWrapper
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.usecase.LaunchBiometricPrompt
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.model.EnableBiometricsError
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.model.ResetBiometricsError
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.model.toEnableBiometricsError
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.EnableBiometrics
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.ResetBiometrics
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.GetCipherForEncryptionError
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.usecase.GetCipherForEncryption
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.model.InitializePassphraseError
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.model.PassphraseStorageKeyConfig
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.model.StorePassphraseError
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.InitializePassphrase
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.StorePassphrase
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import timber.log.Timber
import javax.crypto.Cipher
import javax.inject.Inject

class EnableBiometricsImpl @Inject constructor(
    private val repo: UseBiometricLoginRepository,
    private val getCipherForEncryption: GetCipherForEncryption,
    private val launchBiometricPrompt: LaunchBiometricPrompt,
    private val initializePassphrase: InitializePassphrase,
    private val storePassphrase: StorePassphrase,
    private val passphraseStorageKeyConfig: PassphraseStorageKeyConfig,
    private val resetBiometrics: ResetBiometrics,
) : EnableBiometrics {
    override suspend fun invoke(
        promptWrapper: BiometricPromptWrapper,
        pin: String,
        fromSetup: Boolean,
    ): Result<Unit, EnableBiometricsError> {
        val result: Result<Unit, EnableBiometricsError> = coroutineBinding {
            resetBiometrics().mapError(
                ResetBiometricsError::toEnableBiometricsError
            ).bind()
            val encryptionCipher: Cipher = getCipherForEncryption(
                keystoreKeyConfig = passphraseStorageKeyConfig,
                initializationVector = null,
            ).mapError(
                GetCipherForEncryptionError::toEnableBiometricsError
            ).bind()

            // If biometric fails for any reason, we abort the process
            val initializedEncryptionCipher = launchBiometricPrompt(
                cipher = encryptionCipher,
                promptWrapper = promptWrapper,
            ).mapError(
                BiometricAuthenticationError::toEnableBiometricsError
            ).bind()

            if (fromSetup) {
                initializePassphrase(pin, initializedEncryptionCipher)
                    .mapError(
                        InitializePassphraseError::toEnableBiometricsError
                    ).bind()
            } else {
                storePassphrase(pin, initializedEncryptionCipher)
                    .mapError(
                        StorePassphraseError::toEnableBiometricsError
                    ).bind()
            }

            repo.saveUseBiometricLogin(isEnabled = true)
            Timber.d("Initialization succeeded")
        }
        return result
    }
}
