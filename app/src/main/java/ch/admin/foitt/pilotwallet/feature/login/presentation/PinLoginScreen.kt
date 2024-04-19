package ch.admin.foitt.pilotwallet.feature.login.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.LoadingOverlay
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinCheckResult
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinInputResult
import ch.admin.foitt.pilotwallet.platform.pinInput.presentation.PinInputComponent
import ch.admin.foitt.pilotwallet.platform.utils.LocalActivity
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    style = LoginNavAnimation::class,
)
@Composable
fun PinLoginScreen(
    viewModel: PinLoginViewModel,
) {
    val currentActivity = LocalActivity.current

    val countdown = viewModel.countdown.collectAsStateWithLifecycle().value

    if (countdown.isNotBlank()) {
        BlockedLoginDialog(
            countdown = countdown
        )
    }

    BackHandler {
        currentActivity.finish()
    }

    PinLoginScreenContent(
        pinCheckResult = viewModel.pinCheckResult.collectAsStateWithLifecycle().value,
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        onValidPin = viewModel::onValidPin,
        onPinInputResult = viewModel::onPinInputResult,
    )
}

@Composable
private fun PinLoginScreenContent(
    pinCheckResult: PinCheckResult,
    isLoading: Boolean,
    onValidPin: (String) -> Unit,
    onPinInputResult: (PinInputResult) -> Unit,
) {
    PinInputComponent(
        pinCheckResult = pinCheckResult,
        onValidPin = onValidPin,
        onPinInputResult = onPinInputResult,
    )
    LoadingOverlay(showOverlay = isLoading)
}

@Composable
private fun BlockedLoginDialog(
    countdown: String
) {
    AlertDialog(
        title = { Text(text = stringResource(R.string.login_blocked_title)) },
        text = { Text(text = stringResource(R.string.login_blocked_description, countdown)) },
        onDismissRequest = {},
        confirmButton = {},
    )
}
