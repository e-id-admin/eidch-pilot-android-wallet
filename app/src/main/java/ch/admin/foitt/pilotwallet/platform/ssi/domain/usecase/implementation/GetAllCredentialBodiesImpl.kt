package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.ParseSdJwt
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialBody
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetAllCredentialBodiesError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.toGetAllCredentialBodiesError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialRawRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetAllCredentialBodies
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class GetAllCredentialBodiesImpl @Inject constructor(
    private val credentialRawRepo: CredentialRawRepo,
    private val parseSdJwt: ParseSdJwt
) : GetAllCredentialBodies {
    override suspend fun invoke(): Result<List<CredentialBody>, GetAllCredentialBodiesError> = coroutineBinding {
        val credentials = credentialRawRepo.getAll().mapError { error ->
            error.toGetAllCredentialBodiesError()
        }.bind()
        credentials.map { credential ->
            val sdJwt = parseSdJwt(credential.payload).mapError { error ->
                error.toGetAllCredentialBodiesError()
            }.bind()
            CredentialBody(
                credentialId = credential.credentialId,
                body = sdJwt.jsonWithActualValues.toString()
            )
        }
    }
}
