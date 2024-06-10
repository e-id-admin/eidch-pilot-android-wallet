package ch.admin.foitt.pilotwallet.platform.ssi.data.source.local

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ch.admin.foitt.pilotwallet.platform.database.data.dao.ActivityDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.pilotwallet.platform.database.domain.AppDatabase
import ch.admin.foitt.pilotwallet.platform.database.domain.model.Activity
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.activity1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.activity2
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.activity3
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.activity4
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential1
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ActivityDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var credentialDao: CredentialDao
    private lateinit var activityDao: ActivityDao

    @BeforeEach
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        credentialDao = database.credentialDao()
        activityDao = database.activityDao()
    }

    @AfterEach
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertActivityWithoutCredentialTest() = runTest {
        assertThrows<SQLiteConstraintException> { activityDao.insert(activity1) }
    }

    @Test
    fun insertActivityTest() = runTest {
        credentialDao.insert(credential1)
        activityDao.insert(activity1)

        assertEquals(
            activity1.id,
            activityDao.getLatestActivityFlow().first()?.activity?.id
        )
        assertEquals(
            activity1.type,
            activityDao.getLatestActivityFlow().first()?.activity?.type
        )
        assertEquals(
            activity1.credentialSnapshotStatus,
            activityDao.getLatestActivityFlow().first()?.activity?.credentialSnapshotStatus
        )
        assertEquals(
            activity1.createdAt,
            activityDao.getLatestActivityFlow().first()?.activity?.createdAt
        )
    }

    @Test
    fun getLatestActivityTest() = runTest {
        credentialDao.insert(credential1)

        activityDao.insert(activity1)
        activityDao.insert(activity2)
        activityDao.insert(activity3)
        activityDao.insert(activity4)

        assertEquals(
            activity4.id,
            activityDao.getLatestActivityFlow().first()?.activity?.id
        )
    }

    @Test
    fun getLastNActivitiesForCredentialTest() = runTest {
        credentialDao.insert(credential1)

        activityDao.insert(activity1)
        activityDao.insert(activity2)
        activityDao.insert(activity3)
        activityDao.insert(activity4)

        val activityWithVerifierList = activityDao.getLastNActivitiesForCredentialFlow(id = credential1.id, amount = 3).first()

        assertEquals(
            3,
            activityWithVerifierList.size
        )
        assertEquals(
            activity4.id,
            activityWithVerifierList[0].activity.id
        )
        assertEquals(
            activity3.id,
            activityWithVerifierList[1].activity.id
        )
        assertEquals(
            activity2.id,
            activityWithVerifierList[2].activity.id
        )
    }

    @Test
    fun getActivitiesForCredentialTest() = runTest {
        credentialDao.insert(credential1)

        activityDao.insert(activity1)
        activityDao.insert(activity2)
        activityDao.insert(activity3)
        activityDao.insert(activity4)

        val activityWithVerifierList = activityDao.getActivitiesForCredentialFlow(credential1.id).first()

        assertEquals(
            4,
            activityWithVerifierList.size
        )
        assertEquals(
            activity4.id,
            activityWithVerifierList[0].activity.id
        )
        assertEquals(
            activity3.id,
            activityWithVerifierList[1].activity.id
        )
        assertEquals(
            activity2.id,
            activityWithVerifierList[2].activity.id
        )
        assertEquals(
            activity1.id,
            activityWithVerifierList[3].activity.id
        )
    }

    @Test
    fun deletingActivityTest() = runTest {
        credentialDao.insert(credential1)
        activityDao.insert(activity1)
        activityDao.insert(activity2)

        activityDao.deleteById(activity1.id)

        val activityWithVerifierList = activityDao.getActivitiesForCredentialFlow(credential1.id).first()

        assertEquals(
            1,
            activityWithVerifierList.size
        )
        assertEquals(
            activity2.id,
            activityWithVerifierList[0].activity.id
        )
    }

    @Test
    fun deletingCredentialShouldCascadeDeletionTest() = runTest {
        credentialDao.insert(credential1)
        activityDao.insert(activity1)

        credentialDao.deleteById(credential1.id)

        assertEquals(
            emptyList<Activity>(),
            activityDao.getActivitiesForCredentialFlow(credential1.id).first()
        )
    }
}
