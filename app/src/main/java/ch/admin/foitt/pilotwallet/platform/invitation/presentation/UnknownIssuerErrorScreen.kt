package ch.admin.foitt.pilotwallet.platform.invitation.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.ButtonOutlined
import ch.admin.foitt.pilotwallet.platform.composables.SimpleScreenContent
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun UnknownIssuerErrorScreen(
    viewModel: UnknownIssuerErrorViewModel,
) {
    UnknownIssuerErrorScreenContent(
        onBack = viewModel::onBack,
    )
}

@Composable
private fun UnknownIssuerErrorScreenContent(
    onBack: () -> Unit,
) = SimpleScreenContent(
    icon = R.drawable.pilot_ic_unknown_issuer,
    titleText = stringResource(id = R.string.credential_offer_invalid_issuer_error_title),
    mainText = stringResource(id = R.string.credential_offer_invalid_issuer_error_message),
    bottomBlockContent = {
        ButtonOutlined(
            text = stringResource(id = R.string.global_back_home),
            leftIcon = painterResource(id = R.drawable.pilot_ic_back_button),
            onClick = onBack,
        )
    }
)

@WalletAllScreenPreview
@Composable
private fun UnknownIssuerErrorScreenPreview() {
    PilotWalletTheme {
        UnknownIssuerErrorScreenContent(
            onBack = {},
        )
    }
}
