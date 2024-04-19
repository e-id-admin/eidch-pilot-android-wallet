package ch.admin.foitt.pilotwallet.platform.login.domain.usecase

interface AfterLoginWork {
    suspend operator fun invoke()
}
