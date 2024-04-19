package ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.appSetupState.domain.repository.UseBiometricLoginRepository
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.model.ResetBiometricsError
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.model.toResetBiometricsError
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.ResetBiometrics
import ch.admin.foitt.pilotwallet.platform.di.IoDispatcher
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.repository.PassphraseRepository
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.DeleteSecretKey
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ResetBiometricsImpl @Inject constructor(
    private val passphraseRepository: PassphraseRepository,
    private val useBiometricLoginRepository: UseBiometricLoginRepository,
    private val deleteSecretKey: DeleteSecretKey,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ResetBiometrics {
    override suspend fun invoke(): Result<Unit, ResetBiometricsError> = withContext(ioDispatcher) {
        passphraseRepository.deletePassphrase()
        useBiometricLoginRepository.saveUseBiometricLogin(isEnabled = false)
        return@withContext deleteSecretKey().mapError {
            it.toResetBiometricsError()
        }.onSuccess {
            Timber.d("Deletion succeeded")
        }
    }
}
