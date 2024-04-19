package ch.admin.foitt.pilotwallet.feature.onboarding.presentation

import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.OnboardingQRScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.UserPrivacyPolicyScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingIntroViewModel @Inject constructor(
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.None

    fun onSkip() = navManager.navigateTo(UserPrivacyPolicyScreenDestination)
    fun onNext() = navManager.navigateTo(OnboardingQRScreenDestination)
}
