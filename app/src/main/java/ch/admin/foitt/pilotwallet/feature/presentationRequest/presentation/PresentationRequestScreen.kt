package ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.ButtonOutlined
import ch.admin.foitt.pilotwallet.platform.composables.ButtonTertiary
import ch.admin.foitt.pilotwallet.platform.composables.InvitationHeader
import ch.admin.foitt.pilotwallet.platform.composables.LoadingOverlay
import ch.admin.foitt.pilotwallet.platform.composables.SpacerBottom
import ch.admin.foitt.pilotwallet.platform.composables.SpacerTop
import ch.admin.foitt.pilotwallet.platform.credential.presentation.CredentialClaimsScreenContent
import ch.admin.foitt.pilotwallet.platform.credential.presentation.mock.CredentialMocks
import ch.admin.foitt.pilotwallet.platform.credential.presentation.model.CredentialCardState
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.PresentationRequestNavArg
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimData
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    navArgsDelegate = PresentationRequestNavArg::class
)
@Composable
fun PresentationRequestScreen(viewModel: PresentationRequestViewModel) {
    BackHandler(onBack = viewModel::onDecline)
    PresentationRequestContent(
        verifierName = viewModel.verifierName,
        verifierImage = viewModel.verifierLogoPainter.collectAsStateWithLifecycle().value,
        requestedClaims = viewModel.requestedClaims.collectAsStateWithLifecycle().value,
        credentialCardState = viewModel.credentialState.collectAsStateWithLifecycle().value,
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        isSubmitting = viewModel.isSubmitting.collectAsStateWithLifecycle().value,
        onSubmit = viewModel::submit,
        onDecline = viewModel::onDecline,
    )
}

@Composable
private fun PresentationRequestContent(
    verifierName: String?,
    verifierImage: Painter?,
    requestedClaims: List<CredentialClaimData>,
    credentialCardState: CredentialCardState,
    isLoading: Boolean,
    isSubmitting: Boolean,
    onSubmit: () -> Unit,
    onDecline: () -> Unit,
) = Box(modifier = Modifier.fillMaxSize()) {
    SpacerTop(
        backgroundColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.align(Alignment.TopStart),
        useStatusBarInsets = true,
    )
    CredentialClaimsScreenContent(
        topContent = {
            InvitationHeader(
                inviterName = verifierName ?: stringResource(id = R.string.presentation_verifier_name_unknown),
                inviterImage = verifierImage,
                message = stringResource(id = R.string.presentation_verifier_text),
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = Sizes.s04)
            )
        },
        bottomContent = {
            PresentationRequestButtons(
                onAccept = onSubmit,
                onDecline = onDecline,
                isSubmitting = isSubmitting,
            )
        },
        title = stringResource(id = R.string.presentation_attributes_title),
        claims = requestedClaims,
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
private fun PresentationRequestButtons(
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    isSubmitting: Boolean,
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = Sizes.s04)
) {
    Spacer(modifier = Modifier.height(Sizes.s10))
    ButtonOutlined(
        text = stringResource(id = R.string.presentation_deny_button_text),
        leftIcon = painterResource(id = R.drawable.pilot_ic_cross),
        enabled = !isSubmitting,
        onClick = onDecline,
    )
    Spacer(modifier = Modifier.size(Sizes.s04))
    ButtonTertiary(
        text = stringResource(id = R.string.presentation_accept_button_text),
        onClick = onAccept,
        leftIcon = painterResource(id = R.drawable.pilot_ic_checkmark_button),
        isActive = isSubmitting,
        enabled = !isSubmitting,
        loadingText = stringResource(id = R.string.presentation_send_button_text)
    )
    Spacer(
        modifier = Modifier
            .padding(bottom = Sizes.s06)
            .navigationBarsPadding()
    )
}

@WalletAllScreenPreview
@Composable
private fun PresentationRequestScreenPreview() {
    PilotWalletTheme {
        PresentationRequestContent(
            verifierName = "My Verfifier Name",
            requestedClaims = CredentialMocks.claimList,
            isLoading = false,
            onSubmit = {},
            onDecline = {},
            verifierImage = painterResource(id = R.drawable.pilot_ic_strassenverkehrsamt),
            credentialCardState = CredentialMocks.cardState01,
            isSubmitting = false,
        )
    }
}
