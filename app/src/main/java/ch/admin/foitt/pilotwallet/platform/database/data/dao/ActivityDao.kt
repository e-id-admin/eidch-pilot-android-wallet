package ch.admin.foitt.pilotwallet.platform.database.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ch.admin.foitt.pilotwallet.platform.database.domain.model.Activity
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityDetail
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityWithVerifier
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(activity: Activity): Long

    @Query("SELECT * FROM activity WHERE id = :id")
    fun getById(id: Long): Activity?

    @Transaction
    @Query(
        "SELECT * " +
            "FROM activity " +
            "ORDER BY activity.createdAt DESC " +
            "LIMIT 1"
    )
    fun getLatestActivityFlow(): Flow<ActivityWithVerifier?>

    @Transaction
    @Query(
        "SELECT * " +
            "FROM activity " +
            "WHERE activity.credentialId = :id " +
            "ORDER BY activity.createdAt DESC " +
            "LIMIT :amount"
    )
    fun getLastNActivitiesForCredentialFlow(id: Long, amount: Int): Flow<List<ActivityWithVerifier>>

    @Query("DELETE FROM activity WHERE id = :id")
    fun deleteById(id: Long)

    @Transaction
    @Query("SELECT * FROM activity WHERE id = :id")
    fun getActivityDetailFlow(id: Long): Flow<ActivityDetail>

    @Transaction
    @Query("SELECT * FROM activity WHERE credentialId = :id ORDER BY createdAt DESC")
    fun getActivitiesForCredentialFlow(id: Long): Flow<List<ActivityWithVerifier>>
}
