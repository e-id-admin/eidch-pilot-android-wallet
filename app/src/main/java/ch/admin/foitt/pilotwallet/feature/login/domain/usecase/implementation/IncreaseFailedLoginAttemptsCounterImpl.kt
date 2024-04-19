package ch.admin.foitt.pilotwallet.feature.login.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.feature.login.domain.repository.AttemptsRepository
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.IncreaseFailedLoginAttemptsCounter
import javax.inject.Inject

class IncreaseFailedLoginAttemptsCounterImpl @Inject constructor(
    private val attemptsRepository: AttemptsRepository
) : IncreaseFailedLoginAttemptsCounter {
    override fun invoke() {
        attemptsRepository.increase()
    }
}
