package ch.admin.foitt.pilotwallet.platform.login.domain.usecase.implementation

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.crypto.domain.model.HashDataError
import ch.admin.foitt.pilotwallet.platform.database.domain.model.OpenDatabaseError
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.OpenAppDatabase
import ch.admin.foitt.pilotwallet.platform.login.domain.model.LoginWithPinError
import ch.admin.foitt.pilotwallet.platform.login.domain.model.toLoginWithPinError
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.LoginWithPin
import ch.admin.foitt.pilotwallet.platform.passphraseHashing.domain.usecase.HashPassphrase
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.model.PepperPassphraseError
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.usecase.PepperPassphrase
import ch.admin.foitt.pilotwallet.platform.userInteraction.domain.usecase.UserInteraction
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.onFailure
import timber.log.Timber
import javax.inject.Inject

class LoginWithPinImpl @Inject constructor(
    private val hashPassphrase: HashPassphrase,
    private val openAppDatabase: OpenAppDatabase,
    private val pepperPassphrase: PepperPassphrase,
    private val userInteraction: UserInteraction,
) : LoginWithPin {

    @CheckResult
    override suspend fun invoke(pin: String): Result<Unit, LoginWithPinError> = coroutineBinding {
        val pinHash = hashPassphrase(
            pin = pin,
            initializeSalt = false
        ).mapError(
            HashDataError::toLoginWithPinError
        ).bind()

        val pepperedPinHash = pepperPassphrase(
            passphrase = pinHash.hash,
            initializePepper = false,
        ).mapError(
            PepperPassphraseError::toLoginWithPinError
        ).bind()

        // touching the keyboard does apparently not count as a user interaction on some devices (f. e. samsung galaxy a53)
        // -> trigger it manually
        userInteraction()

        openAppDatabase(
            passphrase = pepperedPinHash.hash
        ).onFailure {
            Timber.d("AppDatabase login failed")
        }.mapError(
            OpenDatabaseError::toLoginWithPinError
        ).bind()

        Timber.d("Pin Authentication succeeded")
    }
}
