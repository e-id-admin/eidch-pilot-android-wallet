package ch.admin.foitt.pilotwallet.platform.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import ch.admin.foitt.pilotwallet.R

@Composable
fun NavigationHomeIcon(
    isBackbuttonVisible: Boolean = false,
    onBackbuttonClick: () -> Unit = {},
) {
    if (isBackbuttonVisible) {
        IconButton(onClick = onBackbuttonClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
    } else {
        IconButton(onClick = { }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_swiss_cross_small),
                tint = Color.Unspecified,
                contentDescription = "Home"
            )
        }
    }
}
