package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.usecase.FetchIssuerPublicKeyInfo
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CredentialOfferError
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.VerifyJwtError
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.toVerifyJwtError
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.VerifyJwt
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.VerifyJwtTimestamps
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import com.nimbusds.jose.crypto.ECDSAVerifier
import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.util.Base64URL
import com.nimbusds.jwt.SignedJWT
import javax.inject.Inject

class VerifyJwtImpl @Inject constructor(
    private val fetchIssuerPublicKeyInfo: FetchIssuerPublicKeyInfo,
    private val verifyJwtTimestamps: VerifyJwtTimestamps,
) : VerifyJwt {
    override suspend fun invoke(jwksUri: String, credential: String): Result<Boolean, VerifyJwtError> = coroutineBinding {
        val publicKeyInfoList = fetchIssuerPublicKeyInfo(jwksUri)
            .mapError { error ->
                error.toVerifyJwtError()
            }.bind()

        val credentialJWT = runSuspendCatching {
            val matchResult = Regex(SD_JWT_PATTERN).matchEntire(credential)
            val jwt = matchResult?.groups?.let { groupCollection ->
                val groups = groupCollection as MatchNamedGroupCollection
                groups[JWT]?.value ?: ""
            } ?: ""
            jwt
        }.mapError {
            CredentialOfferError.Unexpected(it)
        }.bind()

        val signedJWT = runSuspendCatching {
            SignedJWT.parse(credentialJWT)
        }.mapError {
            CredentialOfferError.Unexpected(it)
        }.bind()

        val hasValidSignature = runSuspendCatching {
            val verifiers = publicKeyInfoList.keys.map { publicKey ->
                ECDSAVerifier(ECKey.Builder(Curve(publicKey.crv), Base64URL(publicKey.x), Base64URL(publicKey.y)).build())
            }
            verifiers.any { verifier ->
                signedJWT.verify(verifier)
            }
        }.mapError {
            CredentialOfferError.Unexpected(it)
        }.bind()

        val hasValidTimestamps = verifyJwtTimestamps(signedJWT)
            .mapError { error ->
                error as VerifyJwtError
            }.bind()

        hasValidSignature && hasValidTimestamps
    }

    companion object {
        private const val JWT = "jwt"
        private const val DISCLOSURES = "disclosures"
        private const val KEYBINDING_JWT = "keyBindingJwt"
        private const val SD_JWT_SEPARATOR = '~'
        private const val SD_JWT_PATTERN = "^" +
            "(?<$JWT>(?<header>[A-Za-z0-9-_]+)\\.(?<body>[A-Za-z0-9-_]+)\\.(?<signature>[A-Za-z0-9-_]+))" + // 1 issuer-signed JWT
            "($SD_JWT_SEPARATOR?" + // 0..1 separators
            "(?<$DISCLOSURES>(([A-Za-z0-9-_]+)$SD_JWT_SEPARATOR)+)?" + // 0..* Disclosures + "~"
            "(?<$KEYBINDING_JWT>([A-Za-z0-9-_]+)\\.([A-Za-z0-9-_]+)\\.([A-Za-z0-9-_]+))?" + // 0..1 Key Binding JWT
            ")\$"
    }
}
