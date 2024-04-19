package ch.admin.foitt.pilotwallet.platform.database.domain.model

object DisplayLanguage {
    const val DEFAULT = "de"
    const val FALLBACK = "fallback"
    val PRIORITIES = listOf("de", "en", "fallback")
}
