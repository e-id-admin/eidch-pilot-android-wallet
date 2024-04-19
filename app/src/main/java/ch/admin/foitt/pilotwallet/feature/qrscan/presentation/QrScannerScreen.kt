package ch.admin.foitt.pilotwallet.feature.qrscan.presentation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.feature.qrscan.domain.model.FlashLightState
import ch.admin.foitt.pilotwallet.platform.composables.ButtonSecondaryWrapContent
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.FadingVisibility
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletCards
import ch.admin.foitt.pilotwallet.theme.WalletTexts
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun QrScannerScreen(
    viewModel: QrScannerViewModel,
) {
    QrScannerScreenContent(
        flashLightState = viewModel.flashLightState.collectAsStateWithLifecycle().value,
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        showError = viewModel.showError.collectAsStateWithLifecycle().value,
        onInitScan = viewModel::onInitScan,
        onFlashLight = viewModel::onFlashLight,
    )
}

@Composable
private fun QrScannerScreenContent(
    flashLightState: FlashLightState,
    showError: Boolean,
    isLoading: Boolean,
    onInitScan: (PreviewView) -> Unit,
    onFlashLight: () -> Unit,
) {
    Box {
        val previewView = remember { mutableStateOf<PreviewView?>(null) }
        AndroidView(
            factory = { androidViewContext -> PreviewView(androidViewContext) },
            modifier = Modifier.fillMaxSize(),
            update = { view -> previewView.value = view },
        )
        LaunchedEffect(previewView.value) {
            previewView.value?.let {
                // only init scan once per previewView when previewView is available and attached to lifecycle
                onInitScan(it)
            }
        }
        FadingVisibility(showError) {
            ScanError(
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
        ScannerOverlay(
            flashLightState = flashLightState,
            showScanBox = !isLoading,
            onFlashLight = onFlashLight,
        )
        FadingVisibility(isLoading) {
            LoadingIndicator()
        }
    }
}

@Composable
private fun ScanError(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = Sizes.s04, top = Sizes.s04, end = Sizes.s04, bottom = Sizes.s04)
            .background(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(Sizes.s01))
            .padding(start = Sizes.s04, top = Sizes.s02, end = Sizes.s04, bottom = Sizes.s02),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.pilot_ic_error),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
        )
        Spacer(modifier = Modifier.width(Sizes.s02))
        WalletTexts.ErrorLabel(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.qrScanner_error_message)
        )
    }
}

@Composable
private fun ScannerOverlay(
    flashLightState: FlashLightState,
    showScanBox: Boolean,
    onFlashLight: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(2f))

        FadingVisibility(showScanBox) {
            ScanBox()
        }

        Column(
            modifier = Modifier.weight(3f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            var isHintShown by rememberSaveable { mutableStateOf(true) }
            val modifier = if (isHintShown) {
                Modifier
            } else {
                Modifier
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                    .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
            }
            FlashLightButton(
                modifier = modifier,
                flashLightState = flashLightState,
                onFlashLight = onFlashLight
            )
            ScannerHint(
                isHintShown = isHintShown,
                onHintDismiss = { isHintShown = false }
            )
        }
    }
}

@Composable
private fun ScanBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Sizes.s08, end = Sizes.s08),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.pilot_qrscanner_overlay),
            contentDescription = null,
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun FlashLightButton(
    modifier: Modifier = Modifier,
    flashLightState: FlashLightState,
    onFlashLight: () -> Unit
) {
    if (flashLightState != FlashLightState.UNSUPPORTED) {
        ButtonSecondaryWrapContent(
            modifier = modifier
                .animateContentSize()
                .padding(bottom = Sizes.s08),
            text = stringResource(id = getFlashLightText(flashLightState)),
            leftIcon = painterResource(id = getFlashLightIcon(flashLightState)),
            onClick = onFlashLight,
        )
    }
}

@StringRes
private fun getFlashLightText(flashLightState: FlashLightState) = when (flashLightState) {
    FlashLightState.ON -> R.string.qrScanner_flash_light_button_off
    FlashLightState.OFF -> R.string.qrScanner_flash_light_button_on
    FlashLightState.UNSUPPORTED -> error("Unsupported flash light state")
}

@DrawableRes
private fun getFlashLightIcon(flashLightState: FlashLightState) = when (flashLightState) {
    FlashLightState.ON -> R.drawable.pilot_ic_flashlight_off
    FlashLightState.OFF -> R.drawable.pilot_ic_flashlight_on
    FlashLightState.UNSUPPORTED -> error("Unsupported flash light state")
}

@Composable
private fun ScannerHint(isHintShown: Boolean, onHintDismiss: () -> Unit) {
    AnimatedVisibility(isHintShown) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                .padding(start = Sizes.s06, top = Sizes.s04, end = Sizes.s02, bottom = Sizes.s04),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WalletTexts.BodySmall(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.qrScanner_hint)
            )
            IconButton(onClick = onHintDismiss) {
                Icon(
                    painter = painterResource(id = R.drawable.pilot_ic_close_filled),
                    contentDescription = stringResource(id = R.string.qrScanner_hint_close_button),
                )
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim)
            .zIndex(1.0f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {},
        contentAlignment = Alignment.TopCenter,
    ) {
        WalletCards.InfoCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Sizes.s08, vertical = Sizes.s04),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = Sizes.s04, vertical = Sizes.s02),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Sizes.s04),
                    strokeWidth = 2.dp,
                )
                Spacer(modifier = Modifier.width(Sizes.s02))
                WalletTexts.InfoLabel(
                    text = stringResource(id = R.string.qrScanner_loading_message)
                )
            }
        }
    }
}

private class QrScannerPreviewParamsProvider : PreviewParameterProvider<FlashLightState> {
    override val values = sequenceOf(FlashLightState.ON, FlashLightState.OFF, FlashLightState.UNSUPPORTED)
}

@WalletAllScreenPreview
@Composable
private fun QrScannerPreview(
    @PreviewParameter(QrScannerPreviewParamsProvider::class) flashLightState: FlashLightState,
) {
    PilotWalletTheme {
        QrScannerScreenContent(
            flashLightState = flashLightState,
            onInitScan = {},
            onFlashLight = {},
            showError = false,
            isLoading = false,
        )
    }
}

@WalletAllScreenPreview
@Composable
private fun QrScannerLoadingPreview() {
    PilotWalletTheme {
        QrScannerScreenContent(
            flashLightState = FlashLightState.ON,
            onInitScan = {},
            onFlashLight = {},
            showError = false,
            isLoading = true,
        )
    }
}

@WalletAllScreenPreview
@Composable
private fun QrScannerErrorPreview() {
    PilotWalletTheme {
        QrScannerScreenContent(
            flashLightState = FlashLightState.ON,
            onInitScan = {},
            onFlashLight = {},
            showError = true,
            isLoading = false,
        )
    }
}
