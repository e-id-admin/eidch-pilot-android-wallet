package ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.GetCipherForDecryptionError
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.KeystoreKeyConfig
import com.github.michaelbull.result.Result
import javax.crypto.Cipher

fun interface GetCipherForDecryption {
    @CheckResult
    suspend operator fun invoke(
        keystoreKeyConfig: KeystoreKeyConfig,
        initializationVector: ByteArray,
    ): Result<Cipher, GetCipherForDecryptionError>
}
