package ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation

import androidx.lifecycle.SavedStateHandle
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.extension.navigateUpOrToRoot
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationSuccessScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PresentationValidationErrorViewModel @Inject constructor(
    private val navManager: NavigationManager,
    savedStateHandle: SavedStateHandle,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(
    setTopBarState = setTopBarState,
    addBottomSystemBarPaddings = false,
) {
    override val topBarState = TopBarState.None

    private val navArg = PresentationSuccessScreenDestination.argsFrom(savedStateHandle)

    val fields = navArg.sentFields.toList()

    fun onClose() = navManager.navigateUpOrToRoot()
}
