package ch.admin.foitt.pilotwallet.platform.ssi.data.source.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.pilotwallet.platform.database.domain.AppDatabase
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential2
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CredentialDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var credentialDao: CredentialDao

    @BeforeEach
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).allowMainThreadQueries().build()

        credentialDao = database.credentialDao()
    }

    @AfterEach
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertCredentialTest() = runTest {
        val id = credentialDao.insert(credential1)

        var credentials = credentialDao.getAll()
        assertEquals(1, credentials.size, "There is 1 credential in the db")
        assertEquals(credentials.first(), credentialDao.getById(id))

        val updatedId = credentialDao.insert(credential1)
        credentials = credentialDao.getAll()
        assertEquals(1, credentials.size, "Inserting the same credential replaces the old one")
        assertEquals(credentials.first(), credentialDao.getById(updatedId))
    }

    @Test
    fun updateCredentialTest() = runTest {
        val id = credentialDao.insert(credential1)

        val updatedCredential = credential1.copy(updatedAt = 2L)

        val updatedId = credentialDao.update(updatedCredential).toLong()
        assertEquals(updatedId, id, "Updating should not change the id")

        val credentials = credentialDao.getAll()
        assertEquals(1, credentials.size, "Updating does not create a new credential")
        assertEquals(updatedCredential.copy(id = id), credentials.first(), "Credential should be updated")
    }

    @Test
    fun deleteCredentialTest() = runTest {
        val id1 = credentialDao.insert(credential1)
        val id2 = credentialDao.insert(credential2)

        credentialDao.deleteById(id1)
        assertEquals(1, credentialDao.getAll().size, "There is 1 credential in the db")
        assertTrue(credentialDao.getAll().all { it == credential2 }, "Only Credential 2 is in the db")

        credentialDao.deleteById(id2)
        assertEquals(0, credentialDao.getAll().size, "There is no credential in the db")
    }
}
