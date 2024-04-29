package ch.admin.foitt.pilotwallet.platform.deeplink.domain.repository

interface DeepLinkIntentRepository {
    fun set(deepLink: String)

    fun get(): String?

    fun reset()
}
