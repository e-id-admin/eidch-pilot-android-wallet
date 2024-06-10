package ch.admin.foitt.pilotwallet.platform.database.domain.model

import androidx.room.Embedded
import androidx.room.Relation

data class ActivityWithVerifier(
    @Embedded
    val activity: Activity,
    @Relation(
        parentColumn = "id",
        entityColumn = "activityId",
    )
    val verifier: ActivityVerifier?
)
