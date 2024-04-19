package ch.admin.foitt.pilotwallet.platform.deeplink.domain.usecase

interface SetDeepLinkIntent {
    operator fun invoke(data: String?)
}
