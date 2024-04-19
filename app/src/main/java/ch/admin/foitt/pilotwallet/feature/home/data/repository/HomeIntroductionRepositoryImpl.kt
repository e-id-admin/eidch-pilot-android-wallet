package ch.admin.foitt.pilotwallet.feature.home.data.repository

import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import ch.admin.foitt.pilotwallet.feature.home.domain.repository.HomeIntroductionRepository
import javax.inject.Inject

class HomeIntroductionRepositoryImpl @Inject constructor(
    private val sharedPreferences: EncryptedSharedPreferences,
) : HomeIntroductionRepository {
    private val prefKey = "home_introduction_done"

    override suspend fun isDone() = sharedPreferences.getBoolean(prefKey, false)

    override suspend fun setDone() = sharedPreferences.edit {
        putBoolean(prefKey, true)
    }
}
