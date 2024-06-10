package ch.admin.foitt.pilotwallet.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletListItems
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    SettingsScreenContent(
        onSecurityScreen = viewModel::onSecurityScreen,
        onGetVerified = viewModel::onGetVerified,
        onFeedback = viewModel::onFeedback,
        onHelp = viewModel::onHelp,
        onContact = viewModel::onContact,
        onImpressumScreen = viewModel::onImpressumScreen,
        onLicencesScreen = viewModel::onLicencesScreen,
    )
}

@Composable
private fun SettingsScreenContent(
    onSecurityScreen: () -> Unit,
    onGetVerified: () -> Unit,
    onFeedback: () -> Unit,
    onHelp: () -> Unit,
    onContact: () -> Unit,
    onImpressumScreen: () -> Unit,
    onLicencesScreen: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(state = rememberScrollState())
            .navigationBarsPadding()
            .padding(
                top = Sizes.s04,
                bottom = Sizes.s04,
            )
    ) {
        SettingsSection(onSecurityScreen, onGetVerified)
        Spacer(modifier = Modifier.height(Sizes.s10))

        SupportSection(onFeedback, onHelp, onContact)
        Spacer(modifier = Modifier.height(Sizes.s10))

        InfoSection(onImpressumScreen, onLicencesScreen)
    }
}

@Composable
private fun SettingsSection(
    onSecurityScreen: () -> Unit,
    onGetVerified: () -> Unit,
) {
    WalletListItems.SimpleListItem(
        leadingIcon = R.drawable.pilot_ic_security,
        title = stringResource(id = R.string.settings_security),
        onItemClick = onSecurityScreen,
        trailingIcon = R.drawable.pilot_ic_settings_next,
    )
    WalletListItems.SimpleListItem(
        leadingIcon = R.drawable.pilot_ic_magnifier,
        title = stringResource(id = R.string.settings_getVerified),
        onItemClick = onGetVerified,
        trailingIcon = R.drawable.pilot_ic_settings_next,
        showDivider = false,
    )
}

@Composable
private fun SupportSection(
    onFeedback: () -> Unit,
    onHelp: () -> Unit,
    onContact: () -> Unit
) {
    WalletListItems.SimpleListItem(
        leadingIcon = R.drawable.pilot_ic_feedback,
        title = stringResource(id = R.string.settings_feedback),
        onItemClick = onFeedback,
        trailingIcon = R.drawable.pilot_ic_settings_link,
    )
    WalletListItems.SimpleListItem(
        leadingIcon = R.drawable.pilot_ic_help,
        title = stringResource(id = R.string.settings_help),
        onItemClick = onHelp,
        trailingIcon = R.drawable.pilot_ic_settings_link,
    )
    WalletListItems.SimpleListItem(
        leadingIcon = R.drawable.pilot_ic_contact,
        title = stringResource(id = R.string.settings_contact),
        onItemClick = onContact,
        trailingIcon = R.drawable.pilot_ic_settings_link,
        showDivider = false,
    )
}

@Composable
private fun InfoSection(onImpressumScreen: () -> Unit, onLicencesScreen: () -> Unit) {
    WalletListItems.SimpleListItem(
        leadingIcon = R.drawable.pilot_ic_impressum,
        title = stringResource(id = R.string.settings_impressum),
        onItemClick = onImpressumScreen,
        trailingIcon = R.drawable.pilot_ic_settings_next,
    )
    WalletListItems.SimpleListItem(
        leadingIcon = R.drawable.pilot_ic_document,
        title = stringResource(id = R.string.settings_licences),
        onItemClick = onLicencesScreen,
        trailingIcon = R.drawable.pilot_ic_settings_next,
        showDivider = false,
    )
}

@WalletAllScreenPreview
@Composable
fun SettingsScreenPreview() {
    PilotWalletTheme {
        SettingsScreenContent(
            onSecurityScreen = {},
            onGetVerified = {},
            onFeedback = {},
            onHelp = {},
            onContact = {},
            onImpressumScreen = {},
            onLicencesScreen = {},
        )
    }
}
