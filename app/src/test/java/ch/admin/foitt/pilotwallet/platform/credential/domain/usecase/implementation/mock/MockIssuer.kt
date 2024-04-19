package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation.mock

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.JWSKeyPair
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerPublicKeyInfo
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.util.Base64
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.util.Date

object MockIssuer {
    fun createValidKeyPair(): JWSKeyPair {
        val generator = KeyPairGenerator.getInstance("EC")
        generator.initialize(ECGenParameterSpec("secp521r1"), SecureRandom())
        return createJwsKeyPair(generator)
    }

    private fun createJwsKeyPair(generator: KeyPairGenerator): JWSKeyPair {
        val keyPair = generator.generateKeyPair()
        return JWSKeyPair(
            jwsAlgorithm = JWSAlgorithm.ES512,
            keyPair = keyPair,
            keyId = "KEY_ID"
        )
    }

    fun createPublicKeyInfo(keyPair: JWSKeyPair): IssuerPublicKeyInfo {
        val publicKey = ECKey.Builder(Curve.P_521, keyPair.keyPair.public as ECPublicKey).build()

        return IssuerPublicKeyInfo(
            listOf(
                IssuerPublicKeyInfo.Key(
                    kty = "",
                    kid = "",
                    crv = Curve.P_521.toString(),
                    x = publicKey.x.toString(),
                    y = publicKey.y.toString(),
                )
            )
        )
    }

    fun issueCredential(
        keyPair: JWSKeyPair,
        payload: String,
    ) = createJwt(
        header = createHeader(keyPair = keyPair),
        payload = createPayload(payload),
        keyPair = keyPair
    )

    private fun createHeader(
        cryptographicBindingMethod: String = "supportedCryptographicBindingMethod",
        keyPair: JWSKeyPair
    ): JWSHeader {
        val publicKey = keyPair.toPublicECKey()
        val jwtKid = "$cryptographicBindingMethod:${Base64.encode(publicKey?.toJSONString())}"
        return JWSHeader
            .Builder(keyPair.jwsAlgorithm)
            .keyID(jwtKid)
            .type(JOSEObjectType.JWT)
            .build()
    }

    private fun JWSKeyPair.toPublicECKey() = when (val pub = keyPair.public) {
        is ECPublicKey -> pub.toPublicECKey(jwsAlgorithm)
        else -> null
    }

    private fun ECPublicKey.toPublicECKey(
        jwsAlgorithm: JWSAlgorithm
    ) = ECKey.Builder(jwsAlgorithm.toCurve(), this)
        .build()

    private fun JWSAlgorithm.toCurve() =
        Curve.forJWSAlgorithm(this).first()

    private fun createPayload(
        payload: String
    ) = JWTClaimsSet
        .Builder()
        .claim("vc", payload)
        .issueTime(Date())
        .build()

    private fun createJwt(
        header: JWSHeader,
        payload: JWTClaimsSet,
        keyPair: JWSKeyPair
    ): String {
        val jwt = SignedJWT(header, payload)
        val signer = ECDSASigner(keyPair.keyPair.private, keyPair.jwsAlgorithm.toCurve())
        jwt.sign(signer)
        return jwt.serialize()
    }
}
