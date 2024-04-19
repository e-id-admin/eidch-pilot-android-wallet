package ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.usecase.GetCipherForEncryption
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.model.PassphrasePepperKeyConfig
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.model.PepperPassphraseError
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.model.PepperedData
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.model.toPepperPassphraseError
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.repository.PepperIvRepository
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.usecase.PepperPassphrase
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import javax.inject.Inject

internal class PepperPassphraseImpl @Inject constructor(
    private val getCipherForEncryption: GetCipherForEncryption,
    private val pepperIvRepository: PepperIvRepository,
    private val passphrasePepperKeyConfig: PassphrasePepperKeyConfig,
) : PepperPassphrase {
    override suspend fun invoke(
        passphrase: ByteArray,
        initializePepper: Boolean,
    ): Result<PepperedData, PepperPassphraseError> {
        return coroutineBinding {
            val pepperInitializationVector: ByteArray? = if (!initializePepper) {
                pepperIvRepository.get()
            } else {
                null
            }

            val pepperCipher = getCipherForEncryption(
                passphrasePepperKeyConfig,
                initializationVector = pepperInitializationVector,
            ).mapError { error ->
                error.toPepperPassphraseError()
            }.bind()

            val passphraseBytes: ByteArray = runSuspendCatching {
                pepperCipher.doFinal(passphrase)
            }.mapError { throwable ->
                PepperPassphraseError.Unexpected(throwable)
            }.bind()

            PepperedData(passphraseBytes, pepperCipher.iv)
        }
    }
}
