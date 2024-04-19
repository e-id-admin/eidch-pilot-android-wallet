package ch.admin.foitt.openid4vc.domain.model

interface VerifiableCredentialConfig {
    val cryptographicBindingMethod: String
    val nonceClaimKey: String
    val verifiableCredentialClaimKey: String
    val credentialPath: String
    val format: String
    val algorithm: String
    val keyStoreName: String
    val path: String
    val presentationType: String
}
