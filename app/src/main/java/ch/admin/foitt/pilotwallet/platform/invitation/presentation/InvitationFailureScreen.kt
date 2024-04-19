package ch.admin.foitt.pilotwallet.platform.invitation.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.ButtonSecondary
import ch.admin.foitt.pilotwallet.platform.composables.ResultScreenContent
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.WalletTexts
import ch.admin.foitt.pilotwallet.theme.errorBackgroundDark
import ch.admin.foitt.pilotwallet.theme.errorBackgroundLight
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun InvitationFailureScreen(
    viewModel: InvitationFailureViewModel,
) {
    InvitationFailureScreenContent(
        dateTime = viewModel.dateTime,
        onClose = viewModel::close,
    )
}

@Composable
private fun InvitationFailureScreenContent(
    dateTime: String,
    onClose: () -> Unit,
) = ResultScreenContent(
    iconRes = R.drawable.pilot_ic_warning_big,
    dateTime = dateTime,
    message = stringResource(id = R.string.global_error_unexpected_title),
    topColor = MaterialTheme.colorScheme.errorBackgroundLight,
    bottomColor = MaterialTheme.colorScheme.errorBackgroundDark,
    bottomContent = {
        ButtonSecondary(
            text = stringResource(id = R.string.global_error_backToHome_button),
            onClick = onClose,
            leftIcon = painterResource(id = R.drawable.pilot_ic_back_button),
        )
    },
    content = {
        WalletTexts.BodySmallCentered(
            text = stringResource(id = R.string.global_error_unexpected_message),
            color = MaterialTheme.colorScheme.onError,
        )
    },
)

@WalletAllScreenPreview
@Composable
private fun InvitationFailureScreenPreview() {
    PilotWalletTheme {
        InvitationFailureScreenContent(
            dateTime = "10th December 1990 | 11:17",
            onClose = {},
        )
    }
}
