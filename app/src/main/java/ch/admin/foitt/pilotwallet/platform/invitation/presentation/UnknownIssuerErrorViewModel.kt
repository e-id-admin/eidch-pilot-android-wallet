package ch.admin.foitt.pilotwallet.platform.invitation.presentation

import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.extension.navigateUpOrToRoot
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UnknownIssuerErrorViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.None

    fun onBack() = navigationManager.navigateUpOrToRoot()
}
