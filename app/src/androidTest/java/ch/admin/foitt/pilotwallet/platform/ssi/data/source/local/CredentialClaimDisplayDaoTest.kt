package ch.admin.foitt.pilotwallet.platform.ssi.data.source.local

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialClaimDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialClaimDisplayDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.pilotwallet.platform.database.domain.AppDatabase
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaimDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DisplayLanguage
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.NAME1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credential2
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credentialClaim1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credentialClaim2
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credentialClaimDisplay1
import ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock.CredentialTestData.credentialClaimDisplay2
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CredentialClaimDisplayDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var credentialDao: CredentialDao
    private lateinit var credentialClaimDao: CredentialClaimDao
    private lateinit var credentialClaimDisplayDao: CredentialClaimDisplayDao

    @BeforeEach
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).allowMainThreadQueries().build()

        credentialDao = database.credentialDao()
        credentialDao.insert(credential1)
        credentialDao.insert(credential2)

        credentialClaimDao = database.credentialClaimDao()
        credentialClaimDao.insert(credentialClaim1)
        credentialClaimDao.insert(credentialClaim2)
        credentialClaimDisplayDao = database.credentialClaimDisplayDao()
    }

    @AfterEach
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertCredentialClaimDisplayTest() = runTest {
        val credentialClaimDisplays = listOf(
            credentialClaimDisplay1, credentialClaimDisplay2
        )
        credentialClaimDisplayDao.insertAll(credentialClaimDisplays)

        assertEquals(credentialClaimDisplay1, credentialClaimDisplayDao.getByClaimIdAndLanguage(credentialClaim1.id, "xx"))
        assertEquals(credentialClaimDisplay2, credentialClaimDisplayDao.getByClaimIdAndLanguage(credentialClaim2.id, "xx"))
        Assertions.assertNull(credentialClaimDisplayDao.getByClaimIdAndLanguage(credentialClaim1.id, "YY"))
        Assertions.assertNull(credentialClaimDisplayDao.getByClaimIdAndLanguage(-1, "YY"))
    }

    @Test
    fun insertWithoutMatchingForeignKeyShouldThrow() {
        assertThrows<SQLiteConstraintException> {
            credentialClaimDisplayDao.insertAll(
                listOf(
                    CredentialClaimDisplay(id = 1, claimId = -1, name = NAME1, locale = "xx_XX")
                )
            )
        }
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
            "CORRECT",
            credentialClaimDisplayDao.getByClaimIdAndLanguage(claimId = credentialClaim1.id, language = "xx")?.name,
            "Should return localized CredentialClaimDisplay if locale in db starts with requested language"
        )

        assertEquals(
            "CORRECT",
            credentialClaimDisplayDao.getByClaimIdAndLanguage(claimId = credentialClaim1.id, language = "xx_XX")?.name,
            "Should return localized CredentialClaimDisplay if locale in db matches requested language"
        )

        assertEquals(
            "FALLBACK",
            credentialClaimDisplayDao.getByClaimIdAndLanguage(claimId = credentialClaim1.id, language = "yy")?.name,
            "Should return fallback CredentialClaimDisplay if requested language is not in db"
        )
    }

    @Test
    fun deletingCredentialShouldCascadeDeletion() = runTest {
        val credentialClaimDisplay1 = CredentialClaimDisplay(id = 1, claimId = credentialClaim1.id, name = NAME1, locale = "xx")
        val credentialClaimDisplay2 = CredentialClaimDisplay(id = 2, claimId = credentialClaim2.id, name = NAME1, locale = "xx_XX")

        val credentialClaimDisplays = listOf(
            credentialClaimDisplay1, credentialClaimDisplay2
        )
        credentialClaimDisplayDao.insertAll(credentialClaimDisplays)

        assertEquals(credentialClaimDisplay1, credentialClaimDisplayDao.getByClaimIdAndLanguage(credentialClaim1.id, "xx"))

        credentialDao.deleteById(credential1.id)
        Assertions.assertNull(
            credentialClaimDisplayDao.getByClaimIdAndLanguage(credentialClaim1.id, "xx"),
            "CredentialClaimDisplay with foreign key for credential 1 should be deleted"
        )

        assertEquals(
            credentialClaimDisplay2,
            credentialClaimDisplayDao.getByClaimIdAndLanguage(credentialClaim2.id, "xx"),
            "CredentialClaimDisplay with foreign key for credential 2 should not be deleted"
        )
    }
}
