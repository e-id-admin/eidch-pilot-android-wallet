package ch.admin.foitt.pilotwallet.feature.credentialDelete.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.ButtonOutlined
import ch.admin.foitt.pilotwallet.platform.composables.ButtonWarning
import ch.admin.foitt.pilotwallet.platform.composables.SimpleScreenContent
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.CredentialDeletionNavArg
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    navArgsDelegate = CredentialDeletionNavArg::class
)
@Composable
fun CredentialDeleteScreen(
    viewModel: CredentialDeleteViewModel,
) {
    CredentialDeleteScreenContent(
        onDismiss = viewModel::onDismiss,
        onDelete = viewModel::onDelete
    )
}

@Composable
private fun CredentialDeleteScreenContent(
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    SimpleScreenContent(
        icon = R.drawable.pilot_ic_delete_credential,
        titleText = stringResource(id = R.string.credential_delete_title),
        mainText = stringResource(id = R.string.credential_delete_text),
        bottomBlockContent = {
            ButtonOutlined(
                text = stringResource(id = R.string.credential_delete_cancel_button),
                modifier = Modifier.fillMaxWidth(),
                leftIcon = painterResource(id = R.drawable.pilot_ic_cross),
                onClick = onDismiss
            )
            ButtonWarning(
                text = stringResource(id = R.string.credential_delete_confirm_button),
                modifier = Modifier.fillMaxWidth(),
                leftIcon = painterResource(id = R.drawable.pilot_ic_bin_bb),
                onClick = onDelete
            )
        }
    )
}

@WalletAllScreenPreview
@Composable
private fun CredentialDeletionContentPreview() {
    PilotWalletTheme {
        CredentialDeleteScreenContent(
            onDismiss = {},
            onDelete = {}
        )
    }
}
