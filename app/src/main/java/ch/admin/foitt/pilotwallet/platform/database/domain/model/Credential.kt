package ch.admin.foitt.pilotwallet.platform.database.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity
data class Credential(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val status: CredentialStatus = CredentialStatus.UNKNOWN,
    val createdAt: Long = Instant.now().epochSecond,
    val updatedAt: Long? = null
)
