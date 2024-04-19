package ch.admin.foitt.pilotwallet.platform.nointernet.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.ButtonOutlined
import ch.admin.foitt.pilotwallet.platform.composables.ButtonPrimary
import ch.admin.foitt.pilotwallet.platform.composables.LoadingOverlay
import ch.admin.foitt.pilotwallet.platform.composables.SimpleScreenContent
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.NoInternetConnectionNavArg
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination(
    navArgsDelegate = NoInternetConnectionNavArg::class,
)
fun NoInternetConnectionScreen(
    viewModel: NoInternetConnectionViewModel,
) {
    NoInternetConnectionScreenContent(
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        onRetry = viewModel::retry,
        onClose = viewModel::close,
    )
}

@Composable
private fun NoInternetConnectionScreenContent(
    isLoading: Boolean,
    onRetry: () -> Unit,
    onClose: () -> Unit,
) {
    SimpleScreenContent(
        icon = R.drawable.pilot_ic_no_internet,
        titleText = stringResource(id = R.string.emptyState_offlineTitle),
        mainText = stringResource(id = R.string.emptyState_offlineMessage),
        bottomBlockContent = {
            ButtonOutlined(
                text = stringResource(id = R.string.global_back_home),
                leftIcon = painterResource(id = R.drawable.pilot_ic_back_button),
                onClick = onClose,
            )
            ButtonPrimary(
                text = stringResource(id = R.string.emptyState_offlineRetryButton),
                leftIcon = painterResource(id = R.drawable.pilot_ic_retry),
                onClick = onRetry,
            )
        }
    )
    LoadingOverlay(showOverlay = isLoading)
}

@WalletAllScreenPreview
@Composable
private fun NoInternetConnectionScreenPreview() {
    PilotWalletTheme {
        NoInternetConnectionScreenContent(
            isLoading = false,
            onRetry = {},
            onClose = {},
        )
    }
}
