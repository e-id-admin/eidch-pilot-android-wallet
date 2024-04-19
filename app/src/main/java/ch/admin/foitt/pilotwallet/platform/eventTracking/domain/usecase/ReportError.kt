package ch.admin.foitt.pilotwallet.platform.eventTracking.domain.usecase

fun interface ReportError {
    operator fun invoke(errorMessage: String, error: Throwable?)
}
