package ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase

import ch.admin.foitt.pilotwallet.platform.passphrase.domain.model.InitializePassphraseError
import com.github.michaelbull.result.Result
import javax.crypto.Cipher

fun interface InitializePassphrase {
    suspend operator fun invoke(
        pin: String,
        encryptionCipher: Cipher?,
    ): Result<Unit, InitializePassphraseError>
}
