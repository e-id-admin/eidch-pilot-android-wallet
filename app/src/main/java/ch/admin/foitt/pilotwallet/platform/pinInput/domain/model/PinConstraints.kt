package ch.admin.foitt.pilotwallet.platform.pinInput.domain.model

data class PinConstraints(
    val length: Int = 6,
    val charSet: Regex = Regex("^\\d*$"),
)
