package ch.admin.foitt.pilotwallet.feature.home.domain.repository

interface HomeIntroductionRepository {
    suspend fun isDone(): Boolean
    suspend fun setDone()
}
