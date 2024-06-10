package ch.admin.foitt.pilotwallet.feature.credentialActivities.presentation.model

import androidx.compose.ui.graphics.painter.Painter
import ch.admin.foitt.pilotwallet.platform.credential.presentation.model.CredentialCardState
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityType
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimData

sealed interface ActivityDetailUiState {
    data object Loading : ActivityDetailUiState
    data class Success(
        val actor: String,
        val actorLogo: Painter?,
        val createdAt: String,
        val type: ActivityType,
        val cardState: CredentialCardState,
        val claims: List<CredentialClaimData>,
    ) : ActivityDetailUiState
}
