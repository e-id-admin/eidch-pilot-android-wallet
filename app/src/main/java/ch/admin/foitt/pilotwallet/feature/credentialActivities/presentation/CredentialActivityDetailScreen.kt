package ch.admin.foitt.pilotwallet.feature.credentialActivities.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.feature.credentialActivities.presentation.model.ActivityDetailUiState
import ch.admin.foitt.pilotwallet.platform.activity.presentation.composables.ActivityCredentialReceived
import ch.admin.foitt.pilotwallet.platform.activity.presentation.composables.ActivityPresentationAccepted
import ch.admin.foitt.pilotwallet.platform.activity.presentation.composables.ActivityPresentationDeclined
import ch.admin.foitt.pilotwallet.platform.composables.CredentialDetailBottomSheetItem
import ch.admin.foitt.pilotwallet.platform.composables.LoadingOverlay
import ch.admin.foitt.pilotwallet.platform.credential.presentation.CredentialClaimsScreenContent
import ch.admin.foitt.pilotwallet.platform.credential.presentation.mock.CredentialMocks
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityType
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.CredentialActivityDetailNavArg
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts
import ch.admin.foitt.pilotwallet.theme.warningLight
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    navArgsDelegate = CredentialActivityDetailNavArg::class
)
@Composable
fun CredentialActivityDetailScreen(
    viewModel: CredentialActivityDetailViewModel,
) {
    val showBottomSheet = viewModel.showBottomSheet.collectAsStateWithLifecycle().value

    if (showBottomSheet) {
        CredentialActivityDetailBottomSheet(
            onDismiss = viewModel::onBottomSheetDismiss,
            onDelete = viewModel::onDelete,
        )
    }

    CredentialActivityDetailScreenContent(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
    )
}

@Composable
private fun CredentialActivityDetailScreenContent(
    uiState: ActivityDetailUiState,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when (uiState) {
            ActivityDetailUiState.Loading -> LoadingOverlay(showOverlay = true)
            is ActivityDetailUiState.Success -> SuccessContent(uiState)
        }
    }
}

@Composable
private fun SuccessContent(uiState: ActivityDetailUiState.Success) {
    CredentialClaimsScreenContent(
        modifier = Modifier.padding(bottom = Sizes.s04),
        topContent = { Header(uiState) },
        bottomContent = { Footer(uiState) },
        title = stringResource(id = R.string.activity_details_claims_title),
        claims = uiState.claims,
        credentialCardState = uiState.cardState,
    )
}

@Composable
private fun Header(activityDetail: ActivityDetailUiState.Success) {
    val modifier = Modifier.padding(start = Sizes.s02, end = Sizes.s02, bottom = Sizes.s04)
    when (activityDetail.type) {
        ActivityType.CREDENTIAL_RECEIVED -> ActivityCredentialReceived(
            modifier = modifier,
            issuerName = activityDetail.actor,
            dateTimeString = activityDetail.createdAt,
        )
        ActivityType.PRESENTATION_ACCEPTED -> ActivityPresentationAccepted(
            modifier = modifier,
            verifierName = activityDetail.actor,
            dateTimeString = activityDetail.createdAt
        )
        ActivityType.PRESENTATION_DECLINED -> ActivityPresentationDeclined(
            modifier = modifier,
            verifierName = activityDetail.actor,
            dateTimeString = activityDetail.createdAt
        )
    }
}

@Composable
private fun Footer(uiState: ActivityDetailUiState.Success) {
    Column(modifier = Modifier.padding(horizontal = Sizes.s08)) {
        Spacer(modifier = Modifier.height(Sizes.s06))
        WalletTexts.TitleSmall(
            text = stringResource(id = R.string.activity_details_verifier),
        )
        Spacer(modifier = Modifier.height(Sizes.s06))
        Verifier(inviterName = uiState.actor, inviterImage = uiState.actorLogo)
    }
}

@Composable
private fun Verifier(
    inviterName: String,
    inviterImage: Painter?,
) = Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
) {
    inviterImage?.let {
        Image(
            painter = inviterImage,
            modifier = Modifier.size(Sizes.s08),
            contentScale = ContentScale.Fit,
            contentDescription = null
        )
    } ?: Spacer(
        modifier = Modifier.size(Sizes.s12)
    )
    Spacer(modifier = Modifier.size(Sizes.s04))
    WalletTexts.BodyLarge(text = inviterName)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CredentialActivityDetailBottomSheet(
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        CredentialDetailBottomSheetItem(
            icon = R.drawable.pilot_ic_bin_bb,
            title = stringResource(id = R.string.activity_details_menu_delete_text),
            onClick = onDelete,
            color = MaterialTheme.colorScheme.warningLight
        )
    }
}

@WalletAllScreenPreview
@Composable
private fun CredentialActivityDetailScreenPreview() {
    PilotWalletTheme {
        CredentialActivityDetailScreenContent(
            uiState = ActivityDetailUiState.Success(
                type = ActivityType.PRESENTATION_ACCEPTED,
                actor = "LicenceCheck",
                actorLogo = painterResource(id = R.drawable.pilot_ic_strassenverkehrsamt),
                createdAt = "19. Dec | 12:19",
                cardState = CredentialMocks.cardState01,
                claims = CredentialMocks.claimList,
            ),
        )
    }
}
