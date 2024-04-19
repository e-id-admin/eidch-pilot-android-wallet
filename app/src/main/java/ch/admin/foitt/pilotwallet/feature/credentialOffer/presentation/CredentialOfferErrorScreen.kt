package ch.admin.foitt.pilotwallet.feature.credentialOffer.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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

@Destination
@Composable
fun CredentialOfferErrorScreen(
    viewModel: CredentialOfferErrorViewModel,
) {
    CredentialOfferErrorScreenContent(
        dateTime = viewModel.dateTime,
        onBack = viewModel::onBack,
    )
}

@Composable
private fun CredentialOfferErrorScreenContent(
    dateTime: String,
    onBack: () -> Unit,
) {
    ResultScreenContent(
        iconRes = R.drawable.pilot_ic_warning_big,
        dateTime = dateTime,
        message = stringResource(id = R.string.presentation_error_title),
        topColor = MaterialTheme.colorScheme.errorBackgroundLight,
        bottomColor = MaterialTheme.colorScheme.errorBackgroundDark,
        bottomContent = {
            ButtonSecondary(
                text = stringResource(id = R.string.global_error_backToHome_button),
                onClick = onBack,
            )
        },
        content = {
            WalletTexts.BodySmallCentered(
                text = stringResource(id = R.string.presentation_error_message),
                color = MaterialTheme.colorScheme.onError,
            )
        },
    )
}

@WalletAllScreenPreview
@Composable
fun CredentialOfferErrorScreenPreview() {
    PilotWalletTheme {
        CredentialOfferErrorScreenContent(
            dateTime = "14th December 2023 | 08:00",
            onBack = {},
        )
    }
}
