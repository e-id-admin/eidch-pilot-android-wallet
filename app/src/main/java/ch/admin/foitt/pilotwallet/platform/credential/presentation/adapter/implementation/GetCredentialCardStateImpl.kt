package ch.admin.foitt.pilotwallet.platform.credential.presentation.adapter.implementation

import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetColor
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetDrawableFromData
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CredentialPreview
import ch.admin.foitt.pilotwallet.platform.credential.presentation.adapter.GetCredentialCardState
import ch.admin.foitt.pilotwallet.platform.credential.presentation.model.CredentialCardState
import ch.admin.foitt.pilotwallet.platform.utils.toPainter
import javax.inject.Inject

internal class GetCredentialCardStateImpl @Inject constructor(
    private val getColor: GetColor,
    private val getDrawableFromData: GetDrawableFromData,
) : GetCredentialCardState {
    override suspend fun invoke(credentialPreview: CredentialPreview) = CredentialCardState(
        credentialId = credentialPreview.credentialId,
        title = credentialPreview.title,
        subtitle = credentialPreview.subtitle,
        status = credentialPreview.status,
        borderColor = getColor(credentialPreview.backgroundColor) ?: CredentialCardState.defaultCardColor,
        backgroundColor = getColor(credentialPreview.backgroundColor) ?: CredentialCardState.defaultCardColor,
        textColor = getColor(credentialPreview.textColor) ?: CredentialCardState.defaultCardTextColor,
        logo = getDrawableFromData(credentialPreview.logoData)?.toPainter(),
    )
}
