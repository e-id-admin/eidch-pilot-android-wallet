package ch.admin.foitt.pilotwallet.feature.settings.presentation.security

import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DataAnalysisViewModel @Inject constructor(
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState, addBottomSystemBarPaddings = false) {
    override val topBarState = TopBarState.Details(navManager::navigateUp, R.string.dataAnalysis_screenTitle)
}
