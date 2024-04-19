package ch.admin.foitt.pilotwallet.feature.qrscan.mock

import ch.admin.foitt.pilotwallet.feature.qrscan.domain.repository.CameraIntroRepository

class InMemoryCameraIntroRepository : CameraIntroRepository {
    var value = false
    override suspend fun getPermissionPromptWasTriggered() = value

    override suspend fun setPermissionPromptWasTriggered(introWasPassed: Boolean) {
        value = introWasPassed
    }
}
