package ch.admin.foitt.pilotwallet.platform.database.domain.usecase

fun interface IsAppDatabaseOpen {
    operator fun invoke(): Boolean
}
