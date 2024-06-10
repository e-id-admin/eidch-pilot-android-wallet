package ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation

import androidx.lifecycle.SavedStateHandle
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetDrawableFromUri
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.GetCredentialPreviewsFlow
import ch.admin.foitt.pilotwallet.platform.credential.presentation.adapter.GetCredentialCardState
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.PresentationRequestNavArg
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.extension.navigateUpOrToRoot
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.utils.toPainter
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationCredentialListScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationRequestScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class PresentationCredentialListViewModel @Inject constructor(
    private val navManager: NavigationManager,
    getCredentialPreviewsFlow: GetCredentialPreviewsFlow,
    private val getCredentialCardState: GetCredentialCardState,
    private val getDrawableFromUri: GetDrawableFromUri,
    savedStateHandle: SavedStateHandle,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState, addBottomSystemBarPaddings = false) {
    override val topBarState = TopBarState.None

    private val navArgs = PresentationCredentialListScreenDestination.argsFrom(savedStateHandle)

    val verifierName = navArgs.presentationRequest.clientMetaData?.clientName
    val verifierLogo = flow {
        emit(getDrawableFromUri(navArgs.presentationRequest.clientMetaData?.logoUri)?.toPainter())
    }.toStateFlow(null)

    val credentialStates = getCredentialPreviewsFlow().map { allPreviews ->
        _isLoading.value = false
        val credentialIds = navArgs.compatibleCredentials.map { it.credentialId }
        allPreviews
            .filter { it.credentialId in credentialIds }
            .map { getCredentialCardState(it) }
    }.toStateFlow(emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    fun onCredentialSelected(index: Int) {
        navManager.navigateToAndClearCurrent(
            direction = PresentationRequestScreenDestination(
                navArgs = PresentationRequestNavArg(
                    navArgs.compatibleCredentials[index],
                    navArgs.presentationRequest,
                )
            )
        )
    }

    fun onBack() = navManager.navigateUpOrToRoot()
}
