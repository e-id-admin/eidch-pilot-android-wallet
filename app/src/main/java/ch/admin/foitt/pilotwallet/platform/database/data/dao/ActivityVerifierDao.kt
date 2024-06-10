package ch.admin.foitt.pilotwallet.platform.database.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifier

@Dao
interface ActivityVerifierDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(activityVerifier: ActivityVerifier): Long

    @Query("SELECT * FROM activityverifier WHERE id = :id")
    fun getById(id: Long): ActivityVerifier?

    @Query("DELETE FROM activityverifier where id = :id")
    fun deleteById(id: Long)

    @Query("SELECT * FROM activityverifier WHERE activityId = :id")
    fun getByActivityId(id: Long): ActivityVerifier?
}
