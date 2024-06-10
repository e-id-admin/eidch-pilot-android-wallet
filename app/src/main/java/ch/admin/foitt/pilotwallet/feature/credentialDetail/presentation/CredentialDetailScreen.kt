package ch.admin.foitt.pilotwallet.feature.credentialDetail.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.feature.credentialDetail.presentation.composables.CredentialDetailBottomSheet
import ch.admin.foitt.pilotwallet.platform.composables.LoadingOverlay
import ch.admin.foitt.pilotwallet.platform.credential.presentation.CredentialClaimsScreenContent
import ch.admin.foitt.pilotwallet.platform.credential.presentation.CredentialCorrectnessInfo
import ch.admin.foitt.pilotwallet.platform.credential.presentation.mock.CredentialMocks
import ch.admin.foitt.pilotwallet.platform.credential.presentation.model.CredentialCardState
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.CredentialDetailNavArg
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimData
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    navArgsDelegate = CredentialDetailNavArg::class
)
@Composable
fun CredentialDetailScreen(
    viewModel: CredentialDetailViewModel,
) {
    val showPoliceControlItem = viewModel.showPoliceControlItem.collectAsStateWithLifecycle(false).value
    val showBottomSheet = viewModel.showBottomSheet.collectAsStateWithLifecycle().value

    if (showBottomSheet) {
        CredentialDetailBottomSheet(
            onDismiss = viewModel::onBottomSheetDismiss,
            onDelete = viewModel::onDelete,
            onShowPoliceControl = viewModel::onShowQrCode,
            showPoliceControlItem = showPoliceControlItem,
            onShowActivities = viewModel::onShowActivities,
        )
    }

    CredentialDetailScreenContent(
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        isRefreshing = viewModel.isRefreshing.collectAsStateWithLifecycle().value,
        credentialCardState = viewModel.credentialCardState.collectAsStateWithLifecycle(initialValue = CredentialCardState.EMPTY).value,
        claims = viewModel.credentialClaims.collectAsStateWithLifecycle(initialValue = emptyList()).value,
        onRefresh = viewModel::onRefresh,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CredentialDetailScreenContent(
    isLoading: Boolean,
    isRefreshing: Boolean,
    credentialCardState: CredentialCardState,
    claims: List<CredentialClaimData>,
    onRefresh: () -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh,
    )
    Box(
        modifier = Modifier
            .pullRefresh(pullRefreshState)
            .fillMaxSize()
    ) {
        CredentialClaimsScreenContent(
            bottomContent = {
                Spacer(modifier = Modifier.height(Sizes.s04))
                CredentialCorrectnessInfo(icon = painterResource(id = R.drawable.pilot_ic_info))
            },
            title = stringResource(id = R.string.credential_offer_content_section_title),
            claims = claims,
            credentialCardState = credentialCardState,
        )
        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = isRefreshing,
            state = pullRefreshState,
        )
        LoadingOverlay(showOverlay = isLoading)
    }
}

@WalletAllScreenPreview
@Composable
private fun CredentialDetailScreenPreview() {
    PilotWalletTheme {
        CredentialDetailScreenContent(
            isLoading = false,
            isRefreshing = false,
            credentialCardState = CredentialMocks.cardState01,
            claims = CredentialMocks.claimList,
            onRefresh = {},
        )
    }
}
