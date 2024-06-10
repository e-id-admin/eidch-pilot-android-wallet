package ch.admin.foitt.pilotwallet.platform.database.domain.repository

import androidx.annotation.CheckResult
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
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ChangeDatabasePassphraseError
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CreateDatabaseError
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseState
import ch.admin.foitt.pilotwallet.platform.database.domain.model.OpenDatabaseError
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.StateFlow

interface DatabaseRepository {
    val databaseState: StateFlow<DatabaseState>

    @CheckResult
    suspend fun createDatabase(passphrase: ByteArray): Result<Unit, CreateDatabaseError>

    suspend fun close()

    @CheckResult
    suspend fun open(passphrase: ByteArray): Result<Unit, OpenDatabaseError>

    @CheckResult
    suspend fun checkIfCorrectPassphrase(passphrase: ByteArray): Result<Unit, OpenDatabaseError>

    @CheckResult
    suspend fun changePassphrase(newPassphrase: ByteArray): Result<Unit, ChangeDatabasePassphraseError>

    @CheckResult
    fun isOpen(): Boolean

    //region DAOs
    val credentialDaoFlow: StateFlow<CredentialDao?>
    val credentialDisplayDaoFlow: StateFlow<CredentialDisplayDao?>
    val credentialClaimDao: StateFlow<CredentialClaimDao?>
    val credentialClaimDisplayDao: StateFlow<CredentialClaimDisplayDao?>
    val credentialIssuerDisplayDao: StateFlow<CredentialIssuerDisplayDao?>
    val credentialRawDao: StateFlow<CredentialRawDao?>
    val activityDao: StateFlow<ActivityDao?>
    val activityVerifierDao: StateFlow<ActivityVerifierDao?>
    val activityVerifierCredentialClaimDao: StateFlow<ActivityVerifierCredentialClaimDao?>
    val activityVerifierCredentialClaimDisplayDao: StateFlow<ActivityVerifierCredentialClaimDisplayDao?>
    //endregion
}
