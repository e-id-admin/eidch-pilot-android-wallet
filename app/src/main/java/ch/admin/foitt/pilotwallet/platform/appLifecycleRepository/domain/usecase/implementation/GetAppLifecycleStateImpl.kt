package ch.admin.foitt.pilotwallet.platform.appLifecycleRepository.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.appLifecycleRepository.domain.model.AppLifecycleState
import ch.admin.foitt.pilotwallet.platform.appLifecycleRepository.domain.repository.AppLifecycleRepository
import ch.admin.foitt.pilotwallet.platform.appLifecycleRepository.domain.usecase.GetAppLifecycleState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetAppLifecycleStateImpl @Inject constructor(
    private val appLifecycleRepository: AppLifecycleRepository
) : GetAppLifecycleState {
    override fun invoke(): StateFlow<AppLifecycleState> = appLifecycleRepository.state
}
