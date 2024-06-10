package ch.admin.foitt.pilotwallet.platform.activity.presentation.adapter.implementation

import ch.admin.foitt.pilotwallet.platform.activity.presentation.adapter.GetActivityListItem
import ch.admin.foitt.pilotwallet.platform.activity.presentation.model.ActivityListItem
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetLocalizedDateTime
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityType
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityWithVerifier
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetLocalizedCredentialIssuerDisplay
import ch.admin.foitt.pilotwallet.platform.utils.epochSecondsToZonedDateTime
import javax.inject.Inject

class GetActivityListItemImpl @Inject constructor(
    private val getLocalizedDateTime: GetLocalizedDateTime,
    private val getLocalizedCredentialIssuerDisplay: GetLocalizedCredentialIssuerDisplay,
) : GetActivityListItem {
    override suspend fun invoke(activityWithVerifier: ActivityWithVerifier): ActivityListItem {
        val activity = activityWithVerifier.activity
        val verifier = activityWithVerifier.verifier
        val dateTimeString = getLocalizedDateTime(activity.createdAt.epochSecondsToZonedDateTime())
        val issuerName = getLocalizedCredentialIssuerDisplay(activity.credentialId)?.name ?: ""
        return when (activity.type) {
            ActivityType.CREDENTIAL_RECEIVED -> ActivityListItem.CredentialReceived(
                id = activity.id,
                dateTimeString = dateTimeString,
                issuer = issuerName,
            )
            ActivityType.PRESENTATION_ACCEPTED -> ActivityListItem.PresentationAccepted(
                id = activity.id,
                dateTimeString = dateTimeString,
                verifier = verifier?.name ?: "",
            )
            ActivityType.PRESENTATION_DECLINED -> ActivityListItem.PresentationDeclined(
                id = activity.id,
                dateTimeString = dateTimeString,
                verifier = verifier?.name ?: "",
            )
        }
    }
}
