package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOfferError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchVerifiableCredentialError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.JWSKeyPair
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.SupportedCredential
import ch.admin.foitt.openid4vc.domain.usecase.CreateDidJwk
import ch.admin.foitt.openid4vc.utils.toCurve
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.toErrorIfNull
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.util.Base64
import java.security.interfaces.ECPublicKey
import javax.inject.Inject

class CreateDidJwkImpl @Inject constructor() : CreateDidJwk {
    override suspend fun invoke(
        supportedCredential: SupportedCredential,
        keyPair: JWSKeyPair,
    ): Result<String, FetchVerifiableCredentialError> = binding {
        val cryptographicBindingMethod = getCryptographicBindingMethod(supportedCredential)
        val publicKey = keyPair.toPublicECKey().bind()

        "$cryptographicBindingMethod:${Base64.encode(publicKey.toJSONString())}"
    }

    private fun getCryptographicBindingMethod(supportedCredential: SupportedCredential) =
        // TODO: support multiple cryptographic binding methods
        when (val method = supportedCredential.cryptographicBindingMethodsSupported?.firstOrNull()) {
            null -> "did:jwk"
            else -> method
        }

    private fun JWSKeyPair.toPublicECKey() = runSuspendCatching {
        when (val pub = keyPair.public) {
            is ECPublicKey -> pub.toPublicECKey(jwsAlgorithm)
            else -> null
        }
    }.mapError { throwable ->
        CredentialOfferError.Unexpected(throwable)
    }.toErrorIfNull {
        CredentialOfferError.UnsupportedCryptographicSuite
    }

    private fun ECPublicKey.toPublicECKey(
        jwsAlgorithm: JWSAlgorithm
    ): ECKey = ECKey.Builder(jwsAlgorithm.toCurve(), this).build()
}
