package ch.admin.foitt.pilotwallet.feature.qrscan.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.feature.qrscan.domain.model.PermissionState
import ch.admin.foitt.pilotwallet.feature.qrscan.presentation.permission.PermissionBlockedScreenContent
import ch.admin.foitt.pilotwallet.feature.qrscan.presentation.permission.PermissionIntroScreenContent
import ch.admin.foitt.pilotwallet.feature.qrscan.presentation.permission.PermissionRationalScreenContent
import ch.admin.foitt.pilotwallet.platform.preview.WalletComponentPreview
import ch.admin.foitt.pilotwallet.platform.utils.LocalActivity
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun QrScanPermissionScreen(viewModel: QrScanPermissionViewModel) {
    val currentActivity = LocalActivity.current
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { permissionsGranted ->
            viewModel.onCameraPermissionResult(
                permissionGranted = permissionsGranted,
                activity = currentActivity,
            )
        },
    )

    SideEffect {
        viewModel.setPermissionLauncher(cameraPermissionLauncher)
    }

    LaunchedEffect(viewModel) {
        viewModel.navigateToFirstScreen(currentActivity)
    }

    QrScanPermissionScreenContent(
        permissionState = viewModel.permissionState.collectAsStateWithLifecycle().value,
        onAllow = viewModel::onCameraPermissionPrompt,
        onClose = viewModel::onClose,
        onOpenSettings = viewModel::onOpenSettings,
    )
}

@Composable
private fun QrScanPermissionScreenContent(
    permissionState: PermissionState,
    onAllow: () -> Unit,
    onClose: () -> Unit,
    onOpenSettings: () -> Unit,
) = when (permissionState) {
    PermissionState.Granted,
    PermissionState.Initial -> {}
    PermissionState.Blocked -> PermissionBlockedScreenContent(
        onClose = onClose,
        onOpenSettings = onOpenSettings,
    )
    PermissionState.Intro -> PermissionIntroScreenContent(
        onDeny = onClose,
        onAllow = onAllow,
    )
    PermissionState.Rationale -> PermissionRationalScreenContent(
        onDeny = onClose,
        onAllow = onAllow,
    )
}

@WalletComponentPreview
@Composable
private fun QrScanPermissionScreenPreview() {
    PilotWalletTheme {
        QrScanPermissionScreenContent(
            permissionState = PermissionState.Intro,
            onAllow = {},
            onClose = {},
            onOpenSettings = {},
        )
    }
}
