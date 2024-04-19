package ch.admin.foitt.pilotwallet.feature.login.domain.usecase.implementation

import android.os.SystemClock
import ch.admin.foitt.pilotwallet.feature.login.domain.repository.AttemptsRepository
import ch.admin.foitt.pilotwallet.feature.login.domain.repository.LockoutStartRepository
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.GetLockoutDuration
import java.time.Duration
import javax.inject.Inject
import kotlin.math.max

class GetLockoutDurationImpl @Inject constructor(
    private val attemptsRepository: AttemptsRepository,
    private val lockoutStartRepository: LockoutStartRepository,
) : GetLockoutDuration {

    private companion object {
        const val MAX_LOGIN_ATTEMPTS = 5
        const val BLOCKING_TIME_MS = 5 * 60 * 1000L
    }

    override fun invoke(): Duration {
        val loginAttempts = attemptsRepository.getAttempts()
        return if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
            getLockoutDuration()
        } else {
            Duration.ZERO
        }
    }

    private fun getLockoutDuration(): Duration {
        val timeSinceLastBoot = SystemClock.elapsedRealtime()
        val lockoutStartingTime = lockoutStartRepository.getStartingTime()

        return if (lockoutStartingTime < 0 || timeSinceLastBoot < lockoutStartingTime) {
            // Starting new lockout or restarted device
            // If the device was rebooted, then timeSinceLastBoot is lower than lockoutStartingTime.
            // In this case the remaining lockout time is restarted as it is not possible to calculate the correct value.
            lockoutStartRepository.saveStartingTime(timeSinceLastBoot)
            Duration.ofMillis(BLOCKING_TIME_MS)
        } else if (timeSinceLastBoot - lockoutStartingTime >= BLOCKING_TIME_MS) {
            // Lockout has elapsed
            lockoutStartRepository.deleteStartingTime()
            attemptsRepository.deleteAttempts()
            Duration.ZERO
        } else {
            // Return remaining lockout duration
            Duration.ofMillis(BLOCKING_TIME_MS - max(timeSinceLastBoot - lockoutStartingTime, 0))
        }
    }
}
