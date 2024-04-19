package ch.admin.foitt.pilotwallet.feature.qrscan.presentation

import android.Manifest
import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.feature.qrscan.domain.model.PermissionState
import ch.admin.foitt.pilotwallet.feature.qrscan.domain.usecase.CheckQrScanPermission
import ch.admin.foitt.pilotwallet.feature.qrscan.domain.usecase.ShouldAutoTriggerPermissionPrompt
import ch.admin.foitt.pilotwallet.feature.qrscan.presentation.permission.hasCameraPermission
import ch.admin.foitt.pilotwallet.feature.qrscan.presentation.permission.shouldShowRationale
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.utils.openAppDetailsSettings
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.QrScannerScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
class QrScanPermissionViewModel @Inject constructor(
    private val checkQrScanPermission: CheckQrScanPermission,
    private val shouldAutoTriggerPermissionPrompt: ShouldAutoTriggerPermissionPrompt,
    private val navManager: NavigationManager,
    @ApplicationContext private val appContext: Context,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {

    override val topBarState = TopBarState.SystemBarPadding

    private val cameraPermission by lazy { Manifest.permission.CAMERA }

    private val _permissionState = MutableStateFlow<PermissionState>(PermissionState.Initial)
    val permissionState = _permissionState.asStateFlow()

    private var currentPermissionLauncher: WeakReference<ManagedActivityResultLauncher<String, Boolean>> =
        WeakReference(null)

    fun setPermissionLauncher(permissionLauncher: ManagedActivityResultLauncher<String, Boolean>) {
        currentPermissionLauncher = WeakReference(permissionLauncher)
    }

    fun onCameraPermissionResult(permissionGranted: Boolean, activity: FragmentActivity) {
        val shoulShowRationale = shouldShowRationale(activity)
        viewModelScope.launch {
            checkQrScanPermission(
                permissionsAreGranted = permissionGranted,
                rationaleShouldBeShown = shoulShowRationale,
                promptWasTriggered = true,
            ).let(::handleNewState)
        }
    }

    fun onCameraPermissionPrompt() {
        currentPermissionLauncher.get()?.launch(cameraPermission)
            ?: Timber.e("PermissionLauncher was null when attempting prompt")
    }

    fun onOpenSettings() {
        appContext.openAppDetailsSettings()
        navManager.navigateUp()
    }

    fun navigateToFirstScreen(activity: FragmentActivity) {
        val hasPermission = hasCameraPermission(activity.applicationContext)
        val shouldShowRationale = shouldShowRationale(activity)
        viewModelScope.launch {
            if (shouldAutoTriggerPermissionPrompt()) {
                onCameraPermissionPrompt()
            } else {
                checkQrScanPermission(
                    permissionsAreGranted = hasPermission,
                    rationaleShouldBeShown = shouldShowRationale,
                    promptWasTriggered = false,
                ).let(::handleNewState)
            }
        }
    }

    fun onClose() = navManager.navigateUp()

    private fun handleNewState(newState: PermissionState) = when (newState) {
        PermissionState.Granted -> navManager.navigateToAndClearCurrent(QrScannerScreenDestination)
        PermissionState.Blocked,
        PermissionState.Initial,
        PermissionState.Intro,
        PermissionState.Rationale -> _permissionState.update { newState }
    }
}
