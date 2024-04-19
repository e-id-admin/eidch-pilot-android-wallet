package ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase

import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState

fun interface SetTopBarState {
    operator fun invoke(state: TopBarState)
}
