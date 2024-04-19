package ch.admin.foitt.pilotwallet.feature.settings.presentation.security

import android.content.Context
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.eventTracking.domain.usecase.ApplyUserPrivacyPolicy
import ch.admin.foitt.pilotwallet.platform.eventTracking.domain.usecase.IsUserPrivacyPolicyAcceptedFlow
import ch.admin.foitt.pilotwallet.platform.login.domain.model.CanUseBiometricsForLoginResult
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.CanUseBiometricsForLogin
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.AuthWithPinNavArg
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.GetPassphraseWasDeleted
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.SavePassphraseWasDeleted
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.utils.openLink
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.AuthWithPinScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.DataAnalysisScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.EnterCurrentPinScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuritySettingsViewModel @Inject constructor(
    private val navManager: NavigationManager,
    @ApplicationContext private val appContext: Context,
    private val canUseBiometricsForLogin: CanUseBiometricsForLogin,
    private val applyUserPrivacyPolicy: ApplyUserPrivacyPolicy,
    isUserPrivacyPolicyAcceptedFlow: IsUserPrivacyPolicyAcceptedFlow,
    private val getPassphraseWasDeleted: GetPassphraseWasDeleted,
    private val savePassphraseWasDeleted: SavePassphraseWasDeleted,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState, addBottomSystemBarPaddings = false) {

    override val topBarState = TopBarState.Details(navManager::navigateUp, R.string.securitySettings_title)

    val biometricsHardwareIsAvailable: Flow<Boolean> = flow {
        emit(
            canUseBiometricsForLogin() !is CanUseBiometricsForLoginResult.NoHardwareAvailable
        )
    }

    val isBiometricsToggleEnabled: Flow<Boolean> = flow {
        emit(
            canUseBiometricsForLogin() is CanUseBiometricsForLoginResult.Usable
        )
    }

    val showPassphraseDeletionMessage: Flow<Boolean> = flow {
        emit(getPassphraseWasDeleted())
        savePassphraseWasDeleted(false)
    }

    val shareAnalysisEnabled = isUserPrivacyPolicyAcceptedFlow()

    fun onChangeBiometrics() {
        viewModelScope.launch {
            isBiometricsToggleEnabled.collect { isToggleCurrentStateOn ->
                if (isToggleCurrentStateOn) {
                    toggleBiometricsOff()
                } else {
                    toggleBiometricsOn()
                }
            }
        }
    }

    private fun toggleBiometricsOn() {
        navManager.navigateTo(AuthWithPinScreenDestination(navArgs = AuthWithPinNavArg(enable = true)))
    }

    private fun toggleBiometricsOff() {
        navManager.navigateTo(AuthWithPinScreenDestination(navArgs = AuthWithPinNavArg(enable = false)))
    }

    fun onDataProtection() = appContext.openLink(R.string.securitySettings_dataProtectionLink)

    fun onChangePin() = navManager.navigateTo(EnterCurrentPinScreenDestination)

    fun onShareAnalysisChange(isEnabled: Boolean) {
        applyUserPrivacyPolicy(isEnabled)
    }

    fun onDataAnalysis() = navManager.navigateTo(DataAnalysisScreenDestination)
}
