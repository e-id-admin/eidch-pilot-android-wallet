package ch.admin.foitt.pilotwallet.feature.recentCredentialActivities.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.feature.recentCredentialActivities.presentation.composables.RecentCredentialActivitiesBottomSheet
import ch.admin.foitt.pilotwallet.platform.activity.presentation.composables.ActivityCredentialReceived
import ch.admin.foitt.pilotwallet.platform.activity.presentation.composables.ActivityPresentationAccepted
import ch.admin.foitt.pilotwallet.platform.activity.presentation.composables.ActivityPresentationDeclined
import ch.admin.foitt.pilotwallet.platform.activity.presentation.composables.ShowAllActivities
import ch.admin.foitt.pilotwallet.platform.activity.presentation.mock.ActivityMocks
import ch.admin.foitt.pilotwallet.platform.activity.presentation.model.ActivityListItem
import ch.admin.foitt.pilotwallet.platform.composables.LoadingOverlay
import ch.admin.foitt.pilotwallet.platform.composables.ScrollableWithStickyBottom
import ch.admin.foitt.pilotwallet.platform.credential.presentation.CredentialCard
import ch.admin.foitt.pilotwallet.platform.credential.presentation.model.CredentialCardState
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.RecentCredentialActivitiesNavArg
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    navArgsDelegate = RecentCredentialActivitiesNavArg::class
)
@Composable
fun RecentCredentialActivitiesScreen(viewModel: RecentCredentialActivitiesViewModel) {
    val context = LocalContext.current
    val showPoliceControlItem = viewModel.showPoliceControlItem.collectAsStateWithLifecycle(false).value
    val showBottomSheet = viewModel.showBottomSheet.collectAsStateWithLifecycle().value
    val showErrorToast = viewModel.showErrorToast.collectAsStateWithLifecycle().value

    LaunchedEffect(showErrorToast) {
        if (showErrorToast) {
            Toast.makeText(context, "Could not get activities", Toast.LENGTH_SHORT).show()
            viewModel.resetErrorToast()
        }
    }

    if (showBottomSheet) {
        RecentCredentialActivitiesBottomSheet(
            showPoliceControlItem = showPoliceControlItem,
            onDismiss = viewModel::onBottomSheetDismiss,
            onShowCredentialContent = viewModel::onShowCredentialContent,
            onDelete = viewModel::onDelete,
            onShowPoliceControl = viewModel::onShowQrCode,
        )
    }

    RecentCredentialActivitiesScreenContent(
        uiState = viewModel.uiState.collectAsStateWithLifecycle(initialValue = RecentCredentialActivitiesUiState.Loading).value,
        isRefreshing = viewModel.isRefreshing.collectAsStateWithLifecycle().value,
        onActivityClick = viewModel::onActivityClick,
        onShowAllActivities = viewModel::onShowAllActivities,
        onRefresh = viewModel::onRefresh,
    )
}

@Composable
private fun RecentCredentialActivitiesScreenContent(
    uiState: RecentCredentialActivitiesUiState,
    isRefreshing: Boolean,
    onActivityClick: (Long) -> Unit,
    onShowAllActivities: () -> Unit,
    onRefresh: () -> Unit,
) {
    when (uiState) {
        RecentCredentialActivitiesUiState.Loading -> LoadingOverlay(showOverlay = true)
        is RecentCredentialActivitiesUiState.Success -> RecentCredentialActivitiesContent(
            isRefreshing = isRefreshing,
            credentialCardState = uiState.credentialCardState,
            activities = uiState.activities,
            onRefresh = onRefresh,
            onActivityClick = onActivityClick,
            onShowAllActivities = onShowAllActivities,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RecentCredentialActivitiesContent(
    isRefreshing: Boolean,
    credentialCardState: CredentialCardState,
    activities: List<ActivityListItem>,
    onRefresh: () -> Unit,
    onActivityClick: (Long) -> Unit,
    onShowAllActivities: () -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh,
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        ScrollableWithStickyBottom(
            modifier = Modifier,
            useStatusBarInsets = false,
            stickyBottomContent = {},
            contentPadding = PaddingValues(
                top = Sizes.s04,
                start = Sizes.s04,
                end = Sizes.s04,
                bottom = Sizes.s06
            ),
            scrollableContent = {
                RecentCredentialActivitiesScrollableContent(
                    credentialCardState = credentialCardState,
                    activityListItems = activities,
                    onActivityClick = onActivityClick,
                    onShowAllActivities = onShowAllActivities,
                )
            },
        )
        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = isRefreshing,
            state = pullRefreshState,
        )
    }
}

@Composable
private fun RecentCredentialActivitiesScrollableContent(
    credentialCardState: CredentialCardState,
    activityListItems: List<ActivityListItem>,
    onActivityClick: (Long) -> Unit,
    onShowAllActivities: () -> Unit,
) {
    CredentialCard(
        credentialCardState = credentialCardState
    )
    if (activityListItems.isNotEmpty()) {
        Spacer(modifier = Modifier.height(Sizes.s08))
        WalletTexts.TitleMedium(
            modifier = Modifier.padding(horizontal = Sizes.s02),
            text = stringResource(id = R.string.credential_activities_header_text)
        )
        Spacer(modifier = Modifier.height(Sizes.s02))
        for (activityListItem in activityListItems) {
            when (activityListItem) {
                is ActivityListItem.CredentialReceived -> {
                    ActivityCredentialReceived(
                        issuerName = activityListItem.issuer,
                        dateTimeString = activityListItem.dateTimeString,
                    )
                }
                is ActivityListItem.PresentationAccepted -> {
                    ActivityPresentationAccepted(
                        verifierName = activityListItem.verifier,
                        dateTimeString = activityListItem.dateTimeString,
                        onClick = { onActivityClick(activityListItem.id) },
                    )
                }
                is ActivityListItem.PresentationDeclined -> {
                    ActivityPresentationDeclined(
                        verifierName = activityListItem.verifier,
                        dateTimeString = activityListItem.dateTimeString,
                        onClick = { onActivityClick(activityListItem.id) },
                    )
                }
            }
            HorizontalDivider()
        }
        ShowAllActivities(
            onClick = onShowAllActivities
        )
    }
}

private class RecentCredentialActivitiesPreviewParams : PreviewParameterProvider<RecentCredentialActivitiesUiState> {
    override val values = sequenceOf(
        RecentCredentialActivitiesUiState.Loading,
        RecentCredentialActivitiesUiState.Success(
            CredentialCardState.EMPTY,
            ActivityMocks.recentActivities
        ),
    )
}

@WalletAllScreenPreview
@Composable
private fun RecentCredentialActivitiesScreenPreview(
    @PreviewParameter(RecentCredentialActivitiesPreviewParams::class)
    screenState: RecentCredentialActivitiesUiState,
) {
    PilotWalletTheme {
        RecentCredentialActivitiesScreenContent(
            uiState = screenState,
            isRefreshing = false,
            onActivityClick = {},
            onShowAllActivities = {},
            onRefresh = {},
        )
    }
}
