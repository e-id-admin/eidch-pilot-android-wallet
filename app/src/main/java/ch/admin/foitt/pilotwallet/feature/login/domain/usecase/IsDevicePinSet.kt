package ch.admin.foitt.pilotwallet.feature.login.domain.usecase

import androidx.annotation.CheckResult

fun interface IsDevicePinSet {
    @CheckResult
    operator fun invoke(): Boolean
}
