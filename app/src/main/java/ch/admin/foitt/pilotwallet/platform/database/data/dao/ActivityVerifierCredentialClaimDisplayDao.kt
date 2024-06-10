package ch.admin.foitt.pilotwallet.platform.database.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifierCredentialClaimDisplay

@Dao
interface ActivityVerifierCredentialClaimDisplayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(activityClaimDisplay: ActivityVerifierCredentialClaimDisplay): Long

    @Query("SELECT * FROM activityverifiercredentialclaimdisplay WHERE id = :id")
    fun getById(id: Long): ActivityVerifierCredentialClaimDisplay?

    @Query("SELECT * FROM activityverifiercredentialclaimdisplay WHERE activityClaimId = :id")
    fun getByClaimId(id: Long): List<ActivityVerifierCredentialClaimDisplay>

    @Query("DELETE FROM activityverifiercredentialclaimdisplay WHERE id = :id")
    fun deleteById(id: Long)
}
