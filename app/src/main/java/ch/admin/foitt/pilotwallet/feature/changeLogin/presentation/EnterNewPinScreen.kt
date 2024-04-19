package ch.admin.foitt.pilotwallet.feature.changeLogin.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinCheckResult
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinInputResult
import ch.admin.foitt.pilotwallet.platform.pinInput.presentation.PinInputComponent
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun EnterNewPinScreen(viewModel: EnterNewPinViewModel) {
    EnterNewPinScreenContent(
        pinCheckResult = viewModel.pinCheckResult.collectAsStateWithLifecycle().value,
        onValidPin = { viewModel.onValidPin() },
        onPinInputResult = viewModel::onPinInputResult,
    )
}

@Composable
private fun EnterNewPinScreenContent(
    pinCheckResult: PinCheckResult,
    onValidPin: (String) -> Unit,
    onPinInputResult: (PinInputResult) -> Unit,
) {
    PinInputComponent(
        title = stringResource(id = R.string.pin_change_new_pin_title),
        pinCheckResult = pinCheckResult,
        onValidPin = onValidPin,
        onPinInputResult = onPinInputResult,
    )
}
