package ch.admin.foitt.pilotwallet.feature.login.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.feature.login.domain.repository.AttemptsRepository
import ch.admin.foitt.pilotwallet.feature.login.domain.repository.LockoutStartRepository
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.ResetLockout
import javax.inject.Inject

class ResetLockoutImpl @Inject constructor(
    private val lockoutStartRepository: LockoutStartRepository,
    private val attemptsRepository: AttemptsRepository
) : ResetLockout {
    override fun invoke() {
        lockoutStartRepository.deleteStartingTime()
        attemptsRepository.deleteAttempts()
    }
}
