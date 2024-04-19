@file:Suppress("TooManyFunctions")

package ch.admin.foitt.pilotwallet.platform.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.preview.WalletComponentPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletButtonColors
import ch.admin.foitt.pilotwallet.theme.WalletTexts

@Composable
fun ButtonPrimary(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leftIcon: Painter? = null,
    rightIcon: Painter? = null,
    enabled: Boolean = true,
) = Button(
    onClick = onClick,
    modifier = modifier
        .fillMaxWidth()
        .heightIn(min = Sizes.s12),
    shape = MaterialTheme.shapes.extraSmall,
    colors = WalletButtonColors.primary(),
    enabled = enabled,
) {
    ButtonContent(
        text = text,
        enabled = enabled,
        leftIcon = leftIcon,
        rightIcon = rightIcon,
    )
}

@Composable
fun ButtonSecondary(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leftIcon: Painter? = null,
    rightIcon: Painter? = null,
    enabled: Boolean = true,
) = Button(
    onClick = onClick,
    modifier = modifier
        .fillMaxWidth()
        .heightIn(min = Sizes.s12),
    shape = MaterialTheme.shapes.extraSmall,
    colors = WalletButtonColors.secondary(),
    enabled = enabled,
) {
    ButtonContent(
        text = text,
        enabled = enabled,
        leftIcon = leftIcon,
        rightIcon = rightIcon,
    )
}

@Composable
fun ButtonSecondaryWrapContent(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leftIcon: Painter? = null,
    rightIcon: Painter? = null,
    enabled: Boolean = true,
) = Button(
    onClick = onClick,
    modifier = modifier
        .heightIn(min = Sizes.s12),
    shape = MaterialTheme.shapes.extraSmall,
    colors = WalletButtonColors.secondary(),
    enabled = enabled,
) {
    ButtonContent(
        text = text,
        enabled = enabled,
        leftIcon = leftIcon,
        rightIcon = rightIcon,
    )
}

@Composable
fun ButtonSecondaryWrapContentRoundedCorners(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leftIcon: Painter? = null,
    rightIcon: Painter? = null,
    enabled: Boolean = true,
) = Button(
    onClick = onClick,
    modifier = modifier
        .heightIn(min = Sizes.s12),
    shape = RoundedCornerShape(Sizes.s10),
    colors = WalletButtonColors.secondary(),
    enabled = enabled,
) {
    ButtonContent(
        text = text,
        enabled = enabled,
        leftIcon = leftIcon,
        rightIcon = rightIcon,
    )
}

@Composable
fun ButtonTertiary(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leftIcon: Painter? = null,
    rightIcon: Painter? = null,
    enabled: Boolean = true,
    isActive: Boolean = false,
    loadingText: String? = null
) = Button(
    onClick = onClick,
    modifier = modifier
        .fillMaxWidth()
        .heightIn(min = Sizes.s12),
    shape = MaterialTheme.shapes.extraSmall,
    colors = WalletButtonColors.tertiary(isActive = isActive),
    enabled = enabled,
) {
    ButtonContent(
        text = text,
        enabled = enabled,
        leftIcon = leftIcon,
        rightIcon = rightIcon,
        isActive = isActive,
        loadingText = loadingText,
    )
}

@Composable
fun ButtonWarning(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leftIcon: Painter? = null,
    rightIcon: Painter? = null,
    enabled: Boolean = true,
) = Button(
    onClick = onClick,
    modifier = modifier
        .fillMaxWidth()
        .heightIn(min = Sizes.s12),
    shape = MaterialTheme.shapes.extraSmall,
    colors = WalletButtonColors.warning(),
    enabled = enabled,
) {
    ButtonContent(
        text = text,
        enabled = enabled,
        leftIcon = leftIcon,
        rightIcon = rightIcon,
    )
}

@Composable
fun ButtonOutlined(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leftIcon: Painter? = null,
    rightIcon: Painter? = null,
    enabled: Boolean = true,
) = OutlinedButton(
    onClick = onClick,
    modifier = modifier
        .fillMaxWidth()
        .heightIn(min = Sizes.s12),
    shape = MaterialTheme.shapes.extraSmall,
    colors = WalletButtonColors.outlined(),
    enabled = enabled,
    border = BorderStroke(width = Sizes.line02, color = MaterialTheme.colorScheme.outline)
) {
    ButtonContent(
        text = text,
        enabled = enabled,
        leftIcon = leftIcon,
        rightIcon = rightIcon,
    )
}

@Composable
fun AcceptOrDeclineBottomButtons(
    acceptButtonText: String,
    declineButtonText: String,
    isAcceptEnabled: Boolean,
    acceptButtonIcon: Painter? = null,
    declineButtonIcon: Painter? = null,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) {
    ButtonOutlined(
        text = declineButtonText,
        leftIcon = declineButtonIcon,
        onClick = onDecline,
    )
    Spacer(modifier = Modifier.height(Sizes.s04))
    ButtonTertiary(
        text = acceptButtonText,
        onClick = onAccept,
        enabled = isAcceptEnabled,
        leftIcon = acceptButtonIcon,
    )
}

@Composable
fun OnboardingTopBarButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier.clickable { onClick() }
    ) {
        WalletTexts.BodyLarge(
            modifier = Modifier.padding(Sizes.s02),
            text = text
        )
    }
}

@Composable
private fun ButtonContent(
    text: String,
    leftIcon: Painter?,
    rightIcon: Painter?,
    enabled: Boolean,
    loadingText: String? = null,
    isActive: Boolean = false,
) {
    leftIcon?.let { icon ->
        if (isActive) {
            CircularProgressIndicator(
                color = LocalContentColor.current,
                strokeWidth = Sizes.line02,
                modifier = Modifier
                    .width(Sizes.s04)
                    .height(Sizes.s04)
                    .focusable(false)
            )
        } else {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .width(Sizes.s06)
                    .height(Sizes.s06)
                    .focusable(false)
            )
        }

        Spacer(modifier = Modifier.width(Sizes.s02))
    }
    WalletTexts.Button(
        text = if (isActive && loadingText != null) loadingText else text,
        enabled = enabled || isActive,
    )
    rightIcon?.let { icon ->
        Spacer(modifier = Modifier.width(Sizes.s02))
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier
                .width(Sizes.s06)
                .height(Sizes.s06)
                .focusable(false)
        )
    }
}

@WalletComponentPreview
@Composable
private fun BottomButtonPreview() {
    PilotWalletTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(Sizes.s03)
        ) {
            ButtonPrimary(text = "Click Me Primary", onClick = {})
            ButtonSecondary(text = "Click Me Secondary", onClick = {})
            ButtonSecondaryWrapContent(text = "Click Me Secondary", onClick = {})
            ButtonSecondaryWrapContentRoundedCorners(text = "Click Me Secondary", onClick = {})
            ButtonTertiary(text = "Click Me Tertiary", onClick = {})
            ButtonTertiary(
                text = "Click Me with icon",
                onClick = {},
                leftIcon = painterResource(id = R.drawable.pilot_ic_qrscanner)
            )
            ButtonTertiary(
                text = "Click Me with icon",
                onClick = {},
                rightIcon = painterResource(id = R.drawable.pilot_ic_link)
            )
            ButtonTertiary(
                text = "Click Me with icon and long text: Lorem ipsum dolor sit amet, consectetur adipiscing elit",
                onClick = {},
                leftIcon = painterResource(id = R.drawable.pilot_ic_qrscanner)
            )
            ButtonOutlined(text = "Click Me Outlined", onClick = {})
            ButtonTertiary(text = "Click Me Disabled", onClick = {}, enabled = false)
            ButtonTertiary(
                text = "Loading Primary",
                onClick = {},
                isActive = true,
                leftIcon = painterResource(id = R.drawable.pilot_ic_qrscanner),
                loadingText = "This button is loading..."
            )
        }
    }
}
