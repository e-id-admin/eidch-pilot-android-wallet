package ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.model.GetCredentialRawsError
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.model.toGetCredentialRawsError
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.usecase.GetCredentialRaws
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialRaw
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialRawRepo
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class GetCredentialRawsImpl @Inject constructor(
    private val credentialRawRepository: CredentialRawRepo
) : GetCredentialRaws {
    override suspend fun invoke(): Result<List<CredentialRaw>, GetCredentialRawsError> =
        credentialRawRepository.getAll().mapError { error -> error.toGetCredentialRawsError() }
}
