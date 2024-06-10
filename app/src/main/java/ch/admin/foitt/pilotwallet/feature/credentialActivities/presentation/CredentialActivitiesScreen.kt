package ch.admin.foitt.pilotwallet.feature.credentialActivities.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.feature.credentialActivities.presentation.composables.SwipeToDeleteContainer
import ch.admin.foitt.pilotwallet.platform.activity.presentation.composables.ActivityCredentialReceived
import ch.admin.foitt.pilotwallet.platform.activity.presentation.composables.ActivityPresentationAccepted
import ch.admin.foitt.pilotwallet.platform.activity.presentation.composables.ActivityPresentationDeclined
import ch.admin.foitt.pilotwallet.platform.activity.presentation.mock.ActivityMocks
import ch.admin.foitt.pilotwallet.platform.activity.presentation.model.ActivityListItem
import ch.admin.foitt.pilotwallet.platform.composables.ButtonOutlined
import ch.admin.foitt.pilotwallet.platform.composables.LoadingOverlay
import ch.admin.foitt.pilotwallet.platform.composables.ScrollableLazyColumnWithStickyBottom
import ch.admin.foitt.pilotwallet.platform.composables.SimpleScreenContent
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.CredentialActivitiesNavArg
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    navArgsDelegate = CredentialActivitiesNavArg::class
)
@Composable
fun CredentialActivitiesScreen(
    viewModel: CredentialActivitiesViewModel
) {
    CredentialActivitiesScreenContent(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onClick = viewModel::onActivityClick,
        onDelete = viewModel::onDelete,
        onBackToHome = viewModel::onBackToHome,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CredentialActivitiesScreenContent(
    uiState: CredentialActivitiesUiState,
    onClick: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onBackToHome: () -> Unit,
) {
    // custom transition to have access to the content key in order for crossfade to
    // only be triggered for actual state changes not when just the activities change
    val transition = updateTransition(
        targetState = uiState,
        label = "UiStateCrossfade",
    )
    transition.Crossfade(
        modifier = Modifier,
        animationSpec = tween(),
        contentKey = { state -> state::class.simpleName }
    ) { state ->
        when (state) {
            is CredentialActivitiesUiState.Loading -> LoadingOverlay(showOverlay = true)
            is CredentialActivitiesUiState.Empty -> EmptyContent(onBackToHome)
            is CredentialActivitiesUiState.Activities -> Activities(
                state.activities,
                onClick,
                onDelete,
            )
        }
    }
}

@Composable
fun EmptyContent(
    onBack: () -> Unit,
) {
    SimpleScreenContent(
        icon = R.drawable.pilot_ic_presentation_error_empty_wallet,
        titleText = stringResource(R.string.activities_empty_state_title),
        mainContent = {
            WalletTexts.Body(
                text = stringResource(R.string.activities_empty_state_text),
                modifier = Modifier.fillMaxWidth(),
            )
        },
        bottomBlockContent = {
            ButtonOutlined(
                text = stringResource(id = R.string.global_error_backToHome_button),
                onClick = onBack,
                leftIcon = painterResource(id = R.drawable.pilot_ic_back_button),
            )
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Activities(
    activitiesByMonth: Map<String, List<ActivityListItem>>,
    onClick: (Long) -> Unit,
    onDelete: (Long) -> Unit,
) {
    ScrollableLazyColumnWithStickyBottom(
        useStatusBarInsets = false,
        stickyBottomContent = {},
        stickyBottomPadding = PaddingValues(
            start = Sizes.s04,
            end = Sizes.s04,
        ),
        contentPadding = PaddingValues(
            start = Sizes.s04,
            end = Sizes.s04,
            bottom = Sizes.s06,
        )
    ) {
        for (pair in activitiesByMonth) {
            item {
                MonthHeader(title = pair.key)
            }

            pair.value.forEachIndexed { index, activity ->
                item(
                    key = activity.id
                ) {
                    ActivityItem(
                        modifier = Modifier.animateItemPlacement(),
                        activity = activity,
                        showDivider = index < pair.value.lastIndex,
                        onDelete = onDelete,
                        onClick = onClick,
                    )
                }
            }
        }
    }
}

@Composable
fun MonthHeader(
    title: String,
) {
    Spacer(modifier = Modifier.height(Sizes.s06))
    WalletTexts.LabelMedium(
        text = title
    )
    Spacer(modifier = Modifier.height(Sizes.s02))
}

@Composable
fun ActivityItem(
    modifier: Modifier = Modifier,
    activity: ActivityListItem,
    showDivider: Boolean,
    onDelete: (Long) -> Unit,
    onClick: (Long) -> Unit,
) = SwipeToDeleteContainer(
    modifier = modifier,
    onDelete = { onDelete(activity.id) }
) {
    Column {
        when (activity) {
            is ActivityListItem.CredentialReceived ->
                ActivityCredentialReceived(
                    issuerName = activity.issuer,
                    dateTimeString = activity.dateTimeString
                )
            is ActivityListItem.PresentationAccepted ->
                ActivityPresentationAccepted(
                    verifierName = activity.verifier,
                    dateTimeString = activity.dateTimeString,
                    onClick = { onClick(activity.id) },
                )
            is ActivityListItem.PresentationDeclined ->
                ActivityPresentationDeclined(
                    verifierName = activity.verifier,
                    dateTimeString = activity.dateTimeString,
                    onClick = { onClick(activity.id) },
                )
        }
        if (showDivider) {
            HorizontalDivider()
        }
    }
}

private class CredentialActivitiesPreviewParams : PreviewParameterProvider<CredentialActivitiesUiState> {
    override val values = sequenceOf(
        CredentialActivitiesUiState.Loading,
        CredentialActivitiesUiState.Empty,
        CredentialActivitiesUiState.Activities(
            activities = ActivityMocks.activityMap
        ),
    )
}

@WalletAllScreenPreview
@Composable
private fun CredentialActivitiesPreview(
    @PreviewParameter(CredentialActivitiesPreviewParams::class) screenState: CredentialActivitiesUiState,
) {
    PilotWalletTheme {
        CredentialActivitiesScreenContent(
            uiState = screenState,
            onClick = {},
            onDelete = {},
            onBackToHome = {},
        )
    }
}
