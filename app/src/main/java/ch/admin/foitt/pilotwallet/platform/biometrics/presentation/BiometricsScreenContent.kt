package ch.admin.foitt.pilotwallet.platform.biometrics.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.BiometricsScreenImage
import ch.admin.foitt.pilotwallet.platform.onboarding.composables.OnboardingProgressIndicator
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletIcons
import ch.admin.foitt.pilotwallet.theme.WalletTexts

@Composable
internal fun BiometricsContent(
    @StringRes header: Int,
    @StringRes description: Int,
    @StringRes infoText: Int,
    onOpenSettings: (() -> Unit)? = null,
) {
    WalletTexts.TitleScreenMultiLine(text = stringResource(id = header))
    Spacer(modifier = Modifier.height(Sizes.s04))
    BiometricsScreenImage()
    Spacer(modifier = Modifier.height(Sizes.s04))
    WalletTexts.Body(text = stringResource(id = description))
    Spacer(modifier = Modifier.height(Sizes.s06))
    BiometricsInfoLabel(infoText = stringResource(id = infoText))

    if (onOpenSettings != null) {
        Spacer(modifier = Modifier.height(Sizes.s06))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Sizes.s04)
                .clickable { onOpenSettings() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WalletTexts.BodyLarge(text = stringResource(id = R.string.biometricSetup_disabled_enableButton))
            Icon(
                painter = painterResource(id = R.drawable.pilot_ic_link),
                contentDescription = null
            )
        }
        HorizontalDivider(modifier = Modifier.height(Sizes.line01))
    }
}

@Composable
internal fun OnboardingBiometricsContent(
    @StringRes header: Int,
    @StringRes description: Int,
    @StringRes infoText: Int,
) {
    BiometricsScreenImage()
    Spacer(modifier = Modifier.height(Sizes.s04))
    OnboardingProgressIndicator(currentStep = 4)
    Spacer(modifier = Modifier.height(Sizes.s04))
    WalletTexts.TitleScreenCentered(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = header)
    )
    Spacer(modifier = Modifier.height(Sizes.s04))
    WalletTexts.BodyCentered(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = description)
    )
    Spacer(modifier = Modifier.height(Sizes.s04))
    BiometricsInfoLabel(infoText = stringResource(id = infoText))
}

@Composable
private fun BiometricsInfoLabel(
    infoText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        WalletIcons.IconWithBackground(
            icon = painterResource(id = R.drawable.pilot_ic_lock)
        )
        Spacer(modifier = Modifier.width(Sizes.s04))
        WalletTexts.LabelSmall(text = infoText)
    }
}
