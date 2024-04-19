package ch.admin.foitt.pilotwallet.platform.ssi.data.source.local

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialRawDao
import ch.admin.foitt.pilotwallet.platform.database.domain.AppDatabase
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential2
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credentialRaw1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credentialRaw2
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class CredentialRawDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var credentialDao: CredentialDao
    private lateinit var credentialRawDao: CredentialRawDao

    @Before
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

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertCredentialRawTest() = runTest {
        credentialRawDao.insert(credentialRaw1)

        assertTrue(
            credentialRawDao.getByCredentialId(credentialId = credential1.id).contains(credentialRaw1)
        )
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insertWithoutMatchingForeignKeyShouldThrow() {
        credentialRawDao.insert(credentialRaw1.copy(credentialId = -1))
    }

    @Test
    fun updateCredentialRawTest() = runTest {
        credentialRawDao.insert(credentialRaw1.copy(keyIdentifier = "original"))

        assertEquals(
            "Before updating credentialRaw has original value",
            "original",
            credentialRawDao.getByCredentialId(1).first().keyIdentifier
        )

        credentialRawDao.insert(credentialRaw1.copy(keyIdentifier = "updated"))

        assertEquals(
            "Inserting updated credentialRaw should replace original",
            "updated",
            credentialRawDao.getByCredentialId(1).first().keyIdentifier
        )
    }

    @Test
    fun deletingCredentialShouldCascadeDeletion() = runTest {
        credentialRawDao.insert(credentialRaw1)
        credentialRawDao.insert(credentialRaw2)

        assertTrue(
            "CredentialRaw with id1 should be retrievable",
            credentialRawDao.getByCredentialId(credentialId = credential1.id).contains(credentialRaw1)
        )
        credentialDao.deleteById(credential1.id)
        assertTrue(
            "CredentialRaw with foreign key for credential 1 should be deleted",
            credentialRawDao.getByCredentialId(credentialId = credential1.id).isEmpty()
        )
        assertTrue(
            "CredentialRaw with foreign key for credential 2 should not be deleted",
            credentialRawDao.getByCredentialId(credentialId = credential2.id).contains(credentialRaw2)
        )
    }
}
