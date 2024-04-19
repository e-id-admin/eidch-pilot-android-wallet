package ch.admin.foitt.pilotwallet.platform.appLifecycleRepository.data.repository

import androidx.lifecycle.ProcessLifecycleOwner
import ch.admin.foitt.pilotwallet.platform.appLifecycleRepository.domain.model.AppLifecycleState
import ch.admin.foitt.pilotwallet.platform.appLifecycleRepository.domain.repository.AppLifecycleRepository
import ch.admin.foitt.pilotwallet.platform.di.IoDispatcherScope
import ch.admin.foitt.pilotwallet.platform.utils.map
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class AppLifecycleRepositoryImpl @Inject constructor(
    @IoDispatcherScope ioDispatcherScope: CoroutineScope,
) : AppLifecycleRepository {

    private val androidAppLifecycleState = ProcessLifecycleOwner.get().lifecycle.currentStateFlow

    override val state = androidAppLifecycleState.map(ioDispatcherScope) { lifecycleState ->
        AppLifecycleState.from(lifecycleState)
    }
}
