package ch.admin.foitt.pilotwallet.feature.credentialOffer.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.feature.credentialOffer.domain.model.CredentialOfferIssuer
import ch.admin.foitt.pilotwallet.platform.composables.AcceptOrDeclineBottomButtons
import ch.admin.foitt.pilotwallet.platform.composables.InvitationHeader
import ch.admin.foitt.pilotwallet.platform.composables.LoadingOverlay
import ch.admin.foitt.pilotwallet.platform.composables.SpacerBottom
import ch.admin.foitt.pilotwallet.platform.composables.SpacerTop
import ch.admin.foitt.pilotwallet.platform.credential.presentation.CredentialClaimsScreenContent
import ch.admin.foitt.pilotwallet.platform.credential.presentation.CredentialCorrectnessInfo
import ch.admin.foitt.pilotwallet.platform.credential.presentation.mock.CredentialMocks
import ch.admin.foitt.pilotwallet.platform.credential.presentation.model.CredentialCardState
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.CredentialOfferNavArg
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimData
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination(
    navArgsDelegate = CredentialOfferNavArg::class
)
fun CredentialOfferScreen(
    viewModel: CredentialOfferViewModel,
) {
    BackHandler {
        viewModel.onDeclineClicked()
    }
    CredentialOfferScreenContent(
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        issuer = viewModel.issuer.collectAsStateWithLifecycle().value,
        claims = viewModel.claims.collectAsStateWithLifecycle().value,
        credentialCardState = viewModel.credentialCardState.collectAsStateWithLifecycle().value,
        onAccept = viewModel::onAcceptClicked,
        onDecline = viewModel::onDeclineClicked,
    )
}

@Composable
private fun CredentialOfferScreenContent(
    isLoading: Boolean,
    issuer: CredentialOfferIssuer?,
    credentialCardState: CredentialCardState,
    claims: List<CredentialClaimData>,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) = Box(modifier = Modifier.fillMaxSize()) {
    SpacerTop(
        backgroundColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.align(Alignment.TopStart),
        useStatusBarInsets = true,
    )
    CredentialClaimsScreenContent(
        topContent = {
            issuer?.let {
                InvitationHeader(
                    inviterName = issuer.name,
                    inviterImage = issuer.painter,
                    message = stringResource(id = R.string.credential_offer_invitation),
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = Sizes.s04)
                )
            }
        },
        bottomContent = {
            Spacer(modifier = Modifier.height(Sizes.s10))
            CredentialCorrectnessInfo(icon = painterResource(id = R.drawable.pilot_ic_hand))
            CredentialOfferButtons(
                onAccept = onAccept,
                onDecline = onDecline,
            )
        },
        title = stringResource(id = R.string.credential_offer_content_section_title),
        claims = claims,
        credentialCardState = credentialCardState,
    )
    SpacerBottom(
        backgroundColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.align(Alignment.BottomStart),
        useNavigationBarInsets = true,
    )
    LoadingOverlay(showOverlay = isLoading)
}

@Composable
private fun CredentialOfferButtons(
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Sizes.s04, end = Sizes.s04, bottom = Sizes.s06)
            .navigationBarsPadding()
    ) {
        AcceptOrDeclineBottomButtons(
            acceptButtonText = stringResource(id = R.string.credential_offer_acceptButton),
            declineButtonText = stringResource(id = R.string.credential_offer_refuseButton),
            isAcceptEnabled = true,
            acceptButtonIcon = painterResource(id = R.drawable.pilot_ic_checkmark_button),
            declineButtonIcon = painterResource(id = R.drawable.pilot_ic_cross),
            onAccept = onAccept,
            onDecline = onDecline,
        )
    }
}

@WalletAllScreenPreview
@Composable
private fun InvitationScreenPreview() {
    PilotWalletTheme {
        CredentialOfferScreenContent(
            isLoading = false,
            issuer = CredentialOfferIssuer(
                name = "Test Issuer",
                painter = painterResource(id = R.drawable.pilot_ic_strassenverkehrsamt)
            ),
            credentialCardState = CredentialMocks.cardState01,
            claims = CredentialMocks.claimList,
            onAccept = {},
            onDecline = {},
        )
    }
}
