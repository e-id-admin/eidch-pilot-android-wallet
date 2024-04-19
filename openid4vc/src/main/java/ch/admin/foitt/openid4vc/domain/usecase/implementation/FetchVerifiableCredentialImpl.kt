package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.VerifiableCredential
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOffer
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOfferError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialRequestProofJwt
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchVerifiableCredentialError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.Grant
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerConfiguration
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerCredentialInformation
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.SupportedCredential
import ch.admin.foitt.openid4vc.domain.repository.CredentialOfferRepository
import ch.admin.foitt.openid4vc.domain.usecase.CreateCredentialRequestProofJwt
import ch.admin.foitt.openid4vc.domain.usecase.CreateDidJwk
import ch.admin.foitt.openid4vc.domain.usecase.DeleteKeyPair
import ch.admin.foitt.openid4vc.domain.usecase.FetchVerifiableCredential
import ch.admin.foitt.openid4vc.domain.usecase.GenerateKeyPair
import ch.admin.foitt.openid4vc.utils.retryUseCase
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.onFailure
import javax.inject.Inject

internal class FetchVerifiableCredentialImpl @Inject constructor(
    private val credentialOfferRepository: CredentialOfferRepository,
    private val generateKeyPair: GenerateKeyPair,
    private val createDidJwk: CreateDidJwk,
    private val createCredentialRequestProofJwt: CreateCredentialRequestProofJwt,
    private val deleteKeyPair: DeleteKeyPair,
) : FetchVerifiableCredential {
    override suspend fun invoke(
        credentialOffer: CredentialOffer,
        issuerConfiguration: IssuerConfiguration,
        credentialInformation: IssuerCredentialInformation
    ) = coroutineBinding {
        val issuerEndpoint = credentialOffer.credentialIssuer
        if (credentialOffer.credentials.isEmpty()) {
            return@coroutineBinding Err(CredentialOfferError.UnsupportedCredentialType).bind<VerifiableCredential>()
        }
        val credentialId = credentialOffer.credentials.first()

        val supportedCredential = getSupportedCredential(credentialId, credentialInformation).bind()

        if (supportedCredential.proofTypesSupported.contains(CredentialRequestProofJwt.TYPE).not()) {
            return@coroutineBinding Err(CredentialOfferError.UnsupportedProofType).bind<VerifiableCredential>()
        }

        val keyPair = generateKeyPair(supportedCredential).bind()

        val didJwk = createDidJwk(supportedCredential, keyPair)
            .onFailure { deleteKeyPair(keyPair.keyId) }
            .bind()

        val tokenResponse = getToken(issuerConfiguration, credentialOffer.grants)
            .onFailure { deleteKeyPair(keyPair.keyId) }
            .bind()

        val credentialRequestJwt = retryUseCase {
            createCredentialRequestProofJwt(
                keyPair = keyPair,
                jwtKid = didJwk,
                issuer = issuerEndpoint,
                cNonce = tokenResponse.cNonce
            )
        }
            .onFailure { deleteKeyPair(keyPair.keyId) }
            .bind()

        val credentialResponse = credentialOfferRepository.fetchCredential(
            credentialInformation.credentialEndpoint,
            credentialId,
            supportedCredential.format,
            credentialRequestJwt,
            tokenResponse.accessToken
        )
            .onFailure { deleteKeyPair(keyPair.keyId) }
            .bind()

        VerifiableCredential(
            credential = credentialResponse.credential,
            format = credentialResponse.format,
            signingKeyId = keyPair.keyId,
        )
    }

    private fun getSupportedCredential(
        credentialId: String,
        credentialInformation: IssuerCredentialInformation
    ): Result<SupportedCredential, FetchVerifiableCredentialError> {
        val credential = credentialInformation.supportedCredentials[credentialId]
        return if (credential != null) {
            Ok(credential)
        } else {
            Err(CredentialOfferError.UnsupportedCredentialType)
        }
    }

    private suspend fun getToken(config: IssuerConfiguration, grant: Grant) =
        if (grant.preAuthorizedCode != null) {
            credentialOfferRepository.fetchAccessToken(
                config.tokenEndpoint,
                grant.preAuthorizedCode.preAuthorizedCode
            )
        } else {
            Err(CredentialOfferError.UnsupportedGrantType)
        }
}
