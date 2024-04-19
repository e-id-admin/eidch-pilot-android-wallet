package ch.admin.foitt.pilotwallet.platform.database.domain.model

sealed interface DatabaseError {
    object AlreadyOpen : CreateDatabaseError, OpenDatabaseError
    data class SetupFailed(val throwable: Throwable) : CreateDatabaseError, OpenDatabaseError
    data class WrongPassphrase(val throwable: Throwable) : OpenDatabaseError
    object NotOpen : GetDatabaseError
    data class ReKeyFailed(val throwable: Throwable) : ChangeDatabasePassphraseError
}

sealed interface CreateDatabaseError
sealed interface OpenDatabaseError
sealed interface GetDatabaseError
sealed interface ChangeDatabasePassphraseError
