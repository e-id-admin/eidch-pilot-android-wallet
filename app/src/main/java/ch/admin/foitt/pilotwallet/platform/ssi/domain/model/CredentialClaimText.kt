package ch.admin.foitt.pilotwallet.platform.ssi.domain.model

data class CredentialClaimText(
    override val localizedKey: String,
    val value: String,
) : CredentialClaimData
