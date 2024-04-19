package ch.admin.foitt.pilotwallet.platform.database.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = CredentialClaim::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("claimId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("claimId")
    ]
)
data class CredentialClaimDisplay(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val claimId: Long, // Foreign Key
    val name: String,
    override val locale: String,
) : LocalizedDisplay
