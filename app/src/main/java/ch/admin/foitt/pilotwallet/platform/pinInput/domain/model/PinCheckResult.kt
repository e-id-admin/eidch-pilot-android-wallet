package ch.admin.foitt.pilotwallet.platform.pinInput.domain.model

sealed class PinCheckResult {
    data object Success : PinCheckResult()
    data object Error : PinCheckResult()
    data object Reset : PinCheckResult()
}
