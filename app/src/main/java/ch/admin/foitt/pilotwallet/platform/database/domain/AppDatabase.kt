@file:Suppress("TooManyFunctions")

package ch.admin.foitt.pilotwallet.platform.database.domain

import androidx.room.Dao
import androidx.room.Database
import androidx.room.RawQuery
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteQuery
import ch.admin.foitt.pilotwallet.platform.database.data.dao.ActivityDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.ActivityVerifierCredentialClaimDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.ActivityVerifierCredentialClaimDisplayDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.ActivityVerifierDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialClaimDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialClaimDisplayDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDisplayDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialIssuerDisplayDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialRawDao
import ch.admin.foitt.pilotwallet.platform.database.domain.AppDatabase.Companion.DATABASE_VERSION
import ch.admin.foitt.pilotwallet.platform.database.domain.model.Activity
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifier
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifierCredentialClaim
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifierCredentialClaimDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.Credential
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaimDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialIssuerDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialRaw
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseError
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import net.zetetic.database.sqlcipher.SQLiteDatabase
import timber.log.Timber

@Database(
    entities = [
        CredentialRaw::class,
        Credential::class,
        CredentialDisplay::class,
        CredentialClaim::class,
        CredentialClaimDisplay::class,
        CredentialIssuerDisplay::class,
        Activity::class,
        ActivityVerifier::class,
        ActivityVerifierCredentialClaim::class,
        ActivityVerifierCredentialClaimDisplay::class,
    ],
    version = DATABASE_VERSION,
    exportSchema = false
)
@Suppress("TooManyFunctions")
abstract class AppDatabase : RoomDatabase() {
    // each DAO must be defined as an abstract method
    abstract fun credentialDao(): CredentialDao
    abstract fun credentialClaimDao(): CredentialClaimDao
    abstract fun credentialClaimDisplayDao(): CredentialClaimDisplayDao
    abstract fun credentialDisplayDao(): CredentialDisplayDao
    abstract fun credentialIssuerDisplayDao(): CredentialIssuerDisplayDao
    abstract fun credentialRawDao(): CredentialRawDao
    abstract fun activityDao(): ActivityDao
    abstract fun activityVerifierDao(): ActivityVerifierDao
    abstract fun activityVerifierCredentialClaimDao(): ActivityVerifierCredentialClaimDao
    abstract fun activityVerifierCredentialClaimDisplayDao(): ActivityVerifierCredentialClaimDisplayDao

    abstract fun decryptionTestDao(): DecryptionTestDao

    fun changePassword(newPassword: ByteArray): Result<Unit, DatabaseError.ReKeyFailed> =
        runSuspendCatching {
            val database = openHelper.writableDatabase as SQLiteDatabase
            database.changePassword(newPassword)
        }.mapError { throwable ->
            DatabaseError.ReKeyFailed(throwable)
        }

    suspend fun tryDecrypt(): Result<Unit, DatabaseError.WrongPassphrase> {
        return runSuspendCatching {
            decryptionTestDao().test()
            Unit
        }.mapError { throwable ->
            Timber.d(message = "Error", t = throwable)
            DatabaseError.WrongPassphrase(throwable)
        }
    }

    @Dao
    interface DecryptionTestDao {
        // Returns an Int if database decryption was successful
        // https://www.zetetic.net/sqlcipher/sqlcipher-api/#testing-the-key
        @RawQuery
        suspend fun test(query: SupportSQLiteQuery = SimpleSQLiteQuery("SELECT count(*) FROM sqlite_master")): Int
    }

    companion object {
        internal const val DATABASE_VERSION = 5
        internal val MIGRATION_4_TO_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `Activity` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`credentialId` INTEGER NOT NULL, " +
                        "`type` TEXT NOT NULL, " +
                        "`credentialSnapshotStatus` TEXT NOT NULL, " +
                        "`createdAt` INTEGER NOT NULL, " +
                        "FOREIGN KEY(`credentialId`) REFERENCES `Credential`(`id`) ON UPDATE CASCADE ON DELETE CASCADE" +
                        ")"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_Activity_credentialId` " +
                        "ON `Activity` (`credentialId`)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `ActivityVerifier` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`activityId` INTEGER NOT NULL, " +
                        "`name` TEXT NOT NULL, " +
                        "`logo` TEXT, " +
                        "FOREIGN KEY(`activityId`) REFERENCES `Activity`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_ActivityVerifier_activityId` " +
                        "ON `ActivityVerifier` (`activityId`)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `ActivityVerifierCredentialClaim` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`activityVerifierId` INTEGER NOT NULL, " +
                        "`key` TEXT NOT NULL, " +
                        "`value` TEXT NOT NULL, " +
                        "`valueType` TEXT, " +
                        "`order` INTEGER NOT NULL, " +
                        "FOREIGN KEY(`activityVerifierId`) REFERENCES `ActivityVerifier`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_ActivityVerifierCredentialClaim_activityVerifierId` " +
                        "ON `ActivityVerifierCredentialClaim` (`activityVerifierId`)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `ActivityVerifierCredentialClaimDisplay` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`activityClaimId` INTEGER NOT NULL, " +
                        "`name` TEXT NOT NULL, " +
                        "`locale` TEXT NOT NULL, " +
                        "FOREIGN KEY(`activityClaimId`) REFERENCES `ActivityVerifierCredentialClaim`(`id`) " +
                        "ON UPDATE CASCADE ON DELETE CASCADE )"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_ActivityVerifierCredentialClaimDisplay_activityClaimId` " +
                        "ON `ActivityVerifierCredentialClaimDisplay` (`activityClaimId`)"
                )
            }
        }
    }
}
