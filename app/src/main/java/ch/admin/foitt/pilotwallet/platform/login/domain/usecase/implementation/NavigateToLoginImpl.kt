package ch.admin.foitt.pilotwallet.platform.login.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.ResetBiometrics
import ch.admin.foitt.pilotwallet.platform.login.domain.model.CanUseBiometricsForLoginResult
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.CanUseBiometricsForLogin
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.NavigateToLogin
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.navigation.domain.model.NavigationAction
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.BiometricLoginScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PinLoginScreenDestination
import javax.inject.Inject

class NavigateToLoginImpl @Inject constructor(
    private val navManager: NavigationManager,
    private val canUseBiometricsForLogin: CanUseBiometricsForLogin,
    private val resetBiometrics: ResetBiometrics,
) : NavigateToLogin {
    override suspend fun invoke(): NavigationAction {
        return when (canUseBiometricsForLogin()) {
            CanUseBiometricsForLoginResult.Usable -> NavigationAction {
                navManager.navigateToAndClearCurrent(BiometricLoginScreenDestination)
            }
            CanUseBiometricsForLoginResult.NotSetUpInApp -> NavigationAction {
                navManager.navigateToAndClearCurrent(PinLoginScreenDestination)
            }
            else -> {
                resetBiometrics()
                NavigationAction {
                    navManager.navigateToAndClearCurrent(PinLoginScreenDestination)
                }
            }
        }
    }
}
