package ch.admin.foitt.pilotwallet.platform.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import ch.admin.foitt.pilotwallet.theme.FadingVisibility

@Composable
fun LoadingOverlay(showOverlay: Boolean) {
    FadingVisibility(
        visible = showOverlay,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim)
                .zIndex(1.0f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {},
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
