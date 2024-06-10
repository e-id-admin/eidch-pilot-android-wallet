package ch.admin.foitt.pilotwallet.platform.ssi.data.source.local

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ch.admin.foitt.pilotwallet.platform.database.data.dao.ActivityDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.ActivityVerifierCredentialClaimDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.ActivityVerifierCredentialClaimDisplayDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.ActivityVerifierDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.pilotwallet.platform.database.domain.AppDatabase
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.activity2
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.activityClaim1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.activityClaim2
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.activityCredentialClaimDisplay1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.activityCredentialClaimDisplay2
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.activityCredentialClaimDisplay3
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

class ActivityVerifierCredentialClaimDisplayDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var credentialDao: CredentialDao
    private lateinit var activityDao: ActivityDao
    private lateinit var activityVerifierDao: ActivityVerifierDao
    private lateinit var activityVerifierCredentialClaimDao: ActivityVerifierCredentialClaimDao
    private lateinit var activityVerifierCredentialClaimDisplayDao: ActivityVerifierCredentialClaimDisplayDao

    @BeforeEach
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).allowMainThreadQueries().build()

        credentialDao = database.credentialDao()
        activityDao = database.activityDao()
        activityVerifierDao = database.activityVerifierDao()
        activityVerifierCredentialClaimDao = database.activityVerifierCredentialClaimDao()
        activityVerifierCredentialClaimDisplayDao = database.activityVerifierCredentialClaimDisplayDao()
    }

    @AfterEach
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertActivityVerifierCredentialClaimDisplayWithoutActivityVerifierCredentialClaimTest() = runTest {
        assertThrows<SQLiteConstraintException> {
            activityVerifierCredentialClaimDisplayDao.insert(activityCredentialClaimDisplay1)
        }
    }

    @Test
    fun insertActivityVerifierCredentialClaimDisplayTest() = runTest {
        credentialDao.insert(credential1)
        activityDao.insert(activity2)
        activityVerifierDao.insert(activityVerifier1)
        activityVerifierCredentialClaimDao.insert(activityClaim1)
        val id = activityVerifierCredentialClaimDisplayDao.insert(activityCredentialClaimDisplay1)

        assertEquals(activityCredentialClaimDisplay1.id, activityVerifierCredentialClaimDisplayDao.getById(id)?.id)
        assertEquals(activityCredentialClaimDisplay1.id, activityVerifierCredentialClaimDisplayDao.getById(id)?.activityClaimId)
        assertEquals(activityCredentialClaimDisplay1.name, activityVerifierCredentialClaimDisplayDao.getById(id)?.name)
        assertEquals(activityCredentialClaimDisplay1.locale, activityVerifierCredentialClaimDisplayDao.getById(id)?.locale)
    }

    @Test
    fun getByClaimIdTest() = runTest {
        credentialDao.insert(credential1)
        activityDao.insert(activity2)
        activityVerifierDao.insert(activityVerifier1)
        activityVerifierCredentialClaimDao.insert(activityClaim1)
        activityVerifierCredentialClaimDao.insert(activityClaim2)
        activityVerifierCredentialClaimDisplayDao.insert(activityCredentialClaimDisplay1)
        activityVerifierCredentialClaimDisplayDao.insert(activityCredentialClaimDisplay2)
        activityVerifierCredentialClaimDisplayDao.insert(activityCredentialClaimDisplay3)

        val displaysOfClaim1 = activityVerifierCredentialClaimDisplayDao.getByClaimId(activityClaim1.id)

        assertEquals(listOf(activityCredentialClaimDisplay1, activityCredentialClaimDisplay2), displaysOfClaim1)

    }

    @Test
    fun deleteActivityVerifierCredentialClaimDisplayTest() = runTest {
        credentialDao.insert(credential1)
        activityDao.insert(activity2)
        activityVerifierDao.insert(activityVerifier1)
        activityVerifierCredentialClaimDao.insert(activityClaim1)
        activityVerifierCredentialClaimDisplayDao.insert(activityCredentialClaimDisplay1)

        activityVerifierCredentialClaimDisplayDao.deleteById(activityCredentialClaimDisplay1.id)

        assertNotNull(
            credentialDao.getById(credential1.id)
        )
        assertNotNull(
            activityDao.getLatestActivityFlow().firstOrNull()
        )
        assertNotNull(
            activityVerifierDao.getById(activityVerifier1.id)
        )
        assertNotNull(
            activityVerifierCredentialClaimDao.getById(activityClaim1.id)
        )
        assertNull(
            activityVerifierCredentialClaimDisplayDao.getById(activityCredentialClaimDisplay1.id)
        )
    }

    @Test
    fun deletingActivityVerifierCredentialClaimShouldCascadeDeletionTest() = runTest {
        credentialDao.insert(credential1)
        activityDao.insert(activity2)
        activityVerifierDao.insert(activityVerifier1)
        activityVerifierCredentialClaimDao.insert(activityClaim1)
        activityVerifierCredentialClaimDisplayDao.insert(activityCredentialClaimDisplay1)

        activityVerifierCredentialClaimDao.deleteById(activityClaim1.id)

        assertNotNull(
            credentialDao.getById(credential1.id)
        )
        assertNotNull(
            activityDao.getLatestActivityFlow().firstOrNull()
        )
        assertNotNull(
            activityVerifierDao.getById(activityVerifier1.id)
        )
        assertNull(
            activityVerifierCredentialClaimDao.getById(activityClaim1.id)
        )
        assertNull(
            activityVerifierCredentialClaimDisplayDao.getById(activityCredentialClaimDisplay1.id)
        )
    }

    @Test
    fun deletingActivityVerifierShouldCascadeDeletionTest() = runTest {
        credentialDao.insert(credential1)
        activityDao.insert(activity2)
        activityVerifierDao.insert(activityVerifier1)
        activityVerifierCredentialClaimDao.insert(activityClaim1)
        activityVerifierCredentialClaimDisplayDao.insert(activityCredentialClaimDisplay1)

        activityVerifierDao.deleteById(activityVerifier1.id)

        assertNotNull(
            credentialDao.getById(credential1.id)
        )
        assertNotNull(
            activityDao.getLatestActivityFlow().firstOrNull()
        )
        assertNull(
            activityVerifierDao.getById(activityVerifier1.id)
        )
        assertNull(
            activityVerifierCredentialClaimDao.getById(activityClaim1.id)
        )
        assertNull(
            activityVerifierCredentialClaimDisplayDao.getById(activityCredentialClaimDisplay1.id)
        )
    }

    @Test
    fun deletingActivityShouldCascadeDeletionTest() = runTest {
        credentialDao.insert(credential1)
        activityDao.insert(activity2)
        activityVerifierDao.insert(activityVerifier1)
        activityVerifierCredentialClaimDao.insert(activityClaim1)
        activityVerifierCredentialClaimDisplayDao.insert(activityCredentialClaimDisplay1)

        activityDao.deleteById(activity2.id)

        assertNotNull(
            credentialDao.getById(credential1.id)
        )
        assertNull(
            activityDao.getLatestActivityFlow().firstOrNull()
        )
        assertNull(
            activityVerifierDao.getById(activityVerifier1.id)
        )
        assertNull(
            activityVerifierCredentialClaimDao.getById(activityClaim1.id)
        )
        assertNull(
            activityVerifierCredentialClaimDisplayDao.getById(activityCredentialClaimDisplay1.id)
        )
    }

    @Test
    fun deletingCredentialShouldCascadeDeletionTest() = runTest {
        credentialDao.insert(credential1)
        activityDao.insert(activity2)
        activityVerifierDao.insert(activityVerifier1)
        activityVerifierCredentialClaimDao.insert(activityClaim1)
        activityVerifierCredentialClaimDisplayDao.insert(activityCredentialClaimDisplay1)

        credentialDao.deleteById(credential1.id)

        assertNull(credentialDao.getById(credential1.id))
        assertNull(activityDao.getLatestActivityFlow().firstOrNull())
        assertNull(activityVerifierDao.getById(activityVerifier1.id))
        assertNull(activityVerifierCredentialClaimDao.getById(activityClaim1.id))
        assertNull(activityVerifierCredentialClaimDisplayDao.getById(activityCredentialClaimDisplay1.id))
    }
}
