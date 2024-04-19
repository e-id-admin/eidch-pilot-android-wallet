package ch.admin.foitt.pilotwallet.feature.onboarding.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.feature.onboarding.navigation.OnboardingNavGraph
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinCheckResult
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinInputResult
import ch.admin.foitt.pilotwallet.platform.pinInput.presentation.OnboardingPinInputComponent
import ch.admin.foitt.pilotwallet.theme.Sizes
import com.ramcosta.composedestinations.annotation.Destination

@OnboardingNavGraph
@Destination
@Composable
fun EnterPinScreen(
    viewModel: EnterPinViewModel,
) {
    EnterPinScreenContent(
        pinCheckResult = viewModel.pinCheckResult.collectAsStateWithLifecycle().value,
        onValidPin = { viewModel.onValidPin() },
        onPinInputResult = viewModel::onPinInputResult,
    )
}

@Composable
private fun EnterPinScreenContent(
    pinCheckResult: PinCheckResult,
    onValidPin: (String) -> Unit,
    onPinInputResult: (PinInputResult) -> Unit,
) {
    Box(modifier = Modifier.padding(top = Sizes.s12)) { // add top bar spacing
        OnboardingPinInputComponent(
            title = stringResource(id = R.string.pinSetup_title),
            description = stringResource(id = R.string.pinSetup_content),
            pinCheckResult = pinCheckResult,
            onValidPin = onValidPin,
            onPinInputResult = onPinInputResult,
        )
    }
}
