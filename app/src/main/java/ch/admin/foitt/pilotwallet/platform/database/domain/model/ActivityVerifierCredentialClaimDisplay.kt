package ch.admin.foitt.pilotwallet.platform.database.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ActivityVerifierCredentialClaim::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("activityClaimId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index("activityClaimId"),
    ]
)
data class ActivityVerifierCredentialClaimDisplay(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val activityClaimId: Long, // ForeignKey
    override val name: String,
    override val locale: String,
) : ClaimDisplay

fun CredentialClaimDisplay.toActivityVerifierCredentialClaimDisplay(claimId: Long) = ActivityVerifierCredentialClaimDisplay(
    activityClaimId = claimId,
    name = name,
    locale = locale,
)
