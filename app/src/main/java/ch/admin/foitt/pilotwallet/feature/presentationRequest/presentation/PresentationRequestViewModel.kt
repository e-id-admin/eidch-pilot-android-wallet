package ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation

import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.openid4vc.domain.usecase.DeclinePresentation
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.model.PresentationRequestError
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.usecase.GeneratePresentationMetadata
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.usecase.SubmitPresentation
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetPainterFromUri
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.GetCredentialPreviewFlow
import ch.admin.foitt.pilotwallet.platform.credential.presentation.adapter.GetCredentialCardState
import ch.admin.foitt.pilotwallet.platform.di.IoDispatcherScope
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.ErrorDialogState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetErrorDialogState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimData
import ch.admin.foitt.pilotwallet.platform.utils.trackCompletion
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationDeclinedScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationFailureScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationRequestScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationSuccessScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationValidationErrorScreenDestination
import com.github.michaelbull.result.mapBoth
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PresentationRequestViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val generatePresentationMetadata: GeneratePresentationMetadata,
    private val submitPresentation: SubmitPresentation,
    private val declinePresentation: DeclinePresentation,
    getCredentialPreviewFlow: GetCredentialPreviewFlow,
    private val setErrorDialogState: SetErrorDialogState,
    @IoDispatcherScope private val ioDispatcherScope: CoroutineScope,
    private val getCredentialCardState: GetCredentialCardState,
    private val getPainterFromUri: GetPainterFromUri,
    savedStateHandle: SavedStateHandle,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState, addBottomSystemBarPaddings = false) {
    override val topBarState: TopBarState = TopBarState.None

    private val navArgs = PresentationRequestScreenDestination.argsFrom(savedStateHandle)
    private val compatibleCredential = navArgs.compatibleCredential
    private val presentationRequest = navArgs.presentationRequest

    val verifierName = navArgs.presentationRequest.clientMetaData?.clientName
    val verifierLogo: StateFlow<Painter?> = flow {
        emit(getPainterFromUri(navArgs.presentationRequest.clientMetaData?.logoUri))
    }.toStateFlow(null)

    private val _requestedData: MutableStateFlow<List<CredentialClaimData>> =
        MutableStateFlow(emptyList())
    val requestedClaims = _requestedData.asStateFlow()

    val credentialState = getCredentialPreviewFlow(compatibleCredential.credentialId).map { credentialPreview ->
        getCredentialCardState(credentialPreview)
    }

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting = _isSubmitting.asStateFlow()

    init {
        viewModelScope.launch {
            generatePresentationMetadata(compatibleCredential)
                .onSuccess { data ->
                    _requestedData.value = data.claims
                }.onFailure { error ->
                    setErrorDialogState(state = ErrorDialogState.Unexpected(error.toString()))
                    Timber.e("Could not generate presentation metadata: $error")
                }
        }.trackCompletion(_isLoading)
    }

    fun submit() {
        viewModelScope.launch {
            submitPresentation(
                presentationRequest = presentationRequest,
                compatibleCredential = compatibleCredential,
            ).mapBoth(
                success = { navigateToSuccess() },
                failure = { error ->
                    if (error is PresentationRequestError.ValidationError) {
                        navigateToValidationError()
                    } else {
                        navigateToFailure()
                    }
                },
            )
        }.trackCompletion(_isSubmitting)
    }

    fun onDecline() {
        ioDispatcherScope.launch {
            declinePresentation(presentationRequest.responseUri).onFailure { error ->
                Timber.w("Decline presentation error: $error")
            }
        }
        navManager.navigateToAndClearCurrent(PresentationDeclinedScreenDestination)
    }

    private fun navigateToSuccess() {
        navManager.navigateToAndClearCurrent(
            direction = PresentationSuccessScreenDestination(
                sentFields = getSentFields(),
            )
        )
    }

    private fun navigateToValidationError() {
        navManager.navigateToAndClearCurrent(
            direction = PresentationValidationErrorScreenDestination(
                sentFields = getSentFields(),
            )
        )
    }

    private fun getSentFields() =
        requestedClaims.value.map { claimData ->
            claimData.localizedKey
        }.toTypedArray()

    private fun navigateToFailure() = navManager.navigateToAndClearCurrent(
        PresentationFailureScreenDestination(
            compatibleCredential = compatibleCredential,
            presentationRequest = presentationRequest,
        )
    )
}
