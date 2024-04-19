package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.GetKeyPairError
import ch.admin.foitt.openid4vc.domain.model.VerifiableCredentialConfig
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.CreateVerifiablePresentationTokenError
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestError
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.VerifiablePresentation
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.toCreateVerifiablePresentationError
import ch.admin.foitt.openid4vc.domain.usecase.CreateVerifiablePresentationToken
import ch.admin.foitt.openid4vc.domain.usecase.GetKeyPair
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.util.Base64
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.security.KeyPair
import java.security.interfaces.ECPublicKey
import java.util.UUID
import javax.inject.Inject

internal class CreateVerifiablePresentationTokenImpl @Inject constructor(
    private val getKeyPair: GetKeyPair,
) : CreateVerifiablePresentationToken {
    override suspend fun invoke(
        keyAlias: String,
        nonce: String,
        disclosedSdJwt: String,
        config: VerifiableCredentialConfig,
    ): Result<String, CreateVerifiablePresentationTokenError> = coroutineBinding {
        val algorithm = JWSAlgorithm.parse(config.algorithm)

        val keyPair = getKeyPair(keyAlias, config.keyStoreName)
            .mapError(GetKeyPairError::toCreateVerifiablePresentationError).bind()

        val publicKey = keyPair.toPublicECKey(algorithm)
            .mapError { it.toUnexpected() }.bind()

        val jwtKid = "${config.cryptographicBindingMethod}:${Base64.encode(publicKey.toJSONString())}"

        val verifiablePresentation = VerifiablePresentation(
            type = config.presentationType,
            verifiableCredential = disclosedSdJwt,
        )

        val claimSet = JWTClaimsSet.Builder()
            .claim(config.verifiableCredentialClaimKey, verifiablePresentation)
            .claim(config.nonceClaimKey, nonce)
            .jwtID(UUID.randomUUID().toString())
            .issuer(jwtKid)
            .build()

        val header = JWSHeader.Builder(algorithm)
            .type(JOSEObjectType.JWT)
            .keyID(jwtKid)
            .build()

        val signedJWT = SignedJWT(
            header,
            claimSet,
        )

        runSuspendCatching {
            val signer = ECDSASigner(keyPair.private, algorithm.toCurve())
            signedJWT.sign(signer)
        }.mapError { it.toUnexpected() }.bind()

        signedJWT.serialize()
    }

    //region Helpers
    private fun KeyPair.toPublicECKey(ecAlgorithm: JWSAlgorithm): Result<ECKey, Throwable> = runSuspendCatching {
        when (val pub = public) {
            is ECPublicKey -> pub.toPublicECKey(ecAlgorithm)
            else -> error("Unknown key")
        }
    }

    private fun ECPublicKey.toPublicECKey(
        jwsAlgorithm: JWSAlgorithm
    ) = ECKey.Builder(jwsAlgorithm.toCurve(), this)
        .build()

    private fun JWSAlgorithm.toCurve() =
        Curve.forJWSAlgorithm(this).first()

    private fun Throwable.toUnexpected() = PresentationRequestError.Unexpected(this)

    //endregion
}
