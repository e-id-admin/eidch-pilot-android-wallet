package ch.admin.foitt.pilotwallet.platform.database.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialRaw

@Dao
interface CredentialRawDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(credentialRaw: CredentialRaw): Long

    @Query("SELECT * FROM CredentialRaw WHERE credentialId = :credentialId")
    fun getByCredentialId(credentialId: Long): List<CredentialRaw>

    @Query("SELECT * FROM CredentialRaw")
    fun getAll(): List<CredentialRaw>
}
