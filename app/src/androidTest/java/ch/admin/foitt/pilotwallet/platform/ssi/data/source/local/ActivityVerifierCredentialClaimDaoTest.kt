package ch.admin.foitt.pilotwallet.platform.ssi.data.source.local

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ch.admin.foitt.pilotwallet.platform.database.data.dao.ActivityDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.ActivityVerifierCredentialClaimDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.ActivityVerifierDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.pilotwallet.platform.database.domain.AppDatabase
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.activity2
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.activity3
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.activityClaim1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.activityClaim2
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.activityClaim3
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.activityVerifier1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.activityVerifier2
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

class ActivityVerifierCredentialClaimDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var credentialDao: CredentialDao
    private lateinit var activityDao: ActivityDao
    private lateinit var activityVerifierDao: ActivityVerifierDao
    private lateinit var activityVerifierCredentialClaimDao: ActivityVerifierCredentialClaimDao

    @BeforeEach
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).allowMainThreadQueries().build()

        credentialDao = database.credentialDao()
        activityDao = database.activityDao()
        activityVerifierDao = database.activityVerifierDao()
        activityVerifierCredentialClaimDao = database.activityVerifierCredentialClaimDao()
    }

    @AfterEach
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertActivityVerifierCredentialClaimWithoutActivityVerifierTest() = runTest {
        assertThrows<SQLiteConstraintException> {
            activityVerifierCredentialClaimDao.insert(activityClaim1)
        }
    }

    @Test
    fun insertActivityVerifierCredentialClaimTest() = runTest {
        credentialDao.insert(credential1)
        activityDao.insert(activity2)
        activityVerifierDao.insert(activityVerifier1)
        val id = activityVerifierCredentialClaimDao.insert(activityClaim1)

        assertEquals(activityClaim1.id, activityVerifierCredentialClaimDao.getById(id)?.id)
        assertEquals(activityClaim1.activityVerifierId, activityVerifierCredentialClaimDao.getById(id)?.activityVerifierId)
        assertEquals(activityClaim1.key, activityVerifierCredentialClaimDao.getById(id)?.key)
        assertEquals(activityClaim1.value, activityVerifierCredentialClaimDao.getById(id)?.value)
        assertEquals(activityClaim1.valueType, activityVerifierCredentialClaimDao.getById(id)?.valueType)
        assertEquals(activityClaim1.order, activityVerifierCredentialClaimDao.getById(id)?.order)
    }

    @Test
    fun getByVerifierIdTest() = runTest {
        credentialDao.insert(credential1)
        activityDao.insert(activity2)
        activityDao.insert(activity3)
        activityVerifierDao.insert(activityVerifier1)
        activityVerifierDao.insert(activityVerifier2)
        activityVerifierCredentialClaimDao.insert(activityClaim1)
        activityVerifierCredentialClaimDao.insert(activityClaim2)
        activityVerifierCredentialClaimDao.insert(activityClaim3)

        val claimsOfVerifier1 = activityVerifierCredentialClaimDao.getByVerifierId(activityVerifier1.id)

        assertEquals(
            listOf(activityClaim1, activityClaim2),
            claimsOfVerifier1
        )
    }

    @Test
    fun deleteActivityCredentialClaimTest() = runTest {
        credentialDao.insert(credential1)
        activityDao.insert(activity2)
        activityVerifierDao.insert(activityVerifier1)
        activityVerifierCredentialClaimDao.insert(activityClaim1)

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
    }

    @Test
    fun deletingActivityVerifierShouldCascadeDeletionTest() = runTest {
        credentialDao.insert(credential1)
        activityDao.insert(activity2)
        activityVerifierDao.insert(activityVerifier1)
        activityVerifierCredentialClaimDao.insert(activityClaim1)

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
    }

    @Test
    fun deletingActivityShouldCascadeDeletionTest() = runTest {
        credentialDao.insert(credential1)
        activityDao.insert(activity2)
        activityVerifierDao.insert(activityVerifier1)
        activityVerifierCredentialClaimDao.insert(activityClaim1)

        activityDao.deleteById(activity2.id)

        assertEquals(
            credentialDao.getById(credential1.id),
            credential1
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
    }

    @Test
    fun deletingCredentialShouldCascadeDeletionTest() = runTest {
        credentialDao.insert(credential1)
        activityDao.insert(activity2)
        activityVerifierDao.insert(activityVerifier1)
        activityVerifierCredentialClaimDao.insert(activityClaim1)

        credentialDao.deleteById(credential1.id)

        assertNull(
            credentialDao.getById(1)
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
    }
}
