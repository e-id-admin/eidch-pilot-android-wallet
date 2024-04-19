package ch.admin.foitt.pilotwallet.platform.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.sp
import ch.admin.foitt.pilotwallet.platform.preview.WalletComponentPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme

@Composable
fun ScreenHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
    ) {
        Text(
            text = text.toUpperCase(Locale.current),
            fontWeight = FontWeight.W700,
            modifier = Modifier
                .fillMaxWidth(),
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            fontSize = 14.sp,
        )
    }
}

@WalletComponentPreview
@Composable
private fun ScreenHeaderPreview() {
    PilotWalletTheme {
        ScreenHeader(text = "Mein Aktivitäten")
    }
}
