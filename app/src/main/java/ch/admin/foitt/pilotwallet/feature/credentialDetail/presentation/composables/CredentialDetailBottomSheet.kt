package ch.admin.foitt.pilotwallet.feature.credentialDetail.presentation.composables

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.theme.warningLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CredentialDetailBottomSheet(
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onShowPoliceControl: () -> Unit,
    showPoliceControlItem: Boolean
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        if (showPoliceControlItem) {
            CredentialDetailBottomSheetItem(
                R.drawable.pilot_ic_eye,
                title = stringResource(id = R.string.credential_menu_police_control_text),
                onClick = onShowPoliceControl

            )
            HorizontalDivider()
        }
        CredentialDetailBottomSheetItem(
            R.drawable.pilot_ic_bin,
            title = stringResource(id = R.string.credential_menu_delete_text),
            onClick = onDelete,
            color = MaterialTheme.colorScheme.warningLight
        )
    }
}
