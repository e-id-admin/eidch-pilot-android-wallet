package ch.admin.foitt.pilotwallet.feature.credentialDelete.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.DeleteCredential
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.RecentCredentialActivitiesScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CredentialDeleteViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val deleteCredential: DeleteCredential,
    setTopBarState: SetTopBarState,
    savedStateHandle: SavedStateHandle
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.None

    private val navArgs = RecentCredentialActivitiesScreenDestination.argsFrom(savedStateHandle)

    fun onDelete() {
        viewModelScope.launch {
            deleteCredential(credentialId = navArgs.credentialId)
            navManager.popBackStackTo(RecentCredentialActivitiesScreenDestination, true)
        }
    }

    fun onDismiss() {
        navManager.navigateUp()
    }
}
