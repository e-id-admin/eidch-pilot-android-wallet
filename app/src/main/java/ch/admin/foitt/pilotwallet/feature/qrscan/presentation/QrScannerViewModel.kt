package ch.admin.foitt.pilotwallet.feature.qrscan.presentation

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.feature.qrscan.infra.QrScanner
import ch.admin.foitt.pilotwallet.feature.qrscan.presentation.permission.hasCameraPermission
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.ProcessInvitation
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.ErrorDialogState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetErrorDialogState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.utils.launchWithDelayedLoading
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.QrScanPermissionScreenDestination
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class QrScannerViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val processInvitation: ProcessInvitation,
    private val setErrorDialogState: SetErrorDialogState,
    private val qrScanner: QrScanner,
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState, addBottomSystemBarPaddings = false) {

    override val topBarState = TopBarState.Details(navManager::popBackStack, R.string.qrScanner_title)

    val flashLightState = qrScanner.flashLightState

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _showError = MutableStateFlow(false)
    val showError = _showError.asStateFlow()

    fun onInitScan(previewView: PreviewView) {
        qrScanner.registerScanner(previewView).onFailure { throwable ->
            setErrorDialogState(ErrorDialogState.Unexpected(throwable.localizedMessage))
            Timber.e(message = "Failure while registering scanner", t = throwable)
        }
    }

    private fun onQrScanSuccess(barcodesContent: List<String>) {
        qrScanner.pauseScanner()
        viewModelScope.launchWithDelayedLoading(_isLoading) {
            // TODO: handle edge case when multiple invitations were scanned at the same time
            processInvitation(invitationUri = barcodesContent.firstOrNull() ?: "")
                .onSuccess { navigationAction -> navigationAction.navigate() }
                .onFailure { showErrorWithDelayedHiding() }
        }
    }

    private fun showErrorWithDelayedHiding() {
        if (_showError.value) return
        _showError.value = true
        viewModelScope.launch {
            delay(ERROR_DISPLAY_DURATION_MILLIS)
            _showError.value = false
            qrScanner.resumeScanner()
        }
    }

    private fun tryInitAnalyser() {
        return if (hasCameraPermission(appContext)) {
            qrScanner.initAnalyser(onBarcodesScanned = ::onQrScanSuccess)
        } else {
            navManager.navigateToAndClearCurrent(QrScanPermissionScreenDestination)
        }
    }

    fun onFlashLight() {
        viewModelScope.launch {
            qrScanner.toggleFlashLight()
        }
    }

    init {
        tryInitAnalyser()
    }

    override fun onCleared() {
        qrScanner.unRegisterScanner()
        super.onCleared()
    }

    companion object {
        private const val ERROR_DISPLAY_DURATION_MILLIS = 5000L
    }
}
