package ch.admin.foitt.pilotwallet.platform.pinInput.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes

@Composable
fun PinPlaceholder(
    modifier: Modifier = Modifier,
    showError: Boolean,
    isSet: Boolean,
) {
    val color = if (showError) {
        MaterialTheme.colorScheme.onError
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .requiredSize(size = Sizes.s04)
            .border(
                width = 2.dp,
                color = color,
                shape = CircleShape
            )
            .background(if (isSet) color else Color.Transparent)
    )
}

@Preview(showBackground = true)
@Composable
fun PinPlaceholderPreview() {
    PilotWalletTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(Sizes.s04)
        ) {
            PinPlaceholder(showError = false, isSet = true)
            PinPlaceholder(showError = false, isSet = false)
            PinPlaceholder(showError = true, isSet = false)
        }
    }
}
