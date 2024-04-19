package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOffer
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchIssuerCredentialInformationError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.SupportedCredential
import ch.admin.foitt.openid4vc.domain.usecase.FetchIssuerConfiguration
import ch.admin.foitt.openid4vc.domain.usecase.FetchIssuerCredentialInformation
import ch.admin.foitt.openid4vc.domain.usecase.FetchVerifiableCredential
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CredentialOfferError
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.FetchCredentialError
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.toFetchCredentialError
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.CheckCredentialIntegrity
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.FetchCredential
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.SaveCredential
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class FetchCredentialImpl @Inject constructor(
    private val fetchIssuerConfiguration: FetchIssuerConfiguration,
    private val fetchIssuerCredentialInformation: FetchIssuerCredentialInformation,
    private val fetchVerifiableCredential: FetchVerifiableCredential,
    private val checkCredentialIntegrity: CheckCredentialIntegrity,
    private val saveCredential: SaveCredential,
) : FetchCredential {
    override suspend fun invoke(credentialOffer: CredentialOffer): Result<Long, FetchCredentialError> = coroutineBinding {
        val issuerInfo = fetchIssuerCredentialInformation(credentialOffer.credentialIssuer)
            .mapError(FetchIssuerCredentialInformationError::toFetchCredentialError)
            .bind()
        val issuerConfig = fetchIssuerConfiguration(credentialOffer.credentialIssuer).mapError { error ->
            error as FetchCredentialError
        }.bind()
        val verifiableCredential = fetchVerifiableCredential(credentialOffer, issuerConfig, issuerInfo).mapError { error ->
            error.toFetchCredentialError()
        }.bind()
        val isCredentialValid = checkCredentialIntegrity(issuerConfig, verifiableCredential).mapError { error ->
            error as FetchCredentialError
        }.bind()
        if (isCredentialValid) {
            val credentialIdentifier = getCredentialIdentifier(credentialOffer.credentials, issuerInfo.supportedCredentials).bind()
            val credentialId = saveCredential(issuerInfo, verifiableCredential, credentialIdentifier).mapError { error ->
                error as FetchCredentialError
            }.bind()
            credentialId
        } else {
            Err(CredentialOfferError.IntegrityCheckFailed).bind<Long>()
        }
    }

    private fun getCredentialIdentifier(
        credentials: List<String>,
        supportedCredentials: Map<String, SupportedCredential>
    ): Result<String, FetchCredentialError> {
        val credentialTypes = supportedCredentials
            .filter { supportedCredential -> supportedCredential.key in credentials }
            .flatMap { it.value.credentialDefinition.type }
        return if (credentialTypes.isEmpty()) {
            Err(CredentialOfferError.UnsupportedCredentialType)
        } else {
            Ok(credentials.first()) // TODO: support requesting multiple credentials
        }
    }
}
