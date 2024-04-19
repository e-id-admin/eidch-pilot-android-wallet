package ch.admin.foitt.pilotwallet.platform.database.domain.model

import android.content.Context
import androidx.annotation.CheckResult
import androidx.room.Room
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialClaimDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialClaimDisplayDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDisplayDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialIssuerDisplayDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialRawDao
import ch.admin.foitt.pilotwallet.platform.database.domain.AppDatabase
import ch.admin.foitt.pilotwallet.platform.di.IoDispatcherScope
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import net.zetetic.database.sqlcipher.SQLiteDatabase
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("TooManyFunctions")
@Singleton
class DatabaseWrapper @Inject constructor(
    @IoDispatcherScope private val coroutineScope: CoroutineScope,
) {
    private val databaseMutex = Mutex()
    private val database: MutableStateFlow<AppDatabase?> = MutableStateFlow(null)

    private val decryptionTestDao: Flow<AppDatabase.DecryptionTestDao?> = getDaoFlow { it?.decryptionTestDao() }
    val databaseStatus = combine(database, decryptionTestDao) { currentDatabase, currentDao ->
        when {
            currentDatabase != null && currentDatabase.isOpen && currentDao != null -> DatabaseState.OPEN
            else -> DatabaseState.CLOSED
        }
    }
        .stateIn(
            coroutineScope,
            SharingStarted.Eagerly,
            initialValue = DatabaseState.CLOSED,
        )

    init {
        Timber.d("Load sqlcipher library")
        System.loadLibrary("sqlcipher")
    }

    suspend fun createDatabase(
        context: Context,
        passphrase: ByteArray,
    ): Result<Unit, CreateDatabaseError> = withContext(Dispatchers.IO) {
        databaseMutex.withLock(this) {
            coroutineBinding {
                val buildResult = buildDatabase(context = context, factory = SupportOpenHelperFactory(passphrase))
                val createdDb = buildResult.bind()

                // The database is not actually written as long as we don't do anything with it.
                tryDecrypt(createdDb).mapError { error ->
                    createdDb.close()
                    DatabaseError.SetupFailed(error.throwable)
                }.bind()

                database.update { createdDb }
                buildResult.map {}
            }
        }
    }

    suspend fun close() {
        databaseMutex.withLock(this) {
            Timber.d("Close database")
            database.value?.close()
            database.update { null }
        }
    }

    suspend fun open(
        context: Context,
        passphrase: ByteArray,
    ): Result<Unit, OpenDatabaseError> = withContext(Dispatchers.IO) {
        databaseMutex.withLock(this) {
            Timber.d("Try to open database")
            coroutineBinding {
                if (database.value != null) Err(DatabaseError.AlreadyOpen).bind<OpenDatabaseError>()
                val db = buildDatabase(context = context, factory = SupportOpenHelperFactory(passphrase)).bind()

                tryDecrypt(db)
                    .onSuccess {
                        database.update { db }
                    }.onFailure {
                        db.close()
                        database.update { null }
                    }.bind()
            }
        }
    }

    @CheckResult
    suspend fun checkIfCorrectPassphrase(
        context: Context,
        passphrase: ByteArray
    ): Result<Unit, OpenDatabaseError> = withContext(Dispatchers.IO) {
        databaseMutex.withLock(this) {
            coroutineBinding {
                val db = buildDatabase(context = context, factory = SupportOpenHelperFactory(passphrase)).bind()
                tryDecrypt(db)
                    .onFailure {
                        // do not close the current instance at this point
                        Timber.d("wrong passphrase")
                    }.bind()
                db.close()
            }
        }
    }

    @CheckResult
    suspend fun changePassphrase(newPassphrase: ByteArray): Result<Unit, ChangeDatabasePassphraseError> = withContext(Dispatchers.IO) {
        runSuspendCatching {
            databaseMutex.withLock(this) {
                val db = database.value?.openHelper?.writableDatabase as SQLiteDatabase
                db.changePassword(newPassphrase)
            }
        }.mapError { throwable ->
            DatabaseError.ReKeyFailed(throwable)
        }
    }

    @CheckResult
    internal fun isOpen(): Boolean {
        Timber.d("Try to get open database")
        return database.value != null
    }

    private suspend fun tryDecrypt(db: AppDatabase): Result<Unit, DatabaseError.WrongPassphrase> {
        return runSuspendCatching {
            db.decryptionTestDao().test()
            Unit
        }.mapError { throwable ->
            Timber.d(message = "Error", t = throwable)
            DatabaseError.WrongPassphrase(throwable)
        }
    }

    private fun buildDatabase(context: Context, factory: SupportOpenHelperFactory): Result<AppDatabase, DatabaseError.SetupFailed> {
        return runSuspendCatching {
            Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .openHelperFactory(factory)
                .build()
        }.mapError { throwable ->
            DatabaseError.SetupFailed(throwable)
        }
    }

    private fun <T> getDaoFlow(mapper: (AppDatabase?) -> T?): StateFlow<T?> = database.map { mapper(it) }.stateIn(
        coroutineScope,
        SharingStarted.Eagerly,
        initialValue = null,
    )

    //region DAOs
    val credentialDaoFlow: StateFlow<CredentialDao?> = getDaoFlow { it?.credentialDao() }
    val credentialDisplayDaoFlow: StateFlow<CredentialDisplayDao?> = getDaoFlow { it?.credentialDisplayDao() }
    val credentialClaimDao: StateFlow<CredentialClaimDao?> = getDaoFlow { it?.credentialClaimDao() }
    val credentialClaimDisplayDao: StateFlow<CredentialClaimDisplayDao?> =
        getDaoFlow { it?.credentialClaimDisplayDao() }
    val credentialIssuerDisplayDao: StateFlow<CredentialIssuerDisplayDao?> =
        getDaoFlow { it?.credentialIssuerDisplayDao() }
    val credentialRawDao: StateFlow<CredentialRawDao?> = getDaoFlow { it?.credentialRawDao() }
    //endregion

    companion object {
        private const val DATABASE_NAME = "app_database.db"
    }
}
