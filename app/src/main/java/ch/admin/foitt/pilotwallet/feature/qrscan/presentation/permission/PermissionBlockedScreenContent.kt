package ch.admin.foitt.pilotwallet.feature.qrscan.presentation.permission

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.ButtonOutlined
import ch.admin.foitt.pilotwallet.platform.composables.ButtonPrimary
import ch.admin.foitt.pilotwallet.platform.composables.SimpleScreenContent
import ch.admin.foitt.pilotwallet.platform.preview.WalletComponentPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme

@Composable
fun PermissionBlockedScreenContent(
    onClose: () -> Unit,
    onOpenSettings: () -> Unit,
) = SimpleScreenContent(
    icon = R.drawable.pilot_ic_camera_permission_denied,
    titleText = stringResource(id = R.string.cameraPermission_denied_title),
    mainText = stringResource(id = R.string.cameraPermission_denied_message),
    bottomBlockContent = {
        ButtonOutlined(
            text = stringResource(id = R.string.cameraPermission_denied_closeButton),
            leftIcon = painterResource(id = R.drawable.pilot_ic_back_button),
            onClick = onClose,
        )
        ButtonPrimary(
            text = stringResource(id = R.string.cameraPermission_denied_settingsButton),
            rightIcon = painterResource(id = R.drawable.pilot_ic_link),
            onClick = onOpenSettings,
        )
    }
)

@WalletComponentPreview
@Composable
private fun PermissionBlockedScreenContentPreview() {
    PilotWalletTheme {
        PermissionBlockedScreenContent(
            onClose = {},
            onOpenSettings = {},
        )
    }
}
