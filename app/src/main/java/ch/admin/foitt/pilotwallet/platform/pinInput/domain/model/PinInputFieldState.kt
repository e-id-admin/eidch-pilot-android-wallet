package ch.admin.foitt.pilotwallet.platform.pinInput.domain.model

sealed class PinInputFieldState {
    data object Typing : PinInputFieldState()
    class Valid(val pin: String) : PinInputFieldState()
    data object Success : PinInputFieldState()
    data object Error : PinInputFieldState()
}
