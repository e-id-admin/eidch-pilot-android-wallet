package ch.admin.foitt.pilotwallet.platform.authWithPin.domain.usecase.implementation

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.authWithPin.domain.model.AuthWithPinError
import ch.admin.foitt.pilotwallet.platform.authWithPin.domain.model.toAuthWithPinError
import ch.admin.foitt.pilotwallet.platform.authWithPin.domain.usecase.AuthWithPin
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.CheckDatabasePassphrase
import ch.admin.foitt.pilotwallet.platform.passphraseHashing.domain.usecase.HashPassphrase
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.usecase.PepperPassphrase
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.ErrorDialogState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetErrorDialogState
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.onFailure
import timber.log.Timber
import javax.inject.Inject

class AuthWithPinImpl @Inject constructor(
    private val hashPassphrase: HashPassphrase,
    private val pepperPassphrase: PepperPassphrase,
    private val checkDatabasePassphrase: CheckDatabasePassphrase,
    private val setErrorDialogState: SetErrorDialogState,
) : AuthWithPin {

    @CheckResult
    override suspend fun invoke(pin: String): Result<Unit, AuthWithPinError> = coroutineBinding {
        val pinHash = hashPassphrase(
            pin = pin,
            initializeSalt = false
        ).mapError { error ->
            error.toAuthWithPinError()
        }.bind()

        val pepperedPinHash = pepperPassphrase(
            passphrase = pinHash.hash,
            initializePepper = false,
        ).mapError { error ->
            error.toAuthWithPinError()
        }.bind()

        checkDatabasePassphrase(passphrase = pepperedPinHash.hash)
            .onFailure {
                Timber.d("DB encryption failed, wrong passphrase")
            }.mapError { error ->
                error.toAuthWithPinError()
            }.bind()
    }.onFailure { authError ->
        onAuthFailure(authError)
    }

    private fun onAuthFailure(authError: AuthWithPinError) {
        when (authError) {
            AuthWithPinError.InvalidPassphrase -> {}
            is AuthWithPinError.Unexpected -> {
                setErrorDialogState(
                    ErrorDialogState.Wallet(
                        errorDetails = authError.cause?.localizedMessage,
                    )
                )
                Timber.e(authError.cause)
            }
        }
    }
}
