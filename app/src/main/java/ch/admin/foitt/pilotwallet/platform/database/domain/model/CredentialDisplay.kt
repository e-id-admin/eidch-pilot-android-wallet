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
data class CredentialDisplay(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val credentialId: Long, // ForeignKey
    override val locale: String,
    val name: String,
    val description: String? = null,
    val logoUrl: String? = null,
    val logoData: String? = null,
    val logoAltText: String? = null,
    val backgroundColor: String? = null,
    val textColor: String? = null,
) : LocalizedDisplay
