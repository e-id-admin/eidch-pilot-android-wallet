package ch.admin.foitt.pilotwallet.feature.home.presentation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.times
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.feature.home.presentation.composables.ActivityItem
import ch.admin.foitt.pilotwallet.platform.activity.presentation.mock.ActivityMocks
import ch.admin.foitt.pilotwallet.platform.activity.presentation.model.HomeScreenActivity
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
    val context = LocalContext.current
    val showErrorToast = viewModel.showErrorToast.collectAsStateWithLifecycle().value

    LaunchedEffect(showErrorToast) {
        if (showErrorToast) {
            Toast.makeText(context, "Could not get latest activity", Toast.LENGTH_SHORT).show()
            viewModel.resetShowErrorToast()
        }
    }

    HomeScreenContent(
        screenState = viewModel.screenState.collectAsStateWithLifecycle().value,
        isRefreshing = viewModel.isRefreshing.collectAsStateWithLifecycle().value,
        onQrScan = viewModel::onQrScan,
        onMoreInfo = viewModel::onMoreInfo,
        onNoQr = viewModel::onNoQr,
        onRefresh = viewModel::onRefresh,
        onActivity = viewModel::onActivity,
        onAllActivities = viewModel::onShowAllActivities,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun HomeScreenContent(
    screenState: HomeScreenState,
    isRefreshing: Boolean,
    onQrScan: () -> Unit,
    onMoreInfo: () -> Unit,
    onNoQr: () -> Unit,
    onRefresh: () -> Unit,
    onActivity: (activityId: Long) -> Unit,
    onAllActivities: (credentialId: Long) -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh,
    )
    ScrollableBoxWithStickyBottom(
        modifier = if (screenState is HomeScreenState.Credentials) {
            Modifier.pullRefresh(pullRefreshState)
        } else {
            Modifier
        },
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
                latestActivity = screenState.latestActivity,
                onCredentialClick = screenState.onCredentialClick,
                onActivity = onActivity,
                onAllActivities = onAllActivities,
            )

            HomeScreenState.NoCredential -> NoCredentialContent()
            is HomeScreenState.Introduction -> IntroContent(
                onMoreInfo = onMoreInfo,
                onNoQr = onNoQr,
            )
        }
        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = isRefreshing,
            state = pullRefreshState,
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
    latestActivity: HomeScreenActivity?,
    onCredentialClick: (id: Long) -> Unit,
    onActivity: (activityId: Long) -> Unit,
    onAllActivities: (credentialId: Long) -> Unit,
) = Column {
    Box {
        credentialsState.forEachIndexed { index, credentialState ->
            val offset = index * (Sizes.credentialIconSize + Sizes.s06)
            CredentialCard(
                credentialCardState = credentialState,
                modifier = Modifier.padding(top = offset),
                onClick = { onCredentialClick(credentialState.credentialId) },
            )
        }
    }
    latestActivity?.let {
        Spacer(modifier = Modifier.height(Sizes.s06))
        LatestActivityContainer(
            latestActivity = latestActivity,
            onActivity = onActivity,
            onAllActivities = onAllActivities,
        )
    }
}

@Composable
fun LatestActivityContainer(
    latestActivity: HomeScreenActivity,
    onActivity: (activityId: Long) -> Unit,
    onAllActivities: (credentialId: Long) -> Unit,
) = Column {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WalletTexts.LabelSmall(
            text = stringResource(id = R.string.home_last_activity_header_text)
        )
        WalletTexts.LabelSmallBoldUnderlined(
            modifier = Modifier
                .clickable {
                    onAllActivities(latestActivity.credentialId)
                }
                .padding(vertical = Sizes.s02),
            text = stringResource(id = R.string.home_last_activity_header_button),
            color = MaterialTheme.colorScheme.primary
        )
    }
    Spacer(modifier = Modifier.height(Sizes.s02))
    ActivityItem(
        activity = latestActivity,
        onActivity = onActivity,
    )
}

@WalletAllScreenPreview
@Composable
private fun HomeScreenPreview() {
    PilotWalletTheme {
        HomeScreenContent(
            screenState = HomeScreenState.Credentials(
                credentials = CredentialMocks.cardStates.toList().map { it.value() },
                latestActivity = ActivityMocks.latestActivity,
                onCredentialClick = {},
            ),
            isRefreshing = false,
            onQrScan = {},
            onMoreInfo = {},
            onNoQr = {},
            onRefresh = {},
            onActivity = {},
            onAllActivities = {}
        )
    }
}
