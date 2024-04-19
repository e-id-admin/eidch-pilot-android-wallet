package ch.admin.foitt.openid4vc.domain.model.credentialoffer

import com.nimbusds.jose.JWSAlgorithm
import java.security.KeyPair

interface SigningKeyPair {
    val keyPair: KeyPair
    val keyId: String
}

data class JWSKeyPair(
    val jwsAlgorithm: JWSAlgorithm,
    override val keyPair: KeyPair,
    override val keyId: String
) : SigningKeyPair
