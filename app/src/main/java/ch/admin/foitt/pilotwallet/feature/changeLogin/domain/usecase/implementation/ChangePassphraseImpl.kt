package ch.admin.foitt.pilotwallet.feature.changeLogin.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.feature.changeLogin.domain.model.ChangePassphraseError
import ch.admin.foitt.pilotwallet.feature.changeLogin.domain.model.toChangePassphraseError
import ch.admin.foitt.pilotwallet.feature.changeLogin.domain.usecase.ChangePassphrase
import ch.admin.foitt.pilotwallet.platform.appSetupState.domain.repository.UseBiometricLoginRepository
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.ResetBiometrics
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.ChangeDatabasePassphrase
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.SavePassphraseWasDeleted
import ch.admin.foitt.pilotwallet.platform.passphraseHashing.domain.repository.SaltRepository
import ch.admin.foitt.pilotwallet.platform.passphraseHashing.domain.usecase.HashPassphrase
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.repository.PepperIvRepository
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.usecase.PepperPassphrase
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class ChangePassphraseImpl @Inject constructor(
    private val hashPassphrase: HashPassphrase,
    private val saltRepository: SaltRepository,
    private val pepperPassphrase: PepperPassphrase,
    private val pepperIvRepository: PepperIvRepository,
    private val changeDatabasePassphrase: ChangeDatabasePassphrase,
    private val useBiometricLoginRepository: UseBiometricLoginRepository,
    private val resetBiometrics: ResetBiometrics,
    private val savePassphraseWasDeleted: SavePassphraseWasDeleted,
) : ChangePassphrase {
    override suspend fun invoke(newPin: String): Result<Unit, ChangePassphraseError> =
        coroutineBinding {
            val pinHash = hashPassphrase(
                pin = newPin,
                initializeSalt = true,
            ).mapError { error ->
                error.toChangePassphraseError()
            }.bind()

            val pinHashPeppered = pepperPassphrase(
                passphrase = pinHash.hash,
                initializePepper = true,
            ).mapError { error ->
                error.toChangePassphraseError()
            }.bind()

            changeDatabasePassphrase(
                newPassphrase = pinHashPeppered.hash,
            ).mapError { error ->
                error.toChangePassphraseError()
            }.bind()

            saltRepository.save(pinHash.salt)
            pepperIvRepository.save(pinHashPeppered.initializationVector)

            // Biometric login will not work anymore
            val wasBiometricsEnabled = useBiometricLoginRepository.getUseBiometricLogin()
            resetBiometrics()
            savePassphraseWasDeleted(wasBiometricsEnabled)
        }
}
