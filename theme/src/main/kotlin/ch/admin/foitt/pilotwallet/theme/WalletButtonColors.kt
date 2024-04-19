package ch.admin.foitt.pilotwallet.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object WalletButtonColors {
    @Composable
    fun primary(
        containerColor: Color = MaterialTheme.colorScheme.primary,
        contentColor: Color = MaterialTheme.colorScheme.secondary,
        disabledContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor: Color = MaterialTheme.colorScheme.primary,
    ): ButtonColors = ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )

    @Composable
    fun secondary(
        containerColor: Color = MaterialTheme.colorScheme.secondary,
        contentColor: Color = MaterialTheme.colorScheme.primary,
        disabledContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor: Color = MaterialTheme.colorScheme.primary,
    ): ButtonColors = ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )

    @Composable
    fun tertiary(
        containerColor: Color = MaterialTheme.colorScheme.tertiary,
        contentColor: Color = MaterialTheme.colorScheme.secondary,
        disabledContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor: Color = MaterialTheme.colorScheme.primary,
        activeContainerColor: Color = MaterialTheme.colorScheme.activeButtonBackground,
        isActive: Boolean = false,
    ): ButtonColors {
        return if (isActive) {
            ButtonDefaults.buttonColors(
                containerColor = activeContainerColor,
                contentColor = contentColor,
                disabledContainerColor = activeContainerColor,
                disabledContentColor = contentColor,
            )
        } else {
            ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
                disabledContainerColor = disabledContainerColor,
                disabledContentColor = disabledContentColor,
            )
        }
    }

    @Composable
    fun warning(
        containerColor: Color = MaterialTheme.colorScheme.warningLight,
        contentColor: Color = MaterialTheme.colorScheme.onWarningBackgroundDark,
        disabledContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor: Color = MaterialTheme.colorScheme.primary,
    ): ButtonColors = ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )

    @Composable
    fun outlined(
        containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor: Color = MaterialTheme.colorScheme.primary,
        disabledContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor: Color = MaterialTheme.colorScheme.primary,
    ): ButtonColors = ButtonDefaults.outlinedButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )
}
