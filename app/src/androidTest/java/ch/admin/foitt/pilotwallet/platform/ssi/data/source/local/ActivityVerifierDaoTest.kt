package ch.admin.foitt.pilotwallet.platform.ssi.data.source.local

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ch.admin.foitt.pilotwallet.platform.database.data.dao.ActivityDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.ActivityVerifierDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.pilotwallet.platform.database.domain.AppDatabase
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.activity2
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.activityVerifier1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential1
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ActivityVerifierDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var credentialDao: CredentialDao
    private lateinit var activityDao: ActivityDao
    private lateinit var activityVerifierDao: ActivityVerifierDao

    @BeforeEach
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        credentialDao = database.credentialDao()
        activityDao = database.activityDao()
        activityVerifierDao = database.activityVerifierDao()
    }

    @AfterEach
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertActivityVerifierWithoutActivityTest() = runTest {
        assertThrows<SQLiteConstraintException> {
            activityVerifierDao.insert(activityVerifier1)
        }
    }

    @Test
    fun insertActivityVerifierTest() = runTest {
        credentialDao.insert(credential1)
        activityDao.insert(activity2)
        activityVerifierDao.insert(activityVerifier1)

        assertEquals(activityVerifier1.name, activityVerifierDao.getById(id = 1)?.name)
        assertEquals(activityVerifier1.logo, activityVerifierDao.getById(id = 1)?.logo)
    }

    @Test
    fun deletingActivityShouldCascadeDeletionTest() = runTest {
        credentialDao.insert(credential1)
        activityDao.insert(activity2)

        activityVerifierDao.insert(activityVerifier1)

        assertNotNull(
            activityDao.getLatestActivityFlow().firstOrNull()
        )

        activityDao.deleteById(activity2.id)

        assertNotNull(
            credentialDao.getById(1)
        )
        assertNull(
            activityDao.getLatestActivityFlow().firstOrNull()
        )
        assertNull(
            activityVerifierDao.getById(activityVerifier1.id)
        )
    }

    @Test
    fun deletingCredentialShouldCascadeDeletionTest() = runTest {
        credentialDao.insert(credential1)

        activityDao.insert(activity2)
        activityVerifierDao.insert(activityVerifier1)

        credentialDao.deleteById(credential1.id)

        assertNull(credentialDao.getById(1))
        assertNull(activityDao.getById(activity2.id))
        assertNull(activityDao.getLatestActivityFlow().firstOrNull())
        assertNull(activityVerifierDao.getById(activityVerifier1.id))
    }
}
