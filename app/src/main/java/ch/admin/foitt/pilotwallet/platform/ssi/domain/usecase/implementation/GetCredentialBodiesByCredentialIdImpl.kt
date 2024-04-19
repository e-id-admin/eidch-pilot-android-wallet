package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.ParseSdJwt
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialBody
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCredentialBodiesByCredentialIdError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.toGetCredentialBodiesByCredentialIdError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialRawRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialBodiesByCredentialId
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class GetCredentialBodiesByCredentialIdImpl @Inject constructor(
    private val credentialRawRepo: CredentialRawRepo,
    private val parseSdJwt: ParseSdJwt
) : GetCredentialBodiesByCredentialId {
    override suspend fun invoke(credentialId: Long): Result<List<CredentialBody>, GetCredentialBodiesByCredentialIdError> =
        coroutineBinding {
            val credentialRaws = credentialRawRepo.getByCredentialId(credentialId)
                .mapError { error ->
                    error.toGetCredentialBodiesByCredentialIdError()
                }.bind()
            credentialRaws.map { credentialRaw ->
                val sdJwt = parseSdJwt(credentialRaw.payload).mapError { error ->
                    error.toGetCredentialBodiesByCredentialIdError()
                }.bind()
                CredentialBody(
                    credentialId = credentialRaw.credentialId,
                    body = sdJwt.jsonWithActualValues.toString()
                )
            }
        }
}
