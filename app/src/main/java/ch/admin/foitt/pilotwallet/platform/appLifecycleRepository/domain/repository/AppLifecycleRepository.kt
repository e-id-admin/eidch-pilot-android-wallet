package ch.admin.foitt.pilotwallet.platform.appLifecycleRepository.domain.repository

import ch.admin.foitt.pilotwallet.platform.appLifecycleRepository.domain.model.AppLifecycleState
import kotlinx.coroutines.flow.StateFlow

interface AppLifecycleRepository {
    val state: StateFlow<AppLifecycleState>
}
