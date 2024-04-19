package ch.admin.foitt.pilotwallet.platform.ssi.data.source.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.pilotwallet.platform.database.domain.AppDatabase
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential2
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class CredentialDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var credentialDao: CredentialDao

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        credentialDao = database.credentialDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertCredentialTest() = runTest {
        val id = credentialDao.insert(credential1)

        var credentials = credentialDao.getAll()
        assertEquals("There is 1 credential in the db", 1, credentials.size)
        assertEquals(credentials.first(), credentialDao.getById(id))

        val updatedId = credentialDao.insert(credential1)
        credentials = credentialDao.getAll()
        assertEquals("Inserting the same credential replaces the old one", 1, credentials.size)
        assertEquals(credentials.first(), credentialDao.getById(updatedId))
    }

    @Test
    fun updateCredentialTest() = runTest {
        val id = credentialDao.insert(credential1)

        val updatedCredential = credential1.copy(updatedAt = 2L)

        val updatedId = credentialDao.update(updatedCredential).toLong()
        assertEquals("Updating should not change the id", updatedId, id)

        val credentials = credentialDao.getAll()
        assertEquals("Updating does not create a new credential", 1, credentials.size)
        assertEquals(
            "Credential should be updated",
            updatedCredential.copy(id = id),
            credentials.first()
        )
    }

    @Test
    fun deleteCredentialTest() = runTest {
        val id1 = credentialDao.insert(credential1)
        val id2 = credentialDao.insert(credential2)

        credentialDao.deleteById(id1)
        assertEquals("There is 1 credential in the db", 1, credentialDao.getAll().size)
        assertTrue("Only Credential 2 is in the db", credentialDao.getAll().all { it == credential2 })

        credentialDao.deleteById(id2)
        assertEquals("There is no credential in the db", 0, credentialDao.getAll().size)
    }
}
