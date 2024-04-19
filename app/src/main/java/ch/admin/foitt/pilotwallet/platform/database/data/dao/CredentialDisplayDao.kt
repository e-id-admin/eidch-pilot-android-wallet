package ch.admin.foitt.pilotwallet.platform.database.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialDisplay
import kotlinx.coroutines.flow.Flow

@Dao
interface CredentialDisplayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(credentialDisplays: Collection<CredentialDisplay>)

    @Query("SELECT * FROM CredentialDisplay")
    fun getAllGroupedByCredentialId(): Flow<
        Map<
            @MapColumn(columnName = "credentialId")
            Long,
            List<CredentialDisplay>
            >
        >

    @Query("SELECT * FROM CredentialDisplay WHERE credentialId = :credentialId")
    fun getAsFlow(credentialId: Long): Flow<List<CredentialDisplay>>
}
