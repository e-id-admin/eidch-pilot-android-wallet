package ch.admin.foitt.pilotwallet.feature.credentialDetail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.feature.credentialDetail.domain.usecase.GetPoliceQrCode
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.GetCredentialPreviewFlow
import ch.admin.foitt.pilotwallet.platform.credential.presentation.adapter.GetCredentialCardState
import ch.admin.foitt.pilotwallet.platform.credential.presentation.model.CredentialCardState
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.ErrorDialogState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetErrorDialogState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialClaimsData
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.UpdateCredentialStatus
import ch.admin.foitt.pilotwallet.platform.utils.launchWithDelayedLoading
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialDeleteScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialDetailScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PoliceQrCodeScreenDestination
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CredentialDetailViewModel @Inject constructor(
    private val getPoliceQrCode: GetPoliceQrCode,
    private val updateCredentialStatus: UpdateCredentialStatus,
    private val navManager: NavigationManager,
    private val setErrorDialogState: SetErrorDialogState,
    private val getCredentialClaimsData: GetCredentialClaimsData,
    private val getCredentialCardState: GetCredentialCardState,
    getCredentialPreviewFlow: GetCredentialPreviewFlow,
    setTopBarState: SetTopBarState,
    savedStateHandle: SavedStateHandle
) : ScreenViewModel(setTopBarState) {
    override val topBarState =
        TopBarState.DetailsWithCustomSettings(
            onUp = navManager::navigateUp,
            titleId = R.string.credential_detail_title,
            onSettings = { _showBottomSheet.value = true }
        )

    private val navArgs = CredentialDetailScreenDestination.argsFrom(savedStateHandle)

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet = _showBottomSheet.asStateFlow()

    private var qrCodeImageData = ""
    val showPoliceControlItem = flow {
        getPoliceQrCode(credentialId = navArgs.credentialId)
            .onSuccess { imageData ->
                emit(true)
                qrCodeImageData = imageData
            }
            .onFailure { error ->
                emit(false)
                setErrorDialogState(ErrorDialogState.Unexpected(error.toString()))
            }
    }

    val credentialClaims = channelFlow {
        viewModelScope.launchWithDelayedLoading(isLoadingFlow = _isLoading, delay = 200) {
            getCredentialClaimsData(credentialId = navArgs.credentialId)
                .onSuccess { credentialClaims ->
                    send(credentialClaims)
                }
                .onFailure { error ->
                    setErrorDialogState(ErrorDialogState.Unexpected(error.toString()))
                }
        }
        awaitClose()
    }

    val credentialCardState: Flow<CredentialCardState> =
        getCredentialPreviewFlow(credentialId = navArgs.credentialId).map { getCredentialCardState(it) }

    init {
        viewModelScope.launch {
            updateCredentialStatus(navArgs.credentialId)
        }
    }

    fun onDelete() {
        _showBottomSheet.value = false
        navManager.navigateTo(CredentialDeleteScreenDestination(credentialId = navArgs.credentialId))
    }

    fun onShowQrCode() {
        _showBottomSheet.value = false
        navManager.navigateTo(PoliceQrCodeScreenDestination(imageData = qrCodeImageData))
    }

    fun onBottomSheetDismiss() {
        _showBottomSheet.value = false
    }
}
