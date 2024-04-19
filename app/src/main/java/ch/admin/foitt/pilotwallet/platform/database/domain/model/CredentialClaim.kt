package ch.admin.foitt.pilotwallet.platform.database.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Credential::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("credentialId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("credentialId")
    ]
)
data class CredentialClaim(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val credentialId: Long, // ForeignKey
    val key: String,
    val value: String,
    val valueType: String?,
    val order: Int = -1
)
