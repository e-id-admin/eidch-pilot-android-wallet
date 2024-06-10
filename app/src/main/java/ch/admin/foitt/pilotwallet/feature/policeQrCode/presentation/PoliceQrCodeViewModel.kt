package ch.admin.foitt.pilotwallet.feature.policeQrCode.presentation

import androidx.lifecycle.SavedStateHandle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetDrawableFromData
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.utils.toPainter
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PoliceQrCodeScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class PoliceQrCodeViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val getDrawableFromData: GetDrawableFromData,
    setTopBarState: SetTopBarState,
    savedStateHandle: SavedStateHandle
) : ScreenViewModel(setTopBarState) {
    override val topBarState =
        TopBarState.Details(
            onUp = navManager::navigateUp,
            titleId = R.string.police_control_title
        )

    private val navArgs = PoliceQrCodeScreenDestination.argsFrom(savedStateHandle)

    val imagePainter = flow {
        emit(getDrawableFromData(navArgs.imageData)?.toPainter())
    }
}
