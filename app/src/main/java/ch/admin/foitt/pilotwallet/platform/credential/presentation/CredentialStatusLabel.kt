package ch.admin.foitt.pilotwallet.platform.credential.presentation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts

@Composable
internal fun CredentialStatusLabel(
    modifier: Modifier = Modifier,
    validColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    status: CredentialStatus,
) {
    val color = when (status) {
        CredentialStatus.VALID,
        CredentialStatus.UNKNOWN -> validColor
        CredentialStatus.INVALID -> MaterialTheme.colorScheme.onErrorContainer
    }
    Row(
        modifier = modifier.heightIn(min = Sizes.labelHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LabelIcon(status = status, color = color)
        Spacer(modifier = Modifier.size(Sizes.s01))
        LabelText(status = status, color = color)
    }
}

@Composable
private fun LabelIcon(
    status: CredentialStatus,
    color: Color,
) = Icon(
    painter = when (status) {
        CredentialStatus.VALID -> painterResource(id = R.drawable.pilot_ic_checkmark)
        CredentialStatus.INVALID,
        CredentialStatus.UNKNOWN -> painterResource(id = R.drawable.pilot_ic_invalid)
    },
    contentDescription = null,
    tint = color,
)

@Composable
private fun LabelText(
    status: CredentialStatus,
    color: Color,
) = WalletTexts.CredentialStatus(
    text = when (status) {
        CredentialStatus.VALID -> stringResource(id = R.string.credential_status_valid)
        CredentialStatus.INVALID -> stringResource(id = R.string.credential_status_invalid)
        CredentialStatus.UNKNOWN -> stringResource(id = R.string.credential_status_unknown)
    },
    color = color,
)
