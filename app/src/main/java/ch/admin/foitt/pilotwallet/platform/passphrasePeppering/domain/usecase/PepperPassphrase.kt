package ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.usecase

import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.model.PepperPassphraseError
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.model.PepperedData
import com.github.michaelbull.result.Result

fun interface PepperPassphrase {
    suspend operator fun invoke(
        passphrase: ByteArray,
        initializePepper: Boolean,
    ): Result<PepperedData, PepperPassphraseError>
}
