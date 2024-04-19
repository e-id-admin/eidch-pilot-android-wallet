package ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase

import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.DeleteSecretKeyError
import com.github.michaelbull.result.Result

interface DeleteSecretKey {
    operator fun invoke(): Result<Unit, DeleteSecretKeyError>
}
