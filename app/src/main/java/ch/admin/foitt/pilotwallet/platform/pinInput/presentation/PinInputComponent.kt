package ch.admin.foitt.pilotwallet.platform.pinInput.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.platform.composables.LoadingOverlay
import ch.admin.foitt.pilotwallet.platform.composables.ScalableContentLayout
import ch.admin.foitt.pilotwallet.platform.onboarding.composables.OnboardingProgressIndicator
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinCheckResult
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinInputFieldState
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinInputResult
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.platform.utils.componentViewModel
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts

@Composable
fun OnboardingPinInputComponent(
    title: String? = null,
    description: String? = null,
    pinCheckResult: PinCheckResult = PinCheckResult.Reset,
    showSuccessAnimation: Boolean = false,
    onValidPin: (String) -> Unit,
    onPinInputResult: (PinInputResult) -> Unit,
) = PinInputComponent(
    onboarding = true,
    title = title,
    description = description,
    pinCheckResult = pinCheckResult,
    showSuccessAnimation = showSuccessAnimation,
    onValidPin = onValidPin,
    onPinInputResult = onPinInputResult,
)

@Composable
fun PinInputComponent(
    onboarding: Boolean = false,
    title: String? = null,
    description: String? = null,
    pinCheckResult: PinCheckResult = PinCheckResult.Reset,
    showSuccessAnimation: Boolean = false,
    onValidPin: (String) -> Unit,
    onPinInputResult: (PinInputResult) -> Unit,
) {
    val viewModel: PinInputComponentViewModel = componentViewModel()
    LaunchedEffect(viewModel) {
        viewModel.pinInputFieldState.collect { state ->
            if (state is PinInputFieldState.Valid) {
                onValidPin(state.pin)
            }
        }
    }
    LaunchedEffect(pinCheckResult) {
        viewModel.onPinCheckResult(pinCheckResult)
    }
    PinInputComponentContent(
        onboarding = onboarding,
        title = title,
        description = description,
        showSuccessAnimation = showSuccessAnimation,
        pin = viewModel.pin.collectAsStateWithLifecycle().value,
        pinLength = viewModel.pinLength,
        pinInputFieldState = viewModel.pinInputFieldState.collectAsStateWithLifecycle().value,
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        onPinChange = viewModel::updatePin,
        onAnimationFinished = { success ->
            // onAnimationFinished is triggered when coming back to a screen because the new pinInputFieldState is not
            // passed to the composable in time, ignore it
            if (pinCheckResult is PinCheckResult.Reset) return@PinInputComponentContent
            val result = if (success) {
                PinInputResult.Success(viewModel.pin.value)
            } else {
                PinInputResult.Error
            }
            onPinInputResult(result)
        },
    )
}

@Composable
private fun PinInputComponentContent(
    onboarding: Boolean = false,
    title: String? = null,
    description: String? = null,
    showSuccessAnimation: Boolean = false,
    pin: String,
    pinLength: Int,
    pinInputFieldState: PinInputFieldState,
    isLoading: Boolean? = null,
    onPinChange: (String) -> Unit,
    onAnimationFinished: (Boolean) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding(),
    ) {
        Box( // use Box here for scrolling as it would set the maxHeight to infinity
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Sizes.s04)
        ) {
            ScalableContentLayout(
                height = this@BoxWithConstraints.maxHeight - Sizes.s04.times(2), // Subtract scrolling padding
                scalableContentIndex = if (title == null || onboarding) 0 else 2,
                content = {
                    if (onboarding) {
                        PinInputOnboardingContent(
                            title = title,
                            description = description,
                            pin = pin,
                            pinLength = pinLength,
                            pinInputFieldState = pinInputFieldState,
                            showSuccessAnimation = showSuccessAnimation,
                            onPinChange = onPinChange,
                            onAnimationFinished = onAnimationFinished
                        )
                    } else {
                        PinInputContent(
                            title = title,
                            description = description,
                            pin = pin,
                            pinLength = pinLength,
                            pinInputFieldState = pinInputFieldState,
                            showSuccessAnimation = showSuccessAnimation,
                            onPinChange = onPinChange,
                            onAnimationFinished = onAnimationFinished
                        )
                    }
                }
            )
        }
    }
    isLoading?.let {
        LoadingOverlay(showOverlay = isLoading)
    }
}

@Composable
private fun PinInputContent(
    title: String?,
    description: String?,
    pin: String,
    pinLength: Int,
    pinInputFieldState: PinInputFieldState,
    showSuccessAnimation: Boolean,
    onPinChange: (String) -> Unit,
    onAnimationFinished: (Boolean) -> Unit,
) {
    title?.let {
        WalletTexts.TitleScreenMultiLine(text = title)
        Spacer(modifier = Modifier.height(Sizes.s04))
    }
    PinInputField(
        modifier = Modifier.fillMaxWidth(),
        pin = pin,
        pinLength = pinLength,
        pinInputFieldState = pinInputFieldState,
        showSuccessAnimation = showSuccessAnimation,
        onPinChange = onPinChange,
        onAnimationFinished = onAnimationFinished,
    )
    description?.let {
        Spacer(modifier = Modifier.height(Sizes.s04))
        WalletTexts.Body(
            text = description
        )
    }
}

@Composable
private fun PinInputOnboardingContent(
    onboardingStep: Int = 4,
    title: String?,
    description: String?,
    pin: String,
    pinLength: Int,
    pinInputFieldState: PinInputFieldState,
    showSuccessAnimation: Boolean,
    onPinChange: (String) -> Unit,
    onAnimationFinished: (Boolean) -> Unit,
) {
    PinInputField(
        modifier = Modifier.fillMaxWidth(),
        pin = pin,
        pinLength = pinLength,
        pinInputFieldState = pinInputFieldState,
        showSuccessAnimation = showSuccessAnimation,
        onPinChange = onPinChange,
        onAnimationFinished = onAnimationFinished,
    )
    Spacer(modifier = Modifier.height(Sizes.s04))
    OnboardingProgressIndicator(currentStep = onboardingStep)
    title?.let {
        Spacer(modifier = Modifier.height(Sizes.s04))
        WalletTexts.TitleScreenCentered(
            modifier = Modifier.fillMaxWidth(),
            text = title,
        )
    }
    description?.let {
        Spacer(modifier = Modifier.height(Sizes.s04))
        WalletTexts.BodyCentered(
            modifier = Modifier.fillMaxWidth(),
            text = description,
        )
    }
}

@WalletAllScreenPreview
@Composable
private fun PinInputScreenContentPreview() {
    PilotWalletTheme {
        PinInputComponentContent(
            onboarding = true,
            title = "Code bestätigen",
            description = "Code zur bestätigung wiederholen",
            pin = "123",

            pinLength = 6,
            pinInputFieldState = PinInputFieldState.Typing,
            onPinChange = {},
            onAnimationFinished = {},
        )
    }
}
