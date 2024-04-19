package ch.admin.foitt.pilotwallet.feature.login.domain.usecase

fun interface IncreaseFailedLoginAttemptsCounter {
    operator fun invoke()
}
