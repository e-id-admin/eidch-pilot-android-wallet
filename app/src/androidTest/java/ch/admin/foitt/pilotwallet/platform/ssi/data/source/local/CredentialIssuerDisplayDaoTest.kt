package ch.admin.foitt.pilotwallet.platform.ssi.data.source.local

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialIssuerDisplayDao
import ch.admin.foitt.pilotwallet.platform.database.domain.AppDatabase
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialIssuerDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DisplayLanguage
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.CORRECT
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.FALLBACK
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential2
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credentialIssuerDisplay1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credentialIssuerDisplay2
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CredentialIssuerDisplayDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var credentialDao: CredentialDao
    private lateinit var credentialIssuerDisplayDao: CredentialIssuerDisplayDao

    @BeforeEach
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).allowMainThreadQueries().build()

        credentialDao = database.credentialDao()
        credentialDao.insert(credential1)
        credentialDao.insert(credential2)

        credentialIssuerDisplayDao = database.credentialIssuerDisplayDao()
    }

    @AfterEach
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertWithoutMatchingForeignKeyShouldThrow() = runTest {
        assertThrows<SQLiteConstraintException> {
            credentialIssuerDisplayDao.insertAll(listOf(credentialIssuerDisplay1.copy(credentialId = -1)))
        }
    }

    @Test
    fun insertAllCredentialIssuerTest() = runTest {
        credentialIssuerDisplayDao.insertAll(listOf(credentialIssuerDisplay1, credentialIssuerDisplay2))

        assertEquals(
            credentialIssuerDisplay1.name,
            credentialIssuerDisplayDao.getByCredentialIdAndLanguage(credentialId = credential1.id, language = "xx")?.name
        )
    }

    @Test
    fun shouldReturnFallbackLocale() = runTest {
        val credentialIssuerDisplays = listOf(
            CredentialIssuerDisplay(id = 1, credentialId = credential1.id, name = CORRECT, locale = "xx_XX"),
            CredentialIssuerDisplay(id = 2, credentialId = credential2.id, name = FALLBACK, locale = DisplayLanguage.FALLBACK)
        )
        credentialIssuerDisplayDao.insertAll(credentialIssuerDisplays)

        assertEquals(
            CORRECT,
            credentialIssuerDisplayDao.getByCredentialIdAndLanguage(credentialId = credential1.id, language = "xx")?.name,
            "Should return localized IssuerDisplay if locale in db starts with requested language"
        )

        assertEquals(
            CORRECT,
            credentialIssuerDisplayDao.getByCredentialIdAndLanguage(credentialId = credential1.id, language = "xx_XX")?.name,
            "Should return localized IssuerDisplay if locale in db matches requested language"
        )

        assertEquals(
            FALLBACK,
            credentialIssuerDisplayDao.getByCredentialIdAndLanguage(credentialId = credential2.id, language = "yy")?.name,
            "Should return fallback IssuerDisplay if requested language is not in db"
        )
    }

    @Test
    fun deletingCredentialShouldCascadeDeletion() = runTest {
        credentialIssuerDisplayDao.insertAll(listOf(credentialIssuerDisplay1, credentialIssuerDisplay2))

        credentialDao.deleteById(credential1.id)
        assertNull(
            credentialIssuerDisplayDao.getByCredentialIdAndLanguage(credentialId = credential1.id, "xx"),
            "CredentialIssuer with foreign key for credential 1 should be deleted"
        )
        assertEquals(
            2L,
            credentialIssuerDisplayDao.getByCredentialIdAndLanguage(credentialId = credential2.id, "xx")?.id,
            "CredentialIssuer with foreign key for credential 2 should not be deleted"
        )
    }
}
