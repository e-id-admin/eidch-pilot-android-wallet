package ch.admin.foitt.pilotwallet.feature.login

import android.os.SystemClock
import ch.admin.foitt.pilotwallet.feature.login.domain.repository.AttemptsRepository
import ch.admin.foitt.pilotwallet.feature.login.domain.repository.LockoutStartRepository
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.implementation.GetLockoutDurationImpl
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

class GetTimeoutDurationTest {

    @MockK
    private lateinit var mockAttemptsRepository: AttemptsRepository

    @MockK
    private lateinit var mockLockoutStartRepository: LockoutStartRepository

    private companion object {
        const val MAX_LOGIN_ATTEMPTS = 5
        const val BLOCKING_TIME_MS = 5 * 60 * 1000L
    }

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(SystemClock::class)

        coEvery { mockAttemptsRepository.increase() } just Runs
        coEvery { mockAttemptsRepository.deleteAttempts() } just Runs
        coEvery { mockLockoutStartRepository.saveStartingTime(any()) } just Runs
        coEvery { mockLockoutStartRepository.deleteStartingTime() } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `with no saved uptime or attempts, return zero lock time`() = runTest {
        coEvery { mockLockoutStartRepository.getStartingTime() } returns -1L
        coEvery { mockAttemptsRepository.getAttempts() } returns 0

        // currentUptime should have no impact in this case, so try it with a range of values
        listOf(Long.MAX_VALUE, 1_000_000, 1, 0, -1, -1_000_000, Long.MIN_VALUE).forEach { currentUptime ->
            coEvery { SystemClock.elapsedRealtime() } returns currentUptime
            val getTimeoutDuration =
                GetLockoutDurationImpl(attemptsRepository = mockAttemptsRepository, lockoutStartRepository = mockLockoutStartRepository)

            Assertions.assertEquals(Duration.ZERO, getTimeoutDuration())
        }
    }

    @Test
    fun `with no saved uptime lock after max number of login attempts`() = runTest {
        coEvery { SystemClock.elapsedRealtime() } returns 1_000_000L
        coEvery { mockLockoutStartRepository.getStartingTime() } returns 1_000_000L
        coEvery { mockAttemptsRepository.getAttempts() } returnsMany listOf(1, 2, 3, 4, 5, 6)

        val getTimeoutDuration =
            GetLockoutDurationImpl(attemptsRepository = mockAttemptsRepository, lockoutStartRepository = mockLockoutStartRepository)

        repeat(MAX_LOGIN_ATTEMPTS - 1) { retry ->
            assertEquals(
                Duration.ZERO,
                getTimeoutDuration(),
                "Should not lock after attempt ${retry + 1}/$MAX_LOGIN_ATTEMPTS",
            )
        }

        repeat(2) { retry ->
            assertEquals(
                Duration.ofMillis(BLOCKING_TIME_MS),
                getTimeoutDuration(),
                "Should lock after ${MAX_LOGIN_ATTEMPTS + retry} attempts"
            )
        }

        coVerifySequence {
            // while still unlocked
            repeat(MAX_LOGIN_ATTEMPTS - 1) {
                mockAttemptsRepository.getAttempts()
            }

            // when locked
            repeat(2) {
                mockAttemptsRepository.getAttempts()
                mockLockoutStartRepository.getStartingTime()
            }
        }
    }

    @Test
    fun `should return non-zero lock time when lockout time is not elapsed`() = runTest {
        coEvery { mockAttemptsRepository.getAttempts() } returns MAX_LOGIN_ATTEMPTS

        val timeoutStart = 1_000_000L
        coEvery { mockLockoutStartRepository.getStartingTime() } returns timeoutStart

        val getTimeoutDuration =
            GetLockoutDurationImpl(attemptsRepository = mockAttemptsRepository, lockoutStartRepository = mockLockoutStartRepository)

        listOf(timeoutStart + BLOCKING_TIME_MS - 1, 0, -1_000_000L, Long.MIN_VALUE + timeoutStart).forEach { currentUptime ->
            coEvery { SystemClock.elapsedRealtime() } returns currentUptime
            assertTrue(
                getTimeoutDuration() > Duration.ZERO,
                "lock time should be greater than zero",
            )
            coVerify(exactly = 0) {
                mockAttemptsRepository.increase()
                mockLockoutStartRepository.deleteStartingTime()
            }
        }
    }

    @Test
    fun `should return zero lock time after lockout time is elapsed`() = runTest {
        coEvery { mockAttemptsRepository.getAttempts() } returns MAX_LOGIN_ATTEMPTS

        val timeoutStart = 1_000_000L
        coEvery { mockLockoutStartRepository.getStartingTime() } returns timeoutStart

        val getTimeoutDuration =
            GetLockoutDurationImpl(attemptsRepository = mockAttemptsRepository, lockoutStartRepository = mockLockoutStartRepository)
        listOf(Long.MAX_VALUE, 2_000_000, timeoutStart + BLOCKING_TIME_MS + 1, timeoutStart + BLOCKING_TIME_MS).forEach { currentUptime ->
            coEvery { SystemClock.elapsedRealtime() } returns currentUptime
            assertEquals(
                Duration.ZERO,
                getTimeoutDuration(),
                "Lock time should be zero"
            )
            coVerify {
                mockLockoutStartRepository.deleteStartingTime()
            }
        }
    }

    @Test
    fun `should restart lockout duration time when currentUptime is lower than starting time of lockout`() = runTest {
        coEvery { mockAttemptsRepository.getAttempts() } returns MAX_LOGIN_ATTEMPTS

        val currentUptime = 1_000_000L
        coEvery { SystemClock.elapsedRealtime() } returns currentUptime

        listOf(1, 100_000L, Long.MAX_VALUE).forEach { offset ->
            coEvery { mockLockoutStartRepository.getStartingTime() } returns currentUptime + offset
            val getLockoutDuration = GetLockoutDurationImpl(
                attemptsRepository = mockAttemptsRepository,
                lockoutStartRepository = mockLockoutStartRepository
            )
            assertEquals(
                Duration.ofMillis(BLOCKING_TIME_MS),
                getLockoutDuration(),
                "lock time should be full blocking time"
            )
        }
        coVerify(exactly = 3) {
            mockLockoutStartRepository.saveStartingTime(currentUptime)
        }
    }

    @Test
    fun `should not restart lockout duration time when currentUptime is equal or greater than starting time of lockout`() = runTest {
        coEvery { mockAttemptsRepository.getAttempts() } returns MAX_LOGIN_ATTEMPTS

        val currentUptime = 1_000_000L
        coEvery { SystemClock.elapsedRealtime() } returns currentUptime

        listOf(1, 100_000L, currentUptime - 1).forEach { offset ->
            coEvery { mockLockoutStartRepository.getStartingTime() } returns currentUptime - offset
            val getLockoutDuration = GetLockoutDurationImpl(
                attemptsRepository = mockAttemptsRepository,
                lockoutStartRepository = mockLockoutStartRepository
            )
            assertTrue(
                getLockoutDuration() < Duration.ofMillis(BLOCKING_TIME_MS),
                "lock time should be not be full blocking time with offset of $offset"
            )
        }
        coVerify(exactly = 0) {
            mockLockoutStartRepository.saveStartingTime(any())
        }
    }
}
