package ch.admin.foitt.pilotwallet.platform.database.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Activity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("activityId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index("activityId"),
    ]
)
data class ActivityVerifier(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val activityId: Long, // ForeignKey
    val name: String,
    val logo: String?,
)
