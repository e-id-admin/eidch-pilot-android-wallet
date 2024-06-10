package ch.admin.foitt.pilotwallet.platform.database.data.repository

import android.content.Context
import androidx.room.Room
import ch.admin.foitt.pilotwallet.platform.database.domain.AppDatabase
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseError
import ch.admin.foitt.pilotwallet.platform.database.domain.repository.SetupDatabaseRepository
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import dagger.hilt.android.qualifiers.ApplicationContext
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import javax.inject.Inject

class SetupSqlCipherDatabaseRepository @Inject constructor(
    @ApplicationContext private val appContext: Context
) : SetupDatabaseRepository {

    override fun create(password: ByteArray): Result<AppDatabase, DatabaseError.SetupFailed> =
        runSuspendCatching {
            Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
                .openHelperFactory(SupportOpenHelperFactory(password))
                .addMigrations(AppDatabase.MIGRATION_4_TO_5)
                .build()
        }.mapError { throwable ->
            DatabaseError.SetupFailed(throwable)
        }

    companion object {
        private const val DATABASE_NAME = "app_database.db"

        init {
            System.loadLibrary("sqlcipher")
        }
    }
}
