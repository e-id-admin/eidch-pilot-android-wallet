package ch.admin.foitt.pilotwallet.feature.onboarding.presentation

import android.content.Context
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.eventTracking.domain.usecase.ApplyUserPrivacyPolicy
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.utils.openLink
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.EnterPinScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class UserPrivacyPolicyViewModel @Inject constructor(
    private val navManager: NavigationManager,
    @ApplicationContext private val appContext: Context,
    setTopBarState: SetTopBarState,
    private val applyUserPrivacyPolicy: ApplyUserPrivacyPolicy,
) : ScreenViewModel(setTopBarState) {

    override val topBarState = TopBarState.None

    fun acceptTracking() {
        applyUserPrivacyPolicy(true)
        onNext()
    }

    fun declineTracking() {
        applyUserPrivacyPolicy(false)
        onNext()
    }

    fun onOpenUserPrivacyPolicy() = appContext.openLink(R.string.onboarding_privacy_link_value)

    private fun onNext() {
        navManager.navigateTo(EnterPinScreenDestination)
    }
}
