package ch.admin.foitt.pilotwallet.platform.database.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifierCredentialClaim

@Dao
interface ActivityVerifierCredentialClaimDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(activityClaim: ActivityVerifierCredentialClaim): Long

    @Query("SELECT * FROM activityverifiercredentialclaim WHERE id = :id")
    fun getById(id: Long): ActivityVerifierCredentialClaim?

    @Query("SELECT * FROM activityverifiercredentialclaim WHERE activityVerifierId = :id")
    fun getByVerifierId(id: Long): List<ActivityVerifierCredentialClaim>

    @Query("DELETE FROM activityverifiercredentialclaim WHERE id = :id")
    fun deleteById(id: Long)
}
