package ch.admin.foitt.pilotwallet.feature.home.domain.repository

fun interface SaveHomeIntroductionIsDone {
    suspend operator fun invoke()
}
