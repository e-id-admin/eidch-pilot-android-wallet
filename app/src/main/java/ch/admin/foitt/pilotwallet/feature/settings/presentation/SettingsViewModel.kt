package ch.admin.foitt.pilotwallet.feature.settings.presentation

import android.content.Context
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.utils.openLink
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.ImpressumScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.LicencesScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.SecuritySettingsScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val navManager: NavigationManager,
    @ApplicationContext private val appContext: Context,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState, addBottomSystemBarPaddings = false) {

    override val topBarState = TopBarState.Details(navManager::navigateUp, R.string.settings_title)

    fun onSecurityScreen() = navManager.navigateTo(SecuritySettingsScreenDestination)

    fun onFeedback() = appContext.openLink(R.string.settings_feedbackLink)

    fun onHelp() = appContext.openLink(R.string.settings_helpLink)

    fun onContact() = appContext.openLink(R.string.settings_contactLink)

    fun onImpressumScreen() = navManager.navigateTo(ImpressumScreenDestination)

    fun onLicencesScreen() = navManager.navigateTo(LicencesScreenDestination)
}
