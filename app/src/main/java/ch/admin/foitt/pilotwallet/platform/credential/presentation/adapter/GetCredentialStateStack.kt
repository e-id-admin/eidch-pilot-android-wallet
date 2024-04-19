package ch.admin.foitt.pilotwallet.platform.credential.presentation.adapter

import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CredentialPreview
import ch.admin.foitt.pilotwallet.platform.credential.presentation.model.CredentialCardState

fun interface GetCredentialStateStack {
    suspend operator fun invoke(
        credentialPreviews: List<CredentialPreview>
    ): List<CredentialCardState>
}
