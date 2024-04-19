package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchIssuerConfigurationError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerConfiguration
import ch.admin.foitt.openid4vc.domain.repository.CredentialOfferRepository
import ch.admin.foitt.openid4vc.domain.usecase.FetchIssuerConfiguration
import com.github.michaelbull.result.Result
import javax.inject.Inject

internal class FetchIssuerConfigurationImpl @Inject constructor(
    private val credentialOfferRepository: CredentialOfferRepository,
) : FetchIssuerConfiguration {
    override suspend fun invoke(issuerEndpoint: String): Result<IssuerConfiguration, FetchIssuerConfigurationError> =
        credentialOfferRepository.fetchIssuerConfiguration(issuerEndpoint)
}
