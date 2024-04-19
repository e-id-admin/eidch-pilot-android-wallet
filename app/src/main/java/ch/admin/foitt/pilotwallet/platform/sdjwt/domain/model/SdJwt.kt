package ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model

import com.nimbusds.jwt.SignedJWT

data class SdJwt(
    val signedJwt: SignedJWT,
    val claims: Map<String, SdJwtClaim>
)
