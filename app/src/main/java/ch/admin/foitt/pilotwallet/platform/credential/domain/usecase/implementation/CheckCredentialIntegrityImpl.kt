package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.VerifiableCredential
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerConfiguration
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CheckCredentialIntegrityError
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.CheckCredentialIntegrity
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.VerifyJwt
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.VerifySdJwt
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class CheckCredentialIntegrityImpl @Inject constructor(
    private val verifyJwt: VerifyJwt,
    private val verifySdJwt: VerifySdJwt,
) : CheckCredentialIntegrity {
    override suspend fun invoke(
        issuerConfiguration: IssuerConfiguration,
        verifiableCredential: VerifiableCredential,
    ): Result<Boolean, CheckCredentialIntegrityError> = coroutineBinding {
        val isValidJwt = verifyJwt(issuerConfiguration.jwksUri, verifiableCredential.credential)
            .mapError { error ->
                error as CheckCredentialIntegrityError
            }.bind()

        val isValidSdJwt = verifySdJwt(verifiableCredential.credential)
            .mapError { error ->
                error as CheckCredentialIntegrityError
            }.bind()

        isValidJwt && isValidSdJwt
    }
}
