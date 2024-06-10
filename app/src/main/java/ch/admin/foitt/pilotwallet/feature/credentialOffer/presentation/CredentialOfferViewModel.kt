package ch.admin.foitt.pilotwallet.feature.credentialOffer.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.feature.credentialOffer.domain.model.CredentialOfferIssuer
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.SaveActivity
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetDrawableFromData
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.GetCredentialPreviewFlow
import ch.admin.foitt.pilotwallet.platform.credential.presentation.adapter.GetCredentialCardState
import ch.admin.foitt.pilotwallet.platform.credential.presentation.model.CredentialCardState
import ch.admin.foitt.pilotwallet.platform.database.domain.model.Activity
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityType
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialIssuerDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.extension.navigateUpOrToRoot
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimData
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialClaimsData
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetLocalizedCredentialIssuerDisplay
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.UpdateCredentialStatus
import ch.admin.foitt.pilotwallet.platform.utils.launchWithDelayedLoading
import ch.admin.foitt.pilotwallet.platform.utils.toPainter
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialOfferErrorScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialOfferScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.DeclineCredentialOfferScreenDestination
import com.github.michaelbull.result.mapBoth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CredentialOfferViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val navManager: NavigationManager,
    private val updateCredentialStatus: UpdateCredentialStatus,
    private val getCredentialClaimsData: GetCredentialClaimsData,
    private val getLocalizedCredentialIssuerDisplay: GetLocalizedCredentialIssuerDisplay,
    getCredentialPreviewFlow: GetCredentialPreviewFlow,
    private val getCredentialCardState: GetCredentialCardState,
    private val getDrawableFromData: GetDrawableFromData,
    private val saveActivity: SaveActivity,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(
    setTopBarState,
    addBottomSystemBarPaddings = false,
) {
    override val topBarState = TopBarState.None

    private val navArgs = CredentialOfferScreenDestination.argsFrom(savedStateHandle)
    private val credentialId = navArgs.credentialId

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _issuer = MutableStateFlow<CredentialOfferIssuer?>(null)
    val issuer = _issuer.asStateFlow()

    val credentialCardState: StateFlow<CredentialCardState> =
        getCredentialPreviewFlow(credentialId).map { preview ->
            getCredentialCardState(preview)
        }.toStateFlow(CredentialCardState.EMPTY)

    private val _claims = MutableStateFlow(emptyList<CredentialClaimData>())
    val claims = _claims.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            updateCredentialStatus(credentialId)
        }
        viewModelScope.launchWithDelayedLoading(_isLoading) {
            _issuer.value = getLocalizedCredentialIssuerDisplay(credentialId)?.toCredentialOfferIssuer()
            getCredentialClaimsData(credentialId).mapBoth(
                success = { claims -> _claims.value = claims },
                failure = { navigateToErrorScreen() }
            )
        }
    }

    private suspend fun CredentialIssuerDisplay.toCredentialOfferIssuer() =
        CredentialOfferIssuer(name, getDrawableFromData(image)?.toPainter())

    fun onAcceptClicked() {
        viewModelScope.launch {
            val activityToSave = Activity(
                credentialId = credentialId,
                credentialSnapshotStatus = credentialCardState.value.status ?: CredentialStatus.UNKNOWN,
                type = ActivityType.CREDENTIAL_RECEIVED,
            )
            saveActivity(activityToSave)
            navManager.navigateUpOrToRoot()
        }
    }

    fun onDeclineClicked() {
        navManager.navigateTo(
            DeclineCredentialOfferScreenDestination(credentialId = credentialId)
        )
    }

    private fun navigateToErrorScreen() {
        Timber.e("General error")
        navManager.navigateToAndClearCurrent(CredentialOfferErrorScreenDestination)
    }
}
