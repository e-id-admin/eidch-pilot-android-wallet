package ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.ButtonOutlined
import ch.admin.foitt.pilotwallet.platform.composables.SimpleScreenContent
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun PresentationEmptyWalletScreen(viewModel: PresentationEmptyWalletViewModel) {
    PresentationEmptyWalletContent(
        onSupport = viewModel::onSupport,
        onBack = viewModel::onBack,
    )
}

@Composable
private fun PresentationEmptyWalletContent(
    onSupport: () -> Unit,
    onBack: () -> Unit,
) {
    SimpleScreenContent(
        icon = R.drawable.pilot_ic_presentation_error_empty_wallet,
        titleText = stringResource(R.string.presentation_error_empty_wallet_title),
        mainContent = {
            WalletTexts.Body(
                text = stringResource(R.string.presentation_error_empty_wallet_message),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(Sizes.s02))
            WalletTexts.TextLink(
                text = stringResource(id = R.string.presentation_error_empty_wallet_support_text),
                rightIcon = painterResource(id = R.drawable.pilot_ic_link),
                onClick = onSupport,
            )
        },
        bottomBlockContent = {
            ButtonOutlined(
                text = stringResource(id = R.string.global_error_backToHome_button),
                onClick = onBack,
                leftIcon = painterResource(id = R.drawable.pilot_ic_back_button),
            )
        },
    )
}

@WalletAllScreenPreview
@Composable
private fun PresentationEmptyWalletPreview() {
    PilotWalletTheme {
        PresentationEmptyWalletContent(
            onSupport = {},
            onBack = {},
        )
    }
}
