package ch.admin.foitt.pilotwallet.platform.ssi.data.source.local

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialClaimDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.pilotwallet.platform.database.domain.AppDatabase
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.KEY
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.VALUE
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential2
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CredentialClaimDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var credentialDao: CredentialDao
    private lateinit var credentialClaimDao: CredentialClaimDao

    @BeforeEach
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).allowMainThreadQueries().build()

        credentialDao = database.credentialDao()
        credentialDao.insert(credential1)
        credentialDao.insert(credential2)
        credentialClaimDao = database.credentialClaimDao()
    }

    @AfterEach
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
            CredentialClaim(id = 1, credentialId = credential1.id, key = KEY, value = VALUE, valueType = null)
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

    @Test
    fun insertWithoutMatchingForeignKeyShouldThrow() {
        assertThrows<SQLiteConstraintException> {
            val credentialClaim = CredentialClaim(id = 1, credentialId = -1, key = KEY, value = VALUE, valueType = null)
            credentialClaimDao.insert(credentialClaim)
        }
    }

    @Test
    fun deletingCredentialShouldCascadeDeletion() = runTest {
        val credentialClaim1 = CredentialClaim(
            id = 1,
            credentialId = credential1.id,
            key = KEY,
            value = VALUE,
            valueType = null
        )
        val id1 = credentialClaimDao.insert(credentialClaim1)

        val credentialClaim2 = CredentialClaim(
            id = 2,
            credentialId = credential2.id,
            key = KEY,
            value = VALUE,
            valueType = null
        )
        val id2 = credentialClaimDao.insert(credentialClaim2)

        assertEquals(
            listOf(credentialClaim1.copy(id = id1)),
            credentialClaimDao.getByCredentialId(credentialId = credential1.id),
            "CredentialClaim with id1 should be retrievable"
        )
        credentialDao.deleteById(credential1.id)
        assertEquals(
            emptyList<CredentialClaim>(),
            credentialClaimDao.getByCredentialId(credentialId = credential1.id),
            "CredentialClaim with foreign key for credential 1 should be deleted"
        )
        assertEquals(
            listOf(credentialClaim2.copy(id = id2)),
            credentialClaimDao.getByCredentialId(credentialId = credential2.id),
            "CredentialClaim with foreign key for credential 2 should not be deleted"
        )
    }
}
