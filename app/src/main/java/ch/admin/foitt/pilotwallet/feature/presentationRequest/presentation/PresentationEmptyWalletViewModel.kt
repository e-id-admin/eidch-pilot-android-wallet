package ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation

import android.content.Context
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.extension.navigateUpOrToRoot
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.utils.openLink
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class PresentationEmptyWalletViewModel @Inject constructor(
    private val navManager: NavigationManager,
    @ApplicationContext private val appContext: Context,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.None

    fun onSupport() = appContext.openLink(R.string.presentation_error_empty_wallet_support_link)

    fun onBack() = navManager.navigateUpOrToRoot()
}
