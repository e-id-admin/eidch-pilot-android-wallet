package ch.admin.foitt.pilotwallet.platform.database.domain.usecase

interface CloseAppDatabase {
    suspend operator fun invoke()
}
