package ch.admin.foitt.pilotwallet.feature.settings.presentation.impressum

import android.content.Context
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.utils.openLink
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ImpressumViewModel @Inject constructor(
    private val navManager: NavigationManager,
    @ApplicationContext private val appContext: Context,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.Details(navManager::popBackStack, R.string.impressum_title)

    fun onGithub() = appContext.openLink(R.string.impressum_github_link)

    fun onMoreInformation() = appContext.openLink(R.string.impressum_more_information_link)

    fun onLegals() = appContext.openLink(R.string.impressum_legals_link)
}
