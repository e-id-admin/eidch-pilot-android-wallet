package ch.admin.foitt.pilotwallet.platform.activity.presentation.mock

import ch.admin.foitt.pilotwallet.platform.activity.presentation.model.ActivityListItem
import ch.admin.foitt.pilotwallet.platform.activity.presentation.model.HomeScreenActivity
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityType

object ActivityMocks {
    val activityListItem01 get() = ActivityListItem.PresentationAccepted(
        id = 3938,
        dateTimeString = "19. Dec | 12:19",
        verifier = "LicenceCheck 01",
    )
    val activityListItem02 get() = ActivityListItem.PresentationDeclined(
        id = 0,
        dateTimeString = "01. Jan | 12:19",
        verifier = "Unknown verifier",
    )

    val activityListItem04 get() = ActivityListItem.CredentialReceived(
        id = 998977,
        dateTimeString = "04. Sept | 12:19",
        issuer = "The issuer name",
    )
    val activityListItem05 get() = ActivityListItem.PresentationAccepted(
        id = 998977,
        dateTimeString = "04. Sept | 13:07",
        verifier = "",
    )

    val activityList by lazy {
        listOf(
            activityListItem01,
            activityListItem02,
            activityListItem04,
            activityListItem05
        )
    }

    val recentActivities by lazy {
        listOf(
            activityListItem02,
            activityListItem01,
            activityListItem04,
        )
    }

    val latestActivity get() = HomeScreenActivity(
        id = 0,
        credentialId = 1,
        type = ActivityType.CREDENTIAL_RECEIVED,
        dateTimeString = "14. May 2024 | 15:45",
        name = "Issuer Preview"
    )

    val activityMap by lazy {
        mapOf(
            "May 2024" to listOf(activityListItem01, activityListItem02),
            "Apr 2024" to listOf(activityListItem04)
        )
    }
}
