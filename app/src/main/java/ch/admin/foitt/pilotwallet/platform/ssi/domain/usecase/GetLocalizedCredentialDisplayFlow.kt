package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialDisplay
import kotlinx.coroutines.flow.Flow

interface GetLocalizedCredentialDisplayFlow {
    operator fun invoke(credentialId: Long): Flow<CredentialDisplay>
}
