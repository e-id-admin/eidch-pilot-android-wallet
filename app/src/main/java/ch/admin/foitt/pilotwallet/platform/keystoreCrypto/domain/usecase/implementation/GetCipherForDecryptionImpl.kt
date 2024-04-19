package ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.usecase.implementation

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.GetCipherForDecryptionError
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.KeystoreKeyConfig
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.toGetCipherForDecryptionError
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.usecase.GetCipherForDecryption
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.usecase.GetOrCreateSecretKey
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.getOrThrow
import com.github.michaelbull.result.mapError
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

internal class GetCipherForDecryptionImpl @Inject constructor(
    private val getOrCreateSecretKey: GetOrCreateSecretKey,
) : GetCipherForDecryption {
    @CheckResult
    override suspend fun invoke(
        keystoreKeyConfig: KeystoreKeyConfig,
        initializationVector: ByteArray,
    ): Result<Cipher, GetCipherForDecryptionError> = runSuspendCatching {
        val cipher = Cipher.getInstance(keystoreKeyConfig.encryptionTransformation)
        val secretKey = getOrCreateSecretKey(keystoreKeyConfig).getOrThrow { it.throwable }
        val gcmParameterSpec = GCMParameterSpec(keystoreKeyConfig.gcmAuthTagLength, initializationVector)

        cipher.init(
            Cipher.DECRYPT_MODE,
            secretKey,
            gcmParameterSpec,
        )
        cipher
    }.mapError { throwable ->
        throwable.toGetCipherForDecryptionError()
    }
}
