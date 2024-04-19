package ch.admin.foitt.pilotwallet.platform.invitation.presentation

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
class InvalidCredentialErrorViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    @ApplicationContext private val appContext: Context,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.None

    fun onMoreInformation() = appContext.openLink(R.string.invitation_error_credential_expired_more_info_link)

    fun onBack() = navigationManager.navigateUpOrToRoot()
}
