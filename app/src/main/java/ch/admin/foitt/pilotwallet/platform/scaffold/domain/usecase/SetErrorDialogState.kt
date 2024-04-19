package ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase

import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.ErrorDialogState

fun interface SetErrorDialogState {
    operator fun invoke(state: ErrorDialogState)
}
