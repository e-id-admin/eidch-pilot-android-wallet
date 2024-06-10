package ch.admin.foitt.pilotwallet.feature.home.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import ch.admin.foitt.pilotwallet.platform.activity.presentation.composables.ActivityCredentialReceived
import ch.admin.foitt.pilotwallet.platform.activity.presentation.composables.ActivityPresentationAccepted
import ch.admin.foitt.pilotwallet.platform.activity.presentation.composables.ActivityPresentationDeclined
import ch.admin.foitt.pilotwallet.platform.activity.presentation.model.HomeScreenActivity
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityType
import ch.admin.foitt.pilotwallet.platform.preview.WalletComponentPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.textOutline

@Composable
fun ActivityItem(
    activity: HomeScreenActivity,
    onActivity: (activityId: Long) -> Unit,
) = Card(
    shape = RectangleShape,
    colors = CardColors(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = CardDefaults.cardColors().contentColor,
        disabledContainerColor = CardDefaults.cardColors().disabledContainerColor,
        disabledContentColor = CardDefaults.cardColors().disabledContentColor
    ),
    border = BorderStroke(
        width = Sizes.line01,
        color = MaterialTheme.colorScheme.textOutline
    )
) {
    when (activity.type) {
        ActivityType.CREDENTIAL_RECEIVED -> ActivityCredentialReceived(
            issuerName = activity.name,
            dateTimeString = activity.dateTimeString,
        )
        ActivityType.PRESENTATION_ACCEPTED -> ActivityPresentationAccepted(
            verifierName = activity.name,
            dateTimeString = activity.dateTimeString,
            onClick = { onActivity(activity.id) },
        )
        ActivityType.PRESENTATION_DECLINED -> ActivityPresentationDeclined(
            verifierName = activity.name,
            dateTimeString = activity.dateTimeString,
            onClick = { onActivity(activity.id) },
        )
    }
}

private class ActivityItemPreviewParam : PreviewParameterProvider<HomeScreenActivity> {
    override val values: Sequence<HomeScreenActivity> = sequenceOf(
        HomeScreenActivity(
            id = 0,
            credentialId = 1,
            type = ActivityType.CREDENTIAL_RECEIVED,
            name = "Preview issuer",
            dateTimeString = "12. May 2024 | 15:00"
        ),
        HomeScreenActivity(
            id = 0,
            credentialId = 1,
            type = ActivityType.PRESENTATION_ACCEPTED,
            name = "Preview verifier",
            dateTimeString = "12. May 2024 | 15:00"
        ),
        HomeScreenActivity(
            id = 0,
            credentialId = 1,
            type = ActivityType.PRESENTATION_DECLINED,
            name = "Preview verifier",
            dateTimeString = "12. May 2024 | 15:00"
        ),
    )
}

@WalletComponentPreview
@Composable
fun ActivityItemPreview(
    @PreviewParameter(ActivityItemPreviewParam::class) activity: HomeScreenActivity
) {
    PilotWalletTheme {
        ActivityItem(
            activity = activity,
            onActivity = {},
        )
    }
}
