package ch.admin.foitt.pilotwallet.platform.database.domain

import androidx.room.Dao
import androidx.room.Database
import androidx.room.RawQuery
import androidx.room.RoomDatabase
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialClaimDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialClaimDisplayDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDisplayDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialIssuerDisplayDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialRawDao
import ch.admin.foitt.pilotwallet.platform.database.domain.AppDatabase.Companion.DATABASE_VERSION
import ch.admin.foitt.pilotwallet.platform.database.domain.model.Credential
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaimDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialIssuerDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialRaw

@Database(
    entities = [
        CredentialRaw::class,
        Credential::class,
        CredentialDisplay::class,
        CredentialClaim::class,
        CredentialClaimDisplay::class,
        CredentialIssuerDisplay::class,
    ],
    version = DATABASE_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // each DAO must be defined as an abstract method
    abstract fun credentialDao(): CredentialDao
    abstract fun credentialClaimDao(): CredentialClaimDao
    abstract fun credentialClaimDisplayDao(): CredentialClaimDisplayDao
    abstract fun credentialDisplayDao(): CredentialDisplayDao
    abstract fun credentialIssuerDisplayDao(): CredentialIssuerDisplayDao
    abstract fun credentialRawDao(): CredentialRawDao

    abstract fun decryptionTestDao(): DecryptionTestDao

    @Dao
    interface DecryptionTestDao {
        // Returns an Int if database decryption was successful
        // https://www.zetetic.net/sqlcipher/sqlcipher-api/#testing-the-key
        @RawQuery
        suspend fun test(query: SupportSQLiteQuery = SimpleSQLiteQuery("SELECT count(*) FROM sqlite_master")): Int
    }

    companion object {
        internal const val DATABASE_VERSION = 4
    }
}
