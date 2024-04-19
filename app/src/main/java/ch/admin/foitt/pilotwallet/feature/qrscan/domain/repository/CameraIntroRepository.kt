package ch.admin.foitt.pilotwallet.feature.qrscan.domain.repository

interface CameraIntroRepository {
    suspend fun getPermissionPromptWasTriggered(): Boolean
    suspend fun setPermissionPromptWasTriggered(introWasPassed: Boolean)
}
