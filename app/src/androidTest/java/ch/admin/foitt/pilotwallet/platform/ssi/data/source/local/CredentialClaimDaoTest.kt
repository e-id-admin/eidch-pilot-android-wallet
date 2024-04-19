package ch.admin.foitt.pilotwallet.platform.ssi.data.source.local

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialClaimDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.pilotwallet.platform.database.domain.AppDatabase
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential2
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.key
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.value
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class CredentialClaimDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var credentialDao: CredentialDao
    private lateinit var credentialClaimDao: CredentialClaimDao

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        credentialDao = database.credentialDao()
        credentialDao.insert(credential1)
        credentialDao.insert(credential2)
        credentialClaimDao = database.credentialClaimDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertCredentialClaimTest() = runTest {
        assertEquals(
            emptyList<CredentialClaim>(),
            credentialClaimDao.getByCredentialId(credentialId = credential1.id)
        )

        val credentialClaim =
            CredentialClaim(id = 1, credentialId = credential1.id, key = key, value = value, valueType = null)
        val id = credentialClaimDao.insert(credentialClaim)

        assertEquals(
            listOf(credentialClaim.copy(id = id)),
            credentialClaimDao.getByCredentialId(credentialId = credential1.id)
        )

        credentialClaimDao.insert(credentialClaim.copy(id = 0))
        assertEquals(
            2,
            credentialClaimDao.getByCredentialId(credentialId = credential1.id).size
        )
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insertWithoutMatchingForeignKeyShouldThrow() {
        val credentialClaim =
            CredentialClaim(id = 1, credentialId = -1, key = key, value = value, valueType = null)
        credentialClaimDao.insert(credentialClaim)
    }

    @Test
    fun deletingCredentialShouldCascadeDeletion() = runTest {
        val credentialClaim1 = CredentialClaim(
            id = 1,
            credentialId = credential1.id,
            key = key,
            value = value,
            valueType = null
        )
        val id1 = credentialClaimDao.insert(credentialClaim1)

        val credentialClaim2 = CredentialClaim(
            id = 2,
            credentialId = credential2.id,
            key = key,
            value = value,
            valueType = null
        )
        val id2 = credentialClaimDao.insert(credentialClaim2)

        assertEquals(
            "CredentialClaim with id1 should be retrievable",
            listOf(credentialClaim1.copy(id = id1)),
            credentialClaimDao.getByCredentialId(credentialId = credential1.id)
        )
        credentialDao.deleteById(credential1.id)
        assertEquals(
            "CredentialClaim with foreign key for credential 1 should be deleted",
            emptyList<CredentialClaim>(),
            credentialClaimDao.getByCredentialId(credentialId = credential1.id)
        )
        assertEquals(
            "CredentialClaim with foreign key for credential 2 should not be deleted",
            listOf(credentialClaim2.copy(id = id2)),
            credentialClaimDao.getByCredentialId(credentialId = credential2.id)
        )
    }
}
