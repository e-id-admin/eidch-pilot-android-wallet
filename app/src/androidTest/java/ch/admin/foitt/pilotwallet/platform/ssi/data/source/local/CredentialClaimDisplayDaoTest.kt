package ch.admin.foitt.pilotwallet.platform.ssi.data.source.local

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialClaimDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialClaimDisplayDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.pilotwallet.platform.database.domain.AppDatabase
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaimDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DisplayLanguage
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential2
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credentialClaim1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credentialClaim2
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credentialClaimDisplay1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credentialClaimDisplay2
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.name1
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class CredentialClaimDisplayDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var credentialDao: CredentialDao
    private lateinit var credentialClaimDao: CredentialClaimDao
    private lateinit var credentialClaimDisplayDao: CredentialClaimDisplayDao

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
        credentialClaimDao.insert(credentialClaim1)
        credentialClaimDao.insert(credentialClaim2)
        credentialClaimDisplayDao = database.credentialClaimDisplayDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertCredentialClaimDisplayTest() = runTest {
        val credentialClaimDisplays = listOf(
            credentialClaimDisplay1,
            credentialClaimDisplay2
        )
        credentialClaimDisplayDao.insertAll(credentialClaimDisplays)

        assertEquals(
            credentialClaimDisplay1,
            credentialClaimDisplayDao.getByClaimIdAndLanguage(credentialClaim1.id, "xx")
        )

        assertEquals(
            credentialClaimDisplay2,
            credentialClaimDisplayDao.getByClaimIdAndLanguage(credentialClaim2.id, "xx")
        )

        assertNull(
            credentialClaimDisplayDao.getByClaimIdAndLanguage(credentialClaim1.id, "YY")
        )

        assertNull(
            credentialClaimDisplayDao.getByClaimIdAndLanguage(-1, "YY")
        )
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insertWithoutMatchingForeignKeyShouldThrow() {
        credentialClaimDisplayDao.insertAll(
            listOf(
                CredentialClaimDisplay(id = 1, claimId = -1, name = name1, locale = "xx_XX")
            )
        )
    }

    @Test
    fun shouldReturnFallbackLocale() = runTest {
        credentialClaimDisplayDao.insertAll(
            listOf(
                CredentialClaimDisplay(claimId = credentialClaim1.id, name = "CORRECT", locale = "xx_XX"),
                CredentialClaimDisplay(claimId = credentialClaim1.id, name = "FALLBACK", locale = DisplayLanguage.FALLBACK),
            )
        )

        assertEquals(
            "Should return localized CredentialClaimDisplay if locale in db starts with requested language",
            "CORRECT",
            credentialClaimDisplayDao.getByClaimIdAndLanguage(claimId = credentialClaim1.id, language = "xx")?.name
        )

        assertEquals(
            "Should return localized CredentialClaimDisplay if locale in db matches requested language",
            "CORRECT",
            credentialClaimDisplayDao.getByClaimIdAndLanguage(claimId = credentialClaim1.id, language = "xx_XX")?.name
        )

        assertEquals(
            "Should return fallback CredentialClaimDisplay if requested language is not in db",
            "FALLBACK",
            credentialClaimDisplayDao.getByClaimIdAndLanguage(claimId = credentialClaim1.id, language = "yy")?.name
        )
    }

    @Test
    fun deletingCredentialShouldCascadeDeletion() = runTest {
        val credentialClaimDisplay1 = CredentialClaimDisplay(id = 1, claimId = credentialClaim1.id, name = name1, locale = "xx")
        val credentialClaimDisplay2 = CredentialClaimDisplay(id = 2, claimId = credentialClaim2.id, name = name1, locale = "xx_XX")

        val credentialClaimDisplays = listOf(
            credentialClaimDisplay1,
            credentialClaimDisplay2
        )
        credentialClaimDisplayDao.insertAll(credentialClaimDisplays)

        assertEquals(
            credentialClaimDisplay1,
            credentialClaimDisplayDao.getByClaimIdAndLanguage(credentialClaim1.id, "xx")
        )

        credentialDao.deleteById(credential1.id)
        assertNull(
            "CredentialClaimDisplay with foreign key for credential 1 should be deleted",
            credentialClaimDisplayDao.getByClaimIdAndLanguage(credentialClaim1.id, "xx")
        )

        assertEquals(
            "CredentialClaimDisplay with foreign key for credential 2 should not be deleted",
            credentialClaimDisplay2,
            credentialClaimDisplayDao.getByClaimIdAndLanguage(credentialClaim2.id, "xx")
        )
    }
}
