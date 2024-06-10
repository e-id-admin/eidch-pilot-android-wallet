package ch.admin.foitt.pilotwallet.feature.credentialActivities.presentation

import ch.admin.foitt.pilotwallet.platform.activity.presentation.model.ActivityListItem

sealed interface CredentialActivitiesUiState {
    data object Loading : CredentialActivitiesUiState
    data object Empty : CredentialActivitiesUiState
    data class Activities(
        val activities: Map<String, List<ActivityListItem>>
    ) : CredentialActivitiesUiState
}
