package ch.admin.foitt.pilotwallet.feature.settings.presentation.biometrics

import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetLocalizedDateTime
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class EnableBiometricsErrorViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val getLocalizedDateTime: GetLocalizedDateTime,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(
    setTopBarState = setTopBarState,
    addBottomSystemBarPaddings = false,
) {
    override val topBarState = TopBarState.None

    private val eventTime = ZonedDateTime.now()
    val dateTime: String get() = getLocalizedDateTime(eventTime)

    fun onClose() = navManager.popBackStack()
}
