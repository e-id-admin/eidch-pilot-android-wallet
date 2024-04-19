package ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.passphrase.domain.repository.PassphraseRepository
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.GetPassphraseWasDeleted
import javax.inject.Inject

class GetPassphraseWasDeletedImpl @Inject constructor(
    private val passphraseRepository: PassphraseRepository,
) : GetPassphraseWasDeleted {
    override suspend fun invoke() = passphraseRepository.getPassphraseWasDeleted()
}
