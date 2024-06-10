package ch.admin.foitt.pilotwallet.platform.login.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.ResetBiometrics
import ch.admin.foitt.pilotwallet.platform.login.domain.model.CanUseBiometricsForLoginResult
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.CanUseBiometricsForLogin
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.NavigateToLogin
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.BiometricLoginScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PinLoginScreenDestination
import com.ramcosta.composedestinations.spec.Direction
import javax.inject.Inject

class NavigateToLoginImpl @Inject constructor(
    private val canUseBiometricsForLogin: CanUseBiometricsForLogin,
    private val resetBiometrics: ResetBiometrics,
) : NavigateToLogin {
    override suspend fun invoke(): Direction {
        return when (canUseBiometricsForLogin()) {
            CanUseBiometricsForLoginResult.Usable -> BiometricLoginScreenDestination
            CanUseBiometricsForLoginResult.NotSetUpInApp -> PinLoginScreenDestination
            else -> {
                resetBiometrics()
                PinLoginScreenDestination
            }
        }
    }
}
