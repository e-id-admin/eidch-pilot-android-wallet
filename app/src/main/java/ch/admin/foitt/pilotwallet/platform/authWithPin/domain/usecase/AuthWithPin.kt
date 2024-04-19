package ch.admin.foitt.pilotwallet.platform.authWithPin.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.authWithPin.domain.model.AuthWithPinError
import com.github.michaelbull.result.Result

fun interface AuthWithPin {
    @CheckResult
    suspend operator fun invoke(pin: String): Result<Unit, AuthWithPinError>
}
