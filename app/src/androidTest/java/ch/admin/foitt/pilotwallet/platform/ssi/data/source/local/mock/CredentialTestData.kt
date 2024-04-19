package ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock

import ch.admin.foitt.pilotwallet.platform.database.domain.model.Credential
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaimDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialIssuerDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialRaw
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DisplayLanguage

object CredentialTestData {
    const val value = "VALUE"
    const val key = "KEY"
    const val name1 = "NAME1"
    const val correct = "CORRECT"
    const val fallback = "FALLBACK"

    private const val name2 = "NAME2"
    private const val identifier = "IDENTIFIER"

    val credential1 = Credential(id = 1, status = CredentialStatus.VALID, createdAt = 1, updatedAt = 1)
    val credential2 = Credential(id = 2, status = CredentialStatus.VALID, createdAt = 2, updatedAt = 2)

    val credentialClaim1 = CredentialClaim(id = 1, credentialId = 1, key = key, value = value, valueType = null)
    val credentialClaim2 = CredentialClaim(id = 2, credentialId = 2, key = key, value = value, valueType = null)

    val credentialDisplay1 = CredentialDisplay(id = 1, credentialId = credential1.id, locale = "xx_XX", name = correct)
    val credentialDisplay2 =
        CredentialDisplay(id = 2, credentialId = credential2.id, locale = DisplayLanguage.FALLBACK, name = fallback)
    val credentialDisplay3 =
        CredentialDisplay(id = 3, credentialId = credential1.id, locale = DisplayLanguage.FALLBACK, name = fallback)

    val credentialClaimDisplay1 = CredentialClaimDisplay(id = 1, claimId = credentialClaim1.id, name = name1, locale = "xx")
    val credentialClaimDisplay2 = CredentialClaimDisplay(id = 2, claimId = credentialClaim2.id, name = name2, locale = "xx_XX")

    val credentialIssuerDisplay1 = CredentialIssuerDisplay(id = 1, credentialId = credential1.id, name = name1, locale = "xx")
    val credentialIssuerDisplay2 = CredentialIssuerDisplay(id = 2, credentialId = credential2.id, name = name2, locale = "xx_XX")

    val credentialRaw1 = CredentialRaw(id = 1, credentialId = credential1.id, keyIdentifier = identifier, payload = value, format = value)
    val credentialRaw2 = CredentialRaw(id = 2, credentialId = credential2.id, keyIdentifier = identifier, payload = value, format = value)
}
