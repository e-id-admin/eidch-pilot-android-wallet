package ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.model

import ch.admin.foitt.openid4vc.domain.model.VerifiableCredentialConfig
import ch.admin.foitt.openid4vc.utils.Constants.ANDROID_KEY_STORE
import javax.inject.Inject

// TODO support presentation of credentials in a more generic way
class PresentationConfig @Inject constructor() : VerifiableCredentialConfig {
    override val cryptographicBindingMethod = "did:jwk"
    override val nonceClaimKey = "nonce"
    override val verifiableCredentialClaimKey = "vp"
    override val credentialPath = "\$.vp.verifiableCredential[0]"
    override val format = "jwt_vp_json"
    override val algorithm = "ES512"
    override val keyStoreName = ANDROID_KEY_STORE
    override val path: String = "$"
    override val presentationType: String = "VerifiablePresentation"
}
