package ch.admin.foitt.pilotwallet.platform.scaffold.domain.repository

import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import kotlinx.coroutines.flow.StateFlow

interface TopBarStateRepository {
    val state: StateFlow<TopBarState>

    fun setState(state: TopBarState)
}
