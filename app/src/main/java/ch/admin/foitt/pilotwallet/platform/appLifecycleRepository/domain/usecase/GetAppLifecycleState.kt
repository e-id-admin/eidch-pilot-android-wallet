package ch.admin.foitt.pilotwallet.platform.appLifecycleRepository.domain.usecase

import ch.admin.foitt.pilotwallet.platform.appLifecycleRepository.domain.model.AppLifecycleState
import kotlinx.coroutines.flow.StateFlow

interface GetAppLifecycleState {
    operator fun invoke(): StateFlow<AppLifecycleState>
}
