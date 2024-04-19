package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.di.DefaultDispatcher
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOfferError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialRequestProofJwt
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.JWSKeyPair
import ch.admin.foitt.openid4vc.domain.usecase.CreateCredentialRequestProofJwt
import ch.admin.foitt.openid4vc.utils.toCurve
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

internal class CreateCredentialRequestProofJwtImpl @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : CreateCredentialRequestProofJwt {
    override suspend operator fun invoke(
        keyPair: JWSKeyPair,
        jwtKid: String,
        issuer: String,
        cNonce: String
    ) = withContext(defaultDispatcher) {
        binding {
            val header = createHeader(keyPair, jwtKid)
            val payload = createPayload(issuer, cNonce)
            val jwt = createJwt(
                header = header,
                payload = payload,
                keyPair = keyPair
            ).bind()
            CredentialRequestProofJwt(jwt)
        }
    }

    private fun createHeader(
        keyPair: JWSKeyPair,
        jwtKid: String
    ) = JWSHeader
        .Builder(keyPair.jwsAlgorithm)
        .keyID(jwtKid)
        .type(JOSEObjectType.JWT)
        .build()

    private fun createPayload(
        issuer: String,
        cNonce: String
    ) = JWTClaimsSet
        .Builder()
        .audience(issuer)
        .claim("nonce", cNonce)
        .issueTime(Date())
        .build()

    private fun createJwt(
        header: JWSHeader,
        payload: JWTClaimsSet,
        keyPair: JWSKeyPair
    ) = runSuspendCatching {
        val jwt = SignedJWT(header, payload)
        val signer = ECDSASigner(keyPair.keyPair.private, keyPair.jwsAlgorithm.toCurve())
        jwt.sign(signer)
        jwt.serialize()
    }.mapError { throwable ->
        CredentialOfferError.Unexpected(throwable)
    }
}
