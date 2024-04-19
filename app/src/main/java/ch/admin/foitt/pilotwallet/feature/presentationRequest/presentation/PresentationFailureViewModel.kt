package ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation

import androidx.lifecycle.SavedStateHandle
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetLocalizedDateTime
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.extension.navigateUpOrToRoot
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationRequestScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class PresentationFailureViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val getLocalizedDateTime: GetLocalizedDateTime,
    savedStateHandle: SavedStateHandle,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(
    setTopBarState = setTopBarState,
    addBottomSystemBarPaddings = false,
) {
    override val topBarState = TopBarState.None

    private val navArgs = PresentationRequestScreenDestination.argsFrom(savedStateHandle)
    private val compatibleCredential = navArgs.compatibleCredential
    private val presentationRequest = navArgs.presentationRequest

    private val eventTime = ZonedDateTime.now()
    val dateTime: String get() = getLocalizedDateTime(eventTime)

    fun onRetry() = navManager.navigateToAndClearCurrent(
        direction = PresentationRequestScreenDestination(
            compatibleCredential = compatibleCredential,
            presentationRequest = presentationRequest,
        )
    )

    fun onClose() = navManager.navigateUpOrToRoot()
}
