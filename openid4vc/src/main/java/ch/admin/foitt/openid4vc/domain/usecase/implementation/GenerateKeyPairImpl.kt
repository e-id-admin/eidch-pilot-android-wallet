package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.CreateES512KeyPairError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOfferError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchVerifiableCredentialError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.JWSKeyPair
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.SupportedCredential
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.toCredentialOfferError
import ch.admin.foitt.openid4vc.domain.usecase.CreateES512KeyPair
import ch.admin.foitt.openid4vc.domain.usecase.GenerateKeyPair
import ch.admin.foitt.openid4vc.utils.Constants.ANDROID_KEY_STORE
import ch.admin.foitt.openid4vc.utils.retryUseCase
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import com.nimbusds.jose.JWSAlgorithm
import javax.inject.Inject

internal class GenerateKeyPairImpl @Inject constructor(
    private val createES512KeyPair: CreateES512KeyPair,
) : GenerateKeyPair {
    override suspend fun invoke(
        supportedCredential: SupportedCredential
    ): Result<JWSKeyPair, FetchVerifiableCredentialError> = coroutineBinding {
        // TODO: support multiple cryptographic suites
        val cryptographicSuite = getCryptographicSuite(supportedCredential).bind()
        val keyPair = createKeyPair(cryptographicSuite).bind()

        keyPair
    }

    private fun getCryptographicSuite(supportedCredential: SupportedCredential) =
        when (val suite = supportedCredential.cryptographicSuitesSupported.firstOrNull()) {
            null -> Err(CredentialOfferError.UnsupportedCryptographicSuite)
            else -> Ok(suite)
        }

    private suspend fun createKeyPair(
        cryptographicSuite: String
    ): Result<JWSKeyPair, FetchVerifiableCredentialError> = when (cryptographicSuite) {
        JWSAlgorithm.ES512.name -> {
            retryUseCase { createES512KeyPair(JWSAlgorithm.ES512, ANDROID_KEY_STORE) }
                .mapError(CreateES512KeyPairError::toCredentialOfferError)
        }
        else -> Err(CredentialOfferError.UnsupportedCryptographicSuite)
    }
}
