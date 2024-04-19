package ch.admin.foitt.pilotwallet.platform.database.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ch.admin.foitt.pilotwallet.platform.database.domain.model.Credential
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface CredentialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(credential: Credential): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(credential: Credential): Int

    @Query("DELETE FROM Credential WHERE id = :id")
    fun deleteById(id: Long)

    @Query("SELECT * FROM Credential WHERE id = :id")
    fun getById(id: Long): Credential?

    @Query("SELECT * FROM Credential WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<Credential?>

    @Query("SELECT status FROM Credential WHERE id = :id")
    fun getCredentialStatusByIdAsFlow(id: Long): Flow<CredentialStatus>

    @Query("SELECT * FROM Credential ORDER BY createdAt DESC")
    fun getAll(): List<Credential>

    @Query("SELECT * FROM Credential ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<Credential>>
}
