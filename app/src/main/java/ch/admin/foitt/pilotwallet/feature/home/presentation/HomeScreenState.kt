package ch.admin.foitt.pilotwallet.feature.home.presentation

import ch.admin.foitt.pilotwallet.platform.activity.presentation.model.HomeScreenActivity
import ch.admin.foitt.pilotwallet.platform.credential.presentation.model.CredentialCardState

sealed interface HomeScreenState {
    data object Initial : HomeScreenState
    data object Introduction : HomeScreenState
    data class Credentials(
        val credentials: List<CredentialCardState>,
        val latestActivity: HomeScreenActivity?,
        val onCredentialClick: (Long) -> Unit,
    ) : HomeScreenState
    data object NoCredential : HomeScreenState
}
