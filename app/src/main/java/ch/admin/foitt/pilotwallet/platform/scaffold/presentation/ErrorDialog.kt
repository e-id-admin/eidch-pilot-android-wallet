package ch.admin.foitt.pilotwallet.platform.scaffold.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.preview.WalletComponentPreview
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.ErrorDialogState
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes

@Composable
internal fun ErrorDialog(
    viewModel: ErrorDialogViewModel = hiltViewModel()
) {
    when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
        ErrorDialogState.Closed -> {}
        is ErrorDialogState.Unexpected -> ErrorDialogContent(
            title = stringResource(id = R.string.global_error_unexpected_title),
            message = stringResource(id = R.string.global_error_unexpected_message),
            details = state.errorDetails,
            buttonText = stringResource(id = R.string.global_error_confirm_button),
            icon = painterResource(id = R.drawable.material_error),
            onDismiss = viewModel::onDismiss,
        )
        is ErrorDialogState.Network -> ErrorDialogContent(
            title = stringResource(id = R.string.global_error_network_title),
            message = stringResource(id = R.string.global_error_network_message),
            details = state.errorDetails,
            buttonText = stringResource(id = R.string.global_error_confirm_button),
            icon = painterResource(id = R.drawable.material_signal_wifi_statusbar_not_connected),
            onDismiss = viewModel::onDismiss,
        )
        is ErrorDialogState.Wallet -> ErrorDialogContent(
            title = stringResource(id = R.string.global_error_wallet_title),
            message = stringResource(id = R.string.global_error_wallet_message),
            details = state.errorDetails,
            buttonText = stringResource(id = R.string.global_error_confirm_button),
            icon = painterResource(id = R.drawable.material_error),
            onDismiss = viewModel::onDismiss,
        )
    }
}

@Composable
private fun ErrorDialogContent(
    title: String,
    message: String,
    details: String?,
    icon: Painter,
    buttonText: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Text(
                text = buttonText,
                modifier = Modifier
                    .padding(start = Sizes.s04, top = Sizes.s02, bottom = Sizes.s02)
                    .clickable {
                        onDismiss()
                    },
            )
        },
        modifier = Modifier,
        icon = {
            Image(
                painter = icon,
                contentDescription = title,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error),
            )
        },
        title = {
            Text(text = title)
        },
        text = {
            Column {
                Text(text = message)
                details?.let { detailsString ->
                    Spacer(modifier = Modifier.height(Sizes.s02))
                    Text(text = detailsString)
                }
            }
        },
    )
}

@Composable
@WalletComponentPreview
private fun ErrorDialogPreview() {
    PilotWalletTheme {
        ErrorDialogContent(
            title = stringResource(id = R.string.global_error_unexpected_title),
            message = stringResource(id = R.string.global_error_unexpected_message),
            details = "WalletBustedException",
            icon = painterResource(id = R.drawable.material_signal_wifi_statusbar_not_connected),
            buttonText = stringResource(id = R.string.global_error_confirm_button),
            onDismiss = {},
        )
    }
}
