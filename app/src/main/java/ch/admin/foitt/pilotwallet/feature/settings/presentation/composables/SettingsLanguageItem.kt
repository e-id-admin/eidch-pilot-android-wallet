package ch.admin.foitt.pilotwallet.feature.settings.presentation.composables

import androidx.compose.foundation.clickable
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
fun SettingsLanguageItem(
    title: String,
    isChecked: Boolean,
    onItemClick: () -> Unit
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
            if (isChecked) {
                Icon(
                    painter = painterResource(id = R.drawable.material_checkmark),
                    contentDescription = "Language is chosen"
                )
            }
        }
    )
}

@WalletComponentPreview
@Composable
fun SettingsLanguageItemPreview() {
    PilotWalletTheme {
        SettingsLanguageItem(
            title = "English",
            isChecked = true,
            onItemClick = {}
        )
    }
}
