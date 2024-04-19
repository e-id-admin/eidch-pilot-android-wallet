package ch.admin.foitt.pilotwallet.platform.credential.domain.model

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus

data class CredentialPreview(
    val credentialId: Long,
    val title: String,
    val subtitle: String?,
    val status: CredentialStatus,
    val logoData: String?,
    val backgroundColor: String?,
    val textColor: String?,
)

fun CredentialDisplay.toCredentialPreview(
    status: CredentialStatus?,
) = CredentialPreview(
    credentialId = credentialId,
    title = name,
    subtitle = description,
    status = status ?: CredentialStatus.UNKNOWN,
    logoData = logoData,
    backgroundColor = backgroundColor,
    textColor = textColor,
)
