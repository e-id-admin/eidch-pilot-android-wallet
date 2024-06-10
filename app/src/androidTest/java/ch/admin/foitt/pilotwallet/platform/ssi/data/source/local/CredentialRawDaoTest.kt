package ch.admin.foitt.pilotwallet.platform.ssi.data.source.local

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialRawDao
import ch.admin.foitt.pilotwallet.platform.database.domain.AppDatabase
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential2
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credentialRaw1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credentialRaw2
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CredentialRawDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var credentialDao: CredentialDao
    private lateinit var credentialRawDao: CredentialRawDao

    @BeforeEach
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        credentialDao = database.credentialDao()
        credentialDao.insert(credential = credential1)
        credentialDao.insert(credential = credential2)
        credentialRawDao = database.credentialRawDao()
    }

    @AfterEach
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertCredentialRawTest() = runTest {
        credentialRawDao.insert(credentialRaw1)

        assertTrue(credentialRawDao.getByCredentialId(credentialId = credential1.id).contains(credentialRaw1))
    }

    @Test
    fun insertWithoutMatchingForeignKeyShouldThrow() {
        assertThrows<SQLiteConstraintException> {
            credentialRawDao.insert(credentialRaw1.copy(credentialId = -1))
        }
    }

    @Test
    fun updateCredentialRawTest() = runTest {
        credentialRawDao.insert(credentialRaw1.copy(keyIdentifier = "original"))

        assertEquals(
            "original",
            credentialRawDao.getByCredentialId(1).first().keyIdentifier,
            "Before updating credentialRaw has original value"
        )

        credentialRawDao.insert(credentialRaw1.copy(keyIdentifier = "updated"))

        assertEquals(
            "updated",
            credentialRawDao.getByCredentialId(1).first().keyIdentifier,
            "Inserting updated credentialRaw should replace original"
        )
    }

    @Test
    fun deletingCredentialShouldCascadeDeletion() = runTest {
        credentialRawDao.insert(credentialRaw1)
        credentialRawDao.insert(credentialRaw2)

        assertTrue(
            credentialRawDao.getByCredentialId(credentialId = credential1.id).contains(credentialRaw1),
            "CredentialRaw with id1 should be retrievable"
        )
        credentialDao.deleteById(credential1.id)
        assertTrue(
            credentialRawDao.getByCredentialId(credentialId = credential1.id).isEmpty(),
            "CredentialRaw with foreign key for credential 1 should be deleted"
        )
        assertTrue(
            credentialRawDao.getByCredentialId(credentialId = credential2.id).contains(credentialRaw2),
            "CredentialRaw with foreign key for credential 2 should not be deleted"
        )
    }
}
