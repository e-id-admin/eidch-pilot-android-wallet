package ch.admin.foitt.pilotwallet.feature.home.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.times
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.ButtonPrimary
import ch.admin.foitt.pilotwallet.platform.composables.ScrollableBoxWithStickyBottom
import ch.admin.foitt.pilotwallet.platform.credential.presentation.CredentialCard
import ch.admin.foitt.pilotwallet.platform.credential.presentation.mock.CredentialMocks
import ch.admin.foitt.pilotwallet.platform.credential.presentation.model.CredentialCardState
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
) {
    HomeScreenContent(
        screenState = viewModel.screenState.collectAsStateWithLifecycle().value,
        onQrScan = viewModel::onQrScan,
        onMoreInfo = viewModel::onMoreInfo,
        onNoQr = viewModel::onNoQr,
    )
}

@Composable
private fun HomeScreenContent(
    screenState: HomeScreenState,
    onQrScan: () -> Unit,
    onMoreInfo: () -> Unit,
    onNoQr: () -> Unit,
) = ScrollableBoxWithStickyBottom(
    modifier = Modifier,
    useStatusBarInsets = false,
    contentPadding = PaddingValues(
        top = Sizes.s04,
        start = Sizes.s04,
        end = Sizes.s04,
        bottom = Sizes.s06
    ),
    stickyBottomContent = {
        ButtonPrimary(
            text = stringResource(id = R.string.home_qr_code_scan_button),
            leftIcon = painterResource(id = R.drawable.pilot_ic_qrscanner),
            onClick = onQrScan,
        )
    },
) {
    when (screenState) {
        HomeScreenState.Initial -> {
            // TODO define what happens there
        }
        is HomeScreenState.Credentials -> Credentials(
            credentialsState = screenState.credentials,
            onCredentialClick = screenState.onCredentialClick,
        )
        HomeScreenState.NoCredential -> NoCredentialContent()
        is HomeScreenState.Introduction -> IntroContent(
            onMoreInfo = onMoreInfo,
            onNoQr = onNoQr,
        )
    }
}

@Composable
private fun NoCredentialContent() = Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(Sizes.s04)
) {
    Spacer(modifier = Modifier.height(Sizes.s16))
    Image(
        painter = painterResource(id = R.drawable.pilot_ic_home),
        contentDescription = null,
        modifier = Modifier.fillMaxWidth()
    )
    WalletTexts.Body(text = stringResource(id = R.string.home_empty_view_had_credentials_text))
}

@Composable
private fun IntroContent(
    onMoreInfo: () -> Unit,
    onNoQr: () -> Unit,
) = Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(Sizes.s04)
) {
    WalletTexts.TitleScreenMultiLine(
        text = stringResource(id = R.string.home_empty_view_no_credentials_title)
    )
    Image(
        painter = painterResource(id = R.drawable.pilot_ic_home),
        contentDescription = null,
        modifier = Modifier.fillMaxWidth()
    )
    WalletTexts.Body(text = stringResource(id = R.string.home_empty_view_no_credentials_intro_text))

    WalletTexts.TextLink(
        text = stringResource(id = R.string.home_empty_view_no_credentials_more_info_text),
        onClick = onMoreInfo,
        rightIcon = painterResource(id = R.drawable.pilot_ic_link),
    )
    WalletTexts.Body(text = stringResource(id = R.string.home_empty_view_no_credentials_scan_text))

    WalletTexts.TextLink(
        text = stringResource(id = R.string.home_empty_view_no_credentials_qr_code_text),
        onClick = onNoQr,
        rightIcon = painterResource(id = R.drawable.pilot_ic_link),
    )
}

@Composable
private fun Credentials(
    credentialsState: List<CredentialCardState>,
    onCredentialClick: (id: Long) -> Unit,
) = credentialsState.forEachIndexed { index, credentialState ->
    val offset = index * (Sizes.credentialIconSize + Sizes.s06)
    CredentialCard(
        credentialCardState = credentialState,
        modifier = Modifier.padding(top = offset),
        onClick = { onCredentialClick(credentialState.credentialId) },
    )
}

@WalletAllScreenPreview
@Composable
private fun HomeScreenPreview() {
    PilotWalletTheme {
        HomeScreenContent(
            screenState = HomeScreenState.Credentials(
                credentials = CredentialMocks.cardStates.toList().map { it.value() },
                onCredentialClick = {},
            ),
            onQrScan = {},
            onMoreInfo = {},
            onNoQr = {},
        )
    }
}
