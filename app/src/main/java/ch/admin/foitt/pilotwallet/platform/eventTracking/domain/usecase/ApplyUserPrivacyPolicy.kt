package ch.admin.foitt.pilotwallet.platform.eventTracking.domain.usecase

fun interface ApplyUserPrivacyPolicy {
    operator fun invoke(hasAccepted: Boolean)
}
