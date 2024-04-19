package ch.admin.foitt.pilotwallet.platform.database.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseWrapper
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.IsAppDatabaseOpen
import javax.inject.Inject

class IsAppDatabaseOpenImpl @Inject constructor(
    private val databaseWrapper: DatabaseWrapper,
) : IsAppDatabaseOpen {
    override fun invoke(): Boolean = databaseWrapper.isOpen()
}
