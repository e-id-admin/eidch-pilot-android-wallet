package ch.admin.foitt.pilotwallet.platform.activity.presentation.model

import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityType

data class HomeScreenActivity(
    val id: Long,
    val credentialId: Long,
    val type: ActivityType,
    val name: String,
    val dateTimeString: String
)
