package ch.admin.foitt.pilotwallet.platform.ssi.data.source.local

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialIssuerDisplayDao
import ch.admin.foitt.pilotwallet.platform.database.domain.AppDatabase
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialIssuerDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DisplayLanguage
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.correct
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential2
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credentialIssuerDisplay1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credentialIssuerDisplay2
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.fallback
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class CredentialIssuerDisplayDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var credentialDao: CredentialDao
    private lateinit var credentialIssuerDisplayDao: CredentialIssuerDisplayDao

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        credentialDao = database.credentialDao()
        credentialDao.insert(credential1)
        credentialDao.insert(credential2)

        credentialIssuerDisplayDao = database.credentialIssuerDisplayDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insertWithoutMatchingForeignKeyShouldThrow() = runTest {
        credentialIssuerDisplayDao.insertAll(listOf(credentialIssuerDisplay1.copy(credentialId = -1)))
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
            CredentialIssuerDisplay(id = 1, credentialId = credential1.id, name = correct, locale = "xx_XX"),
            CredentialIssuerDisplay(id = 2, credentialId = credential2.id, name = fallback, locale = DisplayLanguage.FALLBACK)
        )
        credentialIssuerDisplayDao.insertAll(credentialIssuerDisplays)

        assertEquals(
            "Should return localized IssuerDisplay if locale in db starts with requested language",
            correct,
            credentialIssuerDisplayDao.getByCredentialIdAndLanguage(credentialId = credential1.id, language = "xx")?.name
        )

        assertEquals(
            "Should return localized IssuerDisplay if locale in db matches requested language",
            correct,
            credentialIssuerDisplayDao.getByCredentialIdAndLanguage(credentialId = credential1.id, language = "xx_XX")?.name
        )

        assertEquals(
            "Should return fallback IssuerDisplay if requested language is not in db",
            fallback,
            credentialIssuerDisplayDao.getByCredentialIdAndLanguage(credentialId = credential2.id, language = "yy")?.name
        )
    }

    @Test
    fun deletingCredentialShouldCascadeDeletion() = runTest {
        credentialIssuerDisplayDao.insertAll(listOf(credentialIssuerDisplay1, credentialIssuerDisplay2))

        credentialDao.deleteById(credential1.id)
        assertNull(
            "CredentialIssuer with foreign key for credential 1 should be deleted",
            credentialIssuerDisplayDao.getByCredentialIdAndLanguage(credentialId = credential1.id, "xx")
        )
        assertEquals(
            "CredentialIssuer with foreign key for credential 2 should not be deleted",
            2L,
            credentialIssuerDisplayDao.getByCredentialIdAndLanguage(credentialId = credential2.id, "xx")?.id
        )
    }
}
