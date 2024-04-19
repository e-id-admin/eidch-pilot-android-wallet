package ch.admin.foitt.pilotwallet.platform.pinInput.domain.model

sealed class PinInputResult {
    class Success(val pin: String) : PinInputResult()
    data object Error : PinInputResult()
}
