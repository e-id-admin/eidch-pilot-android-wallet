package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock

import ch.admin.foitt.pilotwallet.platform.database.domain.model.Activity
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityType

object MockActivity {
    const val ACTIVITY_ID = 1L
    const val CREATED_AT = 5L
    val ACTIVITY_TYPE = ActivityType.PRESENTATION_ACCEPTED
    val activity = Activity(
        id = ACTIVITY_ID,
        credentialId = MockCredential.CREDENTIAL_ID,
        type = ACTIVITY_TYPE,
        credentialSnapshotStatus = MockCredential.CREDENTIAL_STATUS,
        createdAt = CREATED_AT,
    )
}
