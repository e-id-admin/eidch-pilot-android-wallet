package ch.admin.foitt.pilotwallet.platform.login.domain.model

sealed interface CanUseBiometricsForLoginResult {
    data object DeactivatedInDeviceSettings : CanUseBiometricsForLoginResult
    data object RemovedInDeviceSettings : CanUseBiometricsForLoginResult
    data object Changed : CanUseBiometricsForLoginResult
    data object Usable : CanUseBiometricsForLoginResult
    data object NotSetUpInApp : CanUseBiometricsForLoginResult
    data object NoHardwareAvailable : CanUseBiometricsForLoginResult
}
