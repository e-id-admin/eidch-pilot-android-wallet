package ch.admin.foitt.pilotwallet.platform.pinInput.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinInputFieldState
import ch.admin.foitt.pilotwallet.theme.Gradients
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import timber.log.Timber
import kotlin.math.roundToInt
import kotlin.math.sin

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PinInputField(
    modifier: Modifier = Modifier,
    pinInputFieldState: PinInputFieldState = PinInputFieldState.Typing,
    pin: String,
    pinLength: Int,
    showSuccessAnimation: Boolean = false,
    onPinChange: (String) -> Unit,
    onAnimationFinished: (Boolean) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val windowInfo = LocalWindowInfo.current
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(windowInfo) {
        snapshotFlow { windowInfo.isWindowFocused }.collect { isWindowFocused ->
            if (isWindowFocused) {
                focusRequester.requestFocus()
                keyboard?.show() ?: Timber.w("PinInputField: keyboard not controllable")
            }
        }
    }

    val textFieldValue = remember(pin) {
        mutableStateOf(
            TextFieldValue(
                text = pin,
                selection = TextRange(pin.length)
            )
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Sizes.s01))
            .background(
                if (pinInputFieldState == PinInputFieldState.Error) {
                    Gradients.pinInputErrorBrush()
                } else {
                    Gradients.pinInputBrush()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        SuccessAnimation(
            showSuccessAnimation = showSuccessAnimation,
            pinInputFieldState = pinInputFieldState,
            onAnimationFinished = { onAnimationFinished(true) }
        )
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Sizes.s08)
                .focusRequester(focusRequester),
            value = textFieldValue.value,
            onValueChange = { newPin ->
                if (newPin.text.length <= pinLength) {
                    onPinChange(newPin.text)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
            ),
            decorationBox = {
                val errorAnimatable = createErrorAnimatable(
                    pinInputFieldState = pinInputFieldState,
                    onAnimationFinished = { onAnimationFinished(false) }
                )
                Row(horizontalArrangement = Arrangement.Center) {
                    repeat(pinLength) { index ->
                        val pinIsSet = (index < pin.length)
                        PinPlaceholder(
                            modifier = Modifier.offset {
                                createShakingOffset(amplitude = 10.dp.roundToPx(), errorAnimatable = errorAnimatable)
                            },
                            showError = pinInputFieldState is PinInputFieldState.Error,
                            isSet = pinIsSet
                        )
                        if (index < pinLength - 1) {
                            Spacer(modifier = Modifier.width(Sizes.s05))
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun SuccessAnimation(
    showSuccessAnimation: Boolean,
    pinInputFieldState: PinInputFieldState,
    onAnimationFinished: () -> Unit
) {
    val successAlpha = remember { Animatable(0f) }
    LaunchedEffect(pinInputFieldState) {
        if (pinInputFieldState is PinInputFieldState.Success) {
            if (showSuccessAnimation) {
                successAlpha.snapTo(0f)
                successAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(500),
                )
            }
            onAnimationFinished()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(successAlpha.value)
            .background(Gradients.pinInputSuccessBrush())
    )
}

@Composable
private fun createErrorAnimatable(
    pinInputFieldState: PinInputFieldState,
    onAnimationFinished: () -> Unit
): Animatable<Float, AnimationVector1D> {
    val animation = remember { Animatable(0f) }
    LaunchedEffect(pinInputFieldState) {
        if (pinInputFieldState is PinInputFieldState.Error) {
            animation.snapTo(0f)
            animation.animateTo(
                targetValue = 1f,
                animationSpec = tween(1000),
            )
            onAnimationFinished()
        }
    }
    return animation
}

private fun createShakingOffset(amplitude: Int, errorAnimatable: Animatable<Float, AnimationVector1D>) =
    IntOffset(
        x = (amplitude * sin(errorAnimatable.value * Math.PI * 3f).toFloat()).roundToInt(),
        y = 0
    )

private class PinInputFieldStateProvider : PreviewParameterProvider<PinInputFieldState> {
    override val values: Sequence<PinInputFieldState> = sequenceOf(
        PinInputFieldState.Typing,
        PinInputFieldState.Error,
        PinInputFieldState.Success,
    )
}

@Preview
@Composable
private fun PinInputFieldPreview(@PreviewParameter(PinInputFieldStateProvider::class) state: PinInputFieldState) {
    PilotWalletTheme {
        PinInputField(
            pin = "123",
            pinLength = 6,
            pinInputFieldState = state,
            onPinChange = { },
            onAnimationFinished = {},
        )
    }
}
