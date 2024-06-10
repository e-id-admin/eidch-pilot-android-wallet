package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock

import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CredentialPreview
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus

object MockCredential {
    const val CREDENTIAL_ID = 2L
    val CREDENTIAL_STATUS = CredentialStatus.VALID
    const val TITLE = "credentialTitle"
    const val SUBTITLE = "credentialSubtitle"
    const val LOGO_DATA = "credentialLogoData"
    const val BACKGROUND_COLOR = "credentialBackgroundColor"
    const val TEXT_COLOR = "credentialTextColor"
    val credentialDisplay = CredentialDisplay(
        credentialId = CREDENTIAL_ID,
        locale = "locale",
        name = TITLE,
        description = SUBTITLE,
        logoUrl = null,
        logoData = LOGO_DATA,
        logoAltText = null,
        backgroundColor = BACKGROUND_COLOR,
        textColor = TEXT_COLOR,
    )
    val credentialDisplays = listOf(credentialDisplay)
    val expectedCredentialPreview = CredentialPreview(
        credentialId = CREDENTIAL_ID,
        title = TITLE,
        subtitle = SUBTITLE,
        status = CREDENTIAL_STATUS,
        logoData = LOGO_DATA,
        backgroundColor = BACKGROUND_COLOR,
        textColor = TEXT_COLOR,
    )
}
