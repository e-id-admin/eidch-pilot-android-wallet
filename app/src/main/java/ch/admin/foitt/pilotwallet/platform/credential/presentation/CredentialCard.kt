package ch.admin.foitt.pilotwallet.platform.credential.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import ch.admin.foitt.pilotwallet.platform.credential.presentation.mock.CredentialMocks
import ch.admin.foitt.pilotwallet.platform.credential.presentation.model.CredentialCardState
import ch.admin.foitt.pilotwallet.platform.preview.ComposableWrapper
import ch.admin.foitt.pilotwallet.platform.preview.WalletComponentPreview
import ch.admin.foitt.pilotwallet.theme.Gradients
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes

@Composable
fun CredentialCard(
    credentialCardState: CredentialCardState,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) = Surface(
    modifier = modifier
        .aspectRatio(
            ratio = 1.59f,
            matchHeightConstraintsFirst = false
        )
        .fillMaxWidth()
        .height(Sizes.credentialCardHeight),
    shape = RoundedCornerShape(size = Sizes.s02),
    color = credentialCardState.backgroundColor,
    contentColor = Color.Unspecified,
    border = BorderStroke(
        width = Sizes.line01,
        color = credentialCardState.borderColor,
    ),
    shadowElevation = Sizes.s01,
) {
    Column(
        Modifier
            .run {
                onClick?.let {
                    this.clickable(onClick = onClick)
                } ?: this
            }
            .background(brush = Gradients.credentialBrush())
            .fillMaxSize()
            .padding(Sizes.s04)
    ) {
        credentialCardState.logo?.let { logo ->
            Icon(
                painter = logo,
                contentDescription = null,
                modifier = Modifier
                    .size(Sizes.credentialIconSize),
                tint = Color.Unspecified,
            )
        }
        Spacer(modifier = Modifier.weight(1.0f))
        CredentialCardContent(credentialCardState = credentialCardState)
    }
}

private class CredentialCardPreviewParams : PreviewParameterProvider<ComposableWrapper<CredentialCardState>> {
    override val values = CredentialMocks.cardStates
}

@Composable
@WalletComponentPreview
private fun CredentialCardPreview(
    @PreviewParameter(CredentialCardPreviewParams::class) previewParam: ComposableWrapper<CredentialCardState>,
) {
    PilotWalletTheme {
        CredentialCard(
            credentialCardState = previewParam.value(),
            onClick = {},
        )
    }
}
