package ch.admin.foitt.pilotwallet.platform.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.zIndex
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts

@Composable
internal fun InvitationHeader(
    inviterName: String,
    inviterImage: Painter?,
    message: String,
    modifier: Modifier = Modifier,
) = Column(
    modifier = Modifier
        .zIndex(1f)
        .background(MaterialTheme.colorScheme.background)
) {
    Spacer(modifier = Modifier.height(Sizes.s04))
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        inviterImage?.let {
            Image(
                painter = inviterImage,
                modifier = Modifier.size(Sizes.s12),
                contentScale = ContentScale.Fit,
                contentDescription = null
            )
        } ?: Spacer(
            modifier = Modifier.size(Sizes.s12)
        )
        Spacer(modifier = Modifier.size(Sizes.s04))
        Column {
            WalletTexts.TitleSmall(text = inviterName)
            WalletTexts.BodyLarge(text = message)
        }
    }
    Spacer(modifier = Modifier.height(Sizes.s10))
}
