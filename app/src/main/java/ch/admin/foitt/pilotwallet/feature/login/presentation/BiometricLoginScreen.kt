package ch.admin.foitt.pilotwallet.feature.login.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.BiometricsScreenImage
import ch.admin.foitt.pilotwallet.platform.composables.ButtonOutlined
import ch.admin.foitt.pilotwallet.platform.composables.ButtonPrimary
import ch.admin.foitt.pilotwallet.platform.composables.LoadingOverlay
import ch.admin.foitt.pilotwallet.platform.composables.ScrollableWithStickyBottom
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.platform.utils.LocalActivity
import ch.admin.foitt.pilotwallet.platform.utils.OnResumeEventHandler
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    style = LoginNavAnimation::class,
)
@Composable
fun BiometricLoginScreen(
    viewModel: BiometricLoginViewModel
) {
    val currentActivity = LocalActivity.current

    OnResumeEventHandler {
        viewModel.tryLoginWithBiometric(currentActivity)
    }

    BackHandler {
        currentActivity.finish()
    }

    BiometricLoginScreenContent(
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        onLoginWithPin = viewModel::navigateToLoginWithPin,
        onRetry = { viewModel.tryLoginWithBiometric(currentActivity) },
    )
}

@Composable
fun BiometricLoginScreenContent(
    isLoading: Boolean,
    onLoginWithPin: () -> Unit,
    onRetry: () -> Unit,
) {
    ScrollableWithStickyBottom(
        useStatusBarInsets = false,
        useNavigationBarInsets = false,
        stickyBottomContent = {
            ButtonOutlined(
                text = stringResource(id = R.string.login_biometrics_retryButton),
                leftIcon = painterResource(id = R.drawable.pilot_ic_retry),
                onClick = onRetry
            )
            Spacer(Modifier.height(Sizes.s03))
            ButtonPrimary(
                text = stringResource(id = R.string.login_biometrics_toPinButton),
                rightIcon = painterResource(id = R.drawable.pilot_ic_next_button),
                onClick = { onLoginWithPin() }
            )
        },
        contentPadding = PaddingValues(
            top = Sizes.s05,
            start = Sizes.s04,
            end = Sizes.s04,
        ),
        scrollableContent = {
            BiometricsScreenImage(
                showSubtitle = true,
            )
        }
    )
    LoadingOverlay(showOverlay = isLoading)
}

@WalletAllScreenPreview
@Composable
fun BiometricLoginScreenPreview() {
    PilotWalletTheme {
        BiometricLoginScreenContent(
            isLoading = false,
            onRetry = {},
            onLoginWithPin = {},
        )
    }
}
