package ch.admin.foitt.pilotwallet.feature.home.domain.repository.implementation

import ch.admin.foitt.pilotwallet.feature.home.domain.repository.HomeIntroductionRepository
import ch.admin.foitt.pilotwallet.feature.home.domain.repository.SaveHomeIntroductionIsDone
import javax.inject.Inject

class SaveHomeIntroductionIsDoneImpl @Inject constructor(
    private val homeIntroductionRepository: HomeIntroductionRepository,
) : SaveHomeIntroductionIsDone {
    override suspend fun invoke() = homeIntroductionRepository.setDone()
}
