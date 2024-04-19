package ch.admin.foitt.pilotwallet.feature.settings.presentation.biometrics

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.AuthWithPinNavArg
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinCheckResult
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinInputResult
import ch.admin.foitt.pilotwallet.platform.pinInput.presentation.PinInputComponent
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    navArgsDelegate = AuthWithPinNavArg::class,
)
@Composable
fun AuthWithPinScreen(
    viewModel: AuthWithPinViewModel
) {
    AuthWithPinScreenContent(
        enableBiometrics = viewModel.enableBiometrics,
        pinCheckResult = viewModel.pinCheckResult.collectAsStateWithLifecycle().value,
        onValidPin = viewModel::onValidPin,
        onPinInputResult = viewModel::onPinInputResult,
    )
}

@Composable
private fun AuthWithPinScreenContent(
    enableBiometrics: Boolean,
    pinCheckResult: PinCheckResult,
    onValidPin: (String) -> Unit,
    onPinInputResult: (PinInputResult) -> Unit,
) {
    val description = if (enableBiometrics) {
        R.string.change_biometrics_pin_activation_content_text
    } else {
        R.string.change_biometrics_pin_deactivation_content_text
    }
    PinInputComponent(
        title = stringResource(id = R.string.change_biometrics_header_text),
        description = stringResource(id = description),
        pinCheckResult = pinCheckResult,
        onValidPin = onValidPin,
        onPinInputResult = onPinInputResult,
    )
}
