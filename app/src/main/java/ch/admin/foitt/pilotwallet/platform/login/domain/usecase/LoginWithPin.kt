package ch.admin.foitt.pilotwallet.platform.login.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.login.domain.model.LoginWithPinError
import com.github.michaelbull.result.Result

fun interface LoginWithPin {
    @CheckResult
    suspend operator fun invoke(pin: String): Result<Unit, LoginWithPinError>
}
