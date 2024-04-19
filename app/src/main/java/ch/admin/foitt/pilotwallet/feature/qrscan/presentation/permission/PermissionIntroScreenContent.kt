package ch.admin.foitt.pilotwallet.feature.qrscan.presentation.permission

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.ButtonOutlined
import ch.admin.foitt.pilotwallet.platform.composables.ButtonTertiary
import ch.admin.foitt.pilotwallet.platform.composables.SimpleScreenContent
import ch.admin.foitt.pilotwallet.platform.preview.WalletComponentPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme

@Composable
fun PermissionIntroScreenContent(
    onDeny: () -> Unit,
    onAllow: () -> Unit,
) = SimpleScreenContent(
    icon = R.drawable.pilot_ic_camera_permission,
    titleText = stringResource(id = R.string.cameraPermission_notDetermined_title),
    mainText = stringResource(id = R.string.cameraPermission_notDetermined_message),
    bottomBlockContent = {
        ButtonOutlined(
            text = stringResource(id = R.string.cameraPermission_notDetermined_denyButton),
            leftIcon = painterResource(id = R.drawable.pilot_ic_cross),
            onClick = onDeny,
        )
        ButtonTertiary(
            text = stringResource(id = R.string.cameraPermission_notDetermined_allowButton),
            leftIcon = painterResource(id = R.drawable.pilot_ic_checkmark_button),
            onClick = onAllow,
        )
    }
)

@WalletComponentPreview
@Composable
private fun PermissionIntroScreenContentPreview() {
    PilotWalletTheme {
        PermissionIntroScreenContent(
            onDeny = {},
            onAllow = {},
        )
    }
}
