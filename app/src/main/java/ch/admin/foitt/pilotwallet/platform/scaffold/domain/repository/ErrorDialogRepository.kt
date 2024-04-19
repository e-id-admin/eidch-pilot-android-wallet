package ch.admin.foitt.pilotwallet.platform.scaffold.domain.repository

import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.ErrorDialogState
import kotlinx.coroutines.flow.StateFlow

interface ErrorDialogRepository {
    val state: StateFlow<ErrorDialogState>
    fun setState(errorDialogState: ErrorDialogState)
}
