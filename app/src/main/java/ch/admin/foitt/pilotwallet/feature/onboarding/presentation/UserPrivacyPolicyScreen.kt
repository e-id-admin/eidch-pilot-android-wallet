package ch.admin.foitt.pilotwallet.feature.onboarding.presentation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.feature.onboarding.navigation.OnboardingNavGraph
import ch.admin.foitt.pilotwallet.feature.onboarding.presentation.composables.OnboardingScreenContent
import ch.admin.foitt.pilotwallet.platform.composables.ButtonOutlined
import ch.admin.foitt.pilotwallet.platform.composables.ButtonPrimary
import ch.admin.foitt.pilotwallet.platform.composables.ScrollableWithStickyBottom
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletIcons.IconWithBackground
import ch.admin.foitt.pilotwallet.theme.WalletTexts
import com.ramcosta.composedestinations.annotation.Destination

@OnboardingNavGraph
@Destination
@Composable
fun UserPrivacyPolicyScreen(viewModel: UserPrivacyPolicyViewModel) {
    UserPrivacyPolicyScreenContent(
        acceptTracking = viewModel::acceptTracking,
        declineTracking = viewModel::declineTracking,
        onOpenUserPrivacyPolicyLink = viewModel::onOpenUserPrivacyPolicy,
    )
}

@Composable
private fun UserPrivacyPolicyScreenContent(
    acceptTracking: () -> Unit,
    declineTracking: () -> Unit,
    onOpenUserPrivacyPolicyLink: () -> Unit,
) {
    ScrollableWithStickyBottom(
        scrollableContent = {
            ScrollableContent(
                onOpenUserPrivacyPolicyLink = onOpenUserPrivacyPolicyLink
            )
        },
        stickyBottomContent = {
            ButtonOutlined(
                text = stringResource(id = R.string.onboarding_privacy_declineLoggingButton),
                leftIcon = painterResource(id = R.drawable.pilot_ic_cross),
                onClick = declineTracking
            )
            Spacer(modifier = Modifier.height(Sizes.s04))
            ButtonPrimary(
                text = stringResource(id = R.string.onboarding_privacy_acceptLoggingButton),
                leftIcon = painterResource(id = R.drawable.pilot_ic_checkmark_button),
                onClick = acceptTracking
            )
        }
    )
}

@Composable
private fun ScrollableContent(
    onOpenUserPrivacyPolicyLink: () -> Unit
) {
    OnboardingScreenContent(
        title = stringResource(id = R.string.onboarding_privacy_primary),
        image = painterResource(id = R.drawable.pilot_ic_privacy_options),
        subtitle = stringResource(id = R.string.onboarding_privacy_secondary),
        currentStep = 3,
    )
    Spacer(modifier = Modifier.height(Sizes.s06))
    WalletTexts.TextLink(
        text = stringResource(id = R.string.onboarding_privacy_link_text),
        onClick = onOpenUserPrivacyPolicyLink,
        rightIcon = painterResource(id = R.drawable.pilot_ic_link),
    )
    Spacer(modifier = Modifier.height(Sizes.s06))
    UserPrivacyPolicy()
}

@Composable
private fun UserPrivacyPolicy() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconWithBackground(icon = painterResource(id = R.drawable.pilot_ic_analysis))
        Spacer(modifier = Modifier.width(Sizes.s04))
        val boldText = stringResource(id = R.string.onboarding_privacy_toggle_text)
        val description = stringResource(id = R.string.onboarding_privacy_toggle_text, boldText)
        val start = description.indexOf(boldText)
        val spanStyles = listOf(
            AnnotatedString.Range(
                SpanStyle(fontWeight = FontWeight.Bold),
                start = start,
                end = start + boldText.length,
            )
        )
        WalletTexts.LabelMedium(
            modifier = Modifier.weight(1f),
            text = AnnotatedString(text = description, spanStyles = spanStyles),
        )
    }
}

@WalletAllScreenPreview
@Composable
private fun UserPrivacyPolicyScreenContentPreview() {
    PilotWalletTheme {
        UserPrivacyPolicyScreenContent(
            acceptTracking = {},
            declineTracking = {},
            onOpenUserPrivacyPolicyLink = {},
        )
    }
}
