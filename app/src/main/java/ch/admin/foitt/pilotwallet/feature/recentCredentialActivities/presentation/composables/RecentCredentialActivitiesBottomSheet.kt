package ch.admin.foitt.pilotwallet.feature.recentCredentialActivities.presentation.composables

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.CredentialDetailBottomSheetItem
import ch.admin.foitt.pilotwallet.theme.warningLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentCredentialActivitiesBottomSheet(
    showPoliceControlItem: Boolean,
    onDismiss: () -> Unit,
    onShowCredentialContent: () -> Unit,
    onShowPoliceControl: () -> Unit,
    onDelete: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        CredentialDetailBottomSheetItem(
            icon = R.drawable.pilot_ic_info,
            title = stringResource(id = R.string.credential_menu_details_text),
            onClick = onShowCredentialContent,
        )
        HorizontalDivider()
        if (showPoliceControlItem) {
            CredentialDetailBottomSheetItem(
                icon = R.drawable.pilot_ic_eye,
                title = stringResource(id = R.string.credential_menu_police_control_text),
                onClick = onShowPoliceControl,
            )
            HorizontalDivider()
        }
        CredentialDetailBottomSheetItem(
            icon = R.drawable.pilot_ic_bin_bb,
            title = stringResource(id = R.string.credential_menu_delete_text),
            onClick = onDelete,
            color = MaterialTheme.colorScheme.warningLight
        )
    }
}
