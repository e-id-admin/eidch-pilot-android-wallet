package ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.usecase

import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.model.GetCredentialRawsError
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialRaw
import com.github.michaelbull.result.Result

interface GetCredentialRaws {
    suspend operator fun invoke(): Result<List<CredentialRaw>, GetCredentialRawsError>
}
