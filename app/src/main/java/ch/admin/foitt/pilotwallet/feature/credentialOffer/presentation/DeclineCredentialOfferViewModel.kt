package ch.admin.foitt.pilotwallet.feature.credentialOffer.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.DeleteCredential
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialOfferScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.DeclineCredentialOfferScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.HomeScreenDestination
import com.github.michaelbull.result.onFailure
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DeclineCredentialOfferViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val deleteCredential: DeleteCredential,
    setTopBarState: SetTopBarState,
    savedStateHandle: SavedStateHandle,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.SystemBarPadding

    private val navArgs = DeclineCredentialOfferScreenDestination.argsFrom(savedStateHandle)

    fun onCancel() {
        navManager.popBackStack()
    }

    fun onDecline() {
        viewModelScope.launch {
            deleteCredential(navArgs.credentialId).onFailure { error ->
                when (error) {
                    is SsiError.Unexpected -> Timber.e(error.cause)
                }
            }

            val isHomeOnBackstack = navManager.popBackStackTo(HomeScreenDestination, false)
            if (!isHomeOnBackstack) {
                navManager.navigateToAndPopUpTo(
                    direction = HomeScreenDestination,
                    route = CredentialOfferScreenDestination.route
                )
            }
        }
    }
}
