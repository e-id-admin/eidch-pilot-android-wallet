package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchIssuerPublicKeyInfoError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerPublicKeyInfo
import ch.admin.foitt.openid4vc.domain.repository.CredentialOfferRepository
import ch.admin.foitt.openid4vc.domain.usecase.FetchIssuerPublicKeyInfo
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

internal class FetchIssuerPublicKeyInfoImpl @Inject constructor(
    private val credentialOfferRepository: CredentialOfferRepository,
) : FetchIssuerPublicKeyInfo {
    override suspend fun invoke(issuerEndpoint: String): Result<IssuerPublicKeyInfo, FetchIssuerPublicKeyInfoError> = coroutineBinding {
        val issuerPublicKeyInfo = credentialOfferRepository.fetchIssuerPublicKeyInfo(issuerEndpoint)
            .mapError { error ->
                error
            }.bind()
        issuerPublicKeyInfo
    }
}
