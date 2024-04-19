package ch.admin.foitt.pilotwallet.platform.login.domain.usecase

fun interface BiometricLoginEnabled {
    suspend operator fun invoke(): Boolean
}
