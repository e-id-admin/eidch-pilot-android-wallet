package ch.admin.foitt.pilotwallet.feature.recentCredentialActivities.presentation

import ch.admin.foitt.pilotwallet.platform.activity.presentation.model.ActivityListItem
import ch.admin.foitt.pilotwallet.platform.credential.presentation.model.CredentialCardState

sealed interface RecentCredentialActivitiesUiState {
    data object Loading : RecentCredentialActivitiesUiState
    data class Success(
        val credentialCardState: CredentialCardState,
        val activities: List<ActivityListItem>,
    ) : RecentCredentialActivitiesUiState
}
