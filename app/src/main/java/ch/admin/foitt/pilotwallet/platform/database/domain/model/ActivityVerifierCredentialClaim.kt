package ch.admin.foitt.pilotwallet.platform.database.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ActivityVerifier::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("activityVerifierId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index("activityVerifierId"),
    ]
)
data class ActivityVerifierCredentialClaim(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val activityVerifierId: Long, // ForeignKey
    override val key: String,
    override val value: String,
    override val valueType: String?,
    val order: Int,
) : Claim

fun CredentialClaim.toActivityVerifierCredentialClaim(activityVerifierId: Long) = ActivityVerifierCredentialClaim(
    activityVerifierId = activityVerifierId,
    key = key,
    value = value,
    valueType = valueType,
    order = order,
)
