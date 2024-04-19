package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialDisplay
import ch.admin.foitt.pilotwallet.platform.locale.domain.usecase.GetLocalizedDisplay
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialDisplayRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetLocalizedCredentialDisplayFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class GetLocalizedCredentialDisplayFlowImpl @Inject constructor(
    private val credentialDisplayRepo: CredentialDisplayRepo,
    private val getLocalizedDisplay: GetLocalizedDisplay
) : GetLocalizedCredentialDisplayFlow {

    override fun invoke(credentialId: Long): Flow<CredentialDisplay> =
        credentialDisplayRepo.getCredentialDisplaysFlow(credentialId).mapNotNull { credentialDisplays ->
            if (credentialDisplays.isNotEmpty()) getLocalizedDisplay(credentialDisplays) else null
        }.distinctUntilChanged()
}
