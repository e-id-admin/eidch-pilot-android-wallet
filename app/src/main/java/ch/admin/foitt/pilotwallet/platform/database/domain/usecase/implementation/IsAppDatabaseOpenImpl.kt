package ch.admin.foitt.pilotwallet.platform.database.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.repository.DatabaseRepository
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.IsAppDatabaseOpen
import javax.inject.Inject

class IsAppDatabaseOpenImpl @Inject constructor(
    private val databaseRepository: DatabaseRepository,
) : IsAppDatabaseOpen {
    override fun invoke(): Boolean = databaseRepository.isOpen()
}
