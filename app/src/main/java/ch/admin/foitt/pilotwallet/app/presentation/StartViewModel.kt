package ch.admin.foitt.pilotwallet.app.presentation

import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.platform.appSetupState.domain.repository.OnboardingStateRepository
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.NavigateToLogin
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.OnboardingIntroScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val onboardingStateRepository: OnboardingStateRepository,
    private val navigateToLogin: NavigateToLogin,
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {

    override val topBarState = TopBarState.SystemBarPadding

    fun navigateToFirstScreen() {
        viewModelScope.launch {
            when (onboardingStateRepository.getOnboardingState()) {
                true -> navigateToLogin().navigate()
                false -> navManager.navigateToAndClearCurrent(OnboardingIntroScreenDestination)
            }
        }
    }
}
