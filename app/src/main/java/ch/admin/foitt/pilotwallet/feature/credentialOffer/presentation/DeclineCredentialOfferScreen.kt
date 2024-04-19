package ch.admin.foitt.pilotwallet.feature.credentialOffer.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.ButtonOutlined
import ch.admin.foitt.pilotwallet.platform.composables.ButtonWarning
import ch.admin.foitt.pilotwallet.platform.composables.SimpleScreenContent
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.DeclineCredentialOfferNavArg
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    navArgsDelegate = DeclineCredentialOfferNavArg::class,
)
@Composable
fun DeclineCredentialOfferScreen(
    viewModel: DeclineCredentialOfferViewModel,
) {
    DeclineCredentialOfferScreenContent(
        onCancel = viewModel::onCancel,
        onDecline = viewModel::onDecline,
    )
}

@Composable
private fun DeclineCredentialOfferScreenContent(
    onCancel: () -> Unit,
    onDecline: () -> Unit,
) {
    SimpleScreenContent(
        icon = R.drawable.pilot_ic_delete_credential,
        titleText = stringResource(id = R.string.credential_offer_refuse_confirmation_title),
        mainText = stringResource(id = R.string.credential_offer_refuse_confirmation_message),
        bottomBlockContent = {
            ButtonOutlined(
                text = stringResource(id = R.string.credential_offer_refuse_confirmation_cancelButton),
                leftIcon = painterResource(id = R.drawable.pilot_ic_cross),
                onClick = onCancel
            )
            ButtonWarning(
                text = stringResource(id = R.string.credential_offer_refuse_confirmation_refuseButton),
                leftIcon = painterResource(id = R.drawable.pilot_ic_bin_bb),
                onClick = onDecline
            )
        },
    )
}

@WalletAllScreenPreview
@Composable
private fun DeclineCredentialOfferScreenContentPreview() {
    PilotWalletTheme {
        DeclineCredentialOfferScreenContent(
            onCancel = {},
            onDecline = {},
        )
    }
}
