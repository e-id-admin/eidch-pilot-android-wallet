package ch.admin.foitt.pilotwallet.feature.settings.presentation.composables

import androidx.compose.foundation.clickable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.preview.WalletComponentPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme

@Composable
fun SettingsItem(
    title: String,
    onItemClick: () -> Unit,
    trailingContent: @Composable () -> Unit = {
        Icon(
            painter = painterResource(id = R.drawable.material_chevron_right),
            contentDescription = "More information"
        )
    },
) {
    ListItem(
        modifier = Modifier.clickable {
            onItemClick.invoke()
        },
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        trailingContent = {
            trailingContent.invoke()
        }
    )
    HorizontalDivider()
}

@WalletComponentPreview
@Composable
fun SettingsItemPreview() {
    PilotWalletTheme {
        SettingsItem(
            title = "Feedback geben",
            onItemClick = {}
        )
    }
}
