package ch.admin.foitt.pilotwallet.feature.login.domain.usecase

import java.time.Duration

fun interface GetLockoutDuration {
    operator fun invoke(): Duration
}
