package ch.admin.foitt.pilotwallet.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object WalletCards {

    @Composable
    fun InfoCard(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) = Card(
        modifier = modifier,
        shape = RoundedCornerShape(Sizes.s01),
        colors = CardDefaults.cardColors(containerColor = Colors.blue04),
    ) {
        content()
    }
}
