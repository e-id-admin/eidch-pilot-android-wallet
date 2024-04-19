package ch.admin.foitt.pilotwallet.feature.login.domain.repository

interface AttemptsRepository {
    fun getAttempts(): Int
    fun increase()
    fun deleteAttempts()
}
