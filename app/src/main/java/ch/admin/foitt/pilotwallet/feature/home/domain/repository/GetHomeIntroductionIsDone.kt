package ch.admin.foitt.pilotwallet.feature.home.domain.repository

fun interface GetHomeIntroductionIsDone {
    suspend operator fun invoke(): Boolean
}
