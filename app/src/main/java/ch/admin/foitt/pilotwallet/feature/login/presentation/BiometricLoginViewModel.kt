package ch.admin.foitt.pilotwallet.feature.login.presentation

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.ResetLockout
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricPromptType
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.usecase.GetAuthenticators
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.presentation.AndroidBiometricPrompt
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.ResetBiometrics
import ch.admin.foitt.pilotwallet.platform.login.domain.model.CanUseBiometricsForLoginResult
import ch.admin.foitt.pilotwallet.platform.login.domain.model.LoginError
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.CanUseBiometricsForLogin
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.LoginWithBiometrics
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.utils.trackCompletion
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PinLoginScreenDestination
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BiometricLoginViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val getAuthenticators: GetAuthenticators,
    private val canUseBiometricsForLogin: CanUseBiometricsForLogin,
    private val loginWithBiometrics: LoginWithBiometrics,
    private val resetBiometrics: ResetBiometrics,
    private val resetLockout: ResetLockout,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.RootNoSettings

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun tryLoginWithBiometric(activity: FragmentActivity) {
        viewModelScope.launch {
            canUseBiometricsForLogin().let { result ->
                if (result != CanUseBiometricsForLoginResult.Usable) {
                    Timber.w("Biometrics cannot be used for login: $result")
                    resetBiometrics()
                    navigateToLoginWithPin()
                    return@launch
                }
            }

            val biometricPromptWrapper = AndroidBiometricPrompt(
                activity = activity,
                allowedAuthenticators = getAuthenticators(),
                promptType = BiometricPromptType.Login,
            )

            loginWithBiometrics(biometricPromptWrapper)
                .onSuccess { navigationAction ->
                    resetLockout()
                    navigationAction.navigate()
                }.onFailure { loginError ->
                    when (loginError) {
                        LoginError.Cancelled -> {}
                        else -> navigateToLoginWithPin()
                    }
                }
        }.trackCompletion(_isLoading)
    }

    fun navigateToLoginWithPin() {
        navManager.navigateToAndClearCurrent(
            direction = PinLoginScreenDestination,
        )
    }
}
