package ch.admin.foitt.pilotwallet.platform.credential.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.pilotwallet.platform.preview.WalletComponentPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.labelBackground

@Composable
internal fun CredentialStatusBadge(
    status: CredentialStatus?,
    modifier: Modifier = Modifier,
) = AnimatedContent(
    modifier = modifier,
    targetState = status,
    transitionSpec = {
        fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
    },
    label = "fadingAnimation",
) { credentialStatus ->
    Box(
        modifier = Modifier
            .background(
                color = when (credentialStatus) {
                    null -> Color.Transparent
                    CredentialStatus.VALID,
                    CredentialStatus.UNKNOWN -> MaterialTheme.colorScheme.labelBackground
                    CredentialStatus.INVALID -> MaterialTheme.colorScheme.errorContainer
                },
                shape = RoundedCornerShape(Sizes.s04)
            )
            .padding(start = Sizes.s03, end = Sizes.s03),
    ) {
        credentialStatus?.let {
            CredentialStatusLabel(
                status = credentialStatus,
                modifier = modifier
            )
        } ?: Spacer(modifier.height(Sizes.labelHeight))
    }
}

private class CredentialStatusProvider : PreviewParameterProvider<CredentialStatus> {
    override val values: Sequence<CredentialStatus> = sequenceOf(
        CredentialStatus.UNKNOWN,
        CredentialStatus.INVALID,
        CredentialStatus.VALID,
    )
}

@Composable
@WalletComponentPreview
private fun CredentialStatusBadgePreview(@PreviewParameter(CredentialStatusProvider::class) status: CredentialStatus) {
    PilotWalletTheme {
        CredentialStatusBadge(
            status = status,
        )
    }
}
