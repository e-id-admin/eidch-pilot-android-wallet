package ch.admin.foitt.pilotwallet.feature.credentialOffer.presentation

import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetLocalizedDateTime
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.extension.navigateUpOrToRoot
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class CredentialOfferErrorViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val getLocalizedDateTime: GetLocalizedDateTime,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {

    override val topBarState = TopBarState.None

    private val eventTime = ZonedDateTime.now()
    val dateTime: String get() = getLocalizedDateTime(eventTime)

    fun onBack() {
        navigationManager.navigateUpOrToRoot()
    }
}
