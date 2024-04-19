package ch.admin.foitt.pilotwallet.feature.home.domain.repository.implementation

import ch.admin.foitt.pilotwallet.feature.home.domain.repository.GetHomeIntroductionIsDone
import ch.admin.foitt.pilotwallet.feature.home.domain.repository.HomeIntroductionRepository
import javax.inject.Inject

class GetHomeIntroductionIsDoneImpl @Inject constructor(
    private val homeIntroductionRepository: HomeIntroductionRepository,
) : GetHomeIntroductionIsDone {
    override suspend fun invoke(): Boolean = homeIntroductionRepository.isDone()
}
