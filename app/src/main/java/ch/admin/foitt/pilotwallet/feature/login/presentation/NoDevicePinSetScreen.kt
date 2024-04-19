package ch.admin.foitt.pilotwallet.feature.login.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.ButtonPrimary
import ch.admin.foitt.pilotwallet.platform.composables.SimpleScreenContent
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.platform.utils.LocalActivity
import ch.admin.foitt.pilotwallet.platform.utils.OnResumeEventHandler
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun NoDevicePinSetScreen(viewModel: NoDevicePinSetViewModel) {
    val activity = LocalActivity.current
    BackHandler {
        activity.finish()
    }
    OnResumeEventHandler {
        viewModel.checkDevicePinSet()
    }
    NoDevicePinSetScreenContent(
        onSettings = viewModel::goToSettings
    )
}

@Composable
private fun NoDevicePinSetScreenContent(onSettings: () -> Unit) {
    SimpleScreenContent(
        icon = R.drawable.pilot_ic_missing_device_pin,
        titleText = stringResource(id = R.string.global_error_no_device_pin_title),
        mainText = stringResource(id = R.string.global_error_no_device_pin_message),
        bottomBlockContent = {
            ButtonPrimary(
                text = stringResource(id = R.string.global_error_no_device_pin_button),
                rightIcon = painterResource(id = R.drawable.pilot_ic_link),
                onClick = { onSettings() },
            )
        }
    )
}

@WalletAllScreenPreview
@Composable
private fun NoDevicePinSetScreenPreview() {
    PilotWalletTheme {
        NoDevicePinSetScreenContent(onSettings = {})
    }
}
