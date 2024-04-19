package ch.admin.foitt.pilotwallet.platform.passphrasePeppering.data.repository

import androidx.security.crypto.EncryptedSharedPreferences
import ch.admin.foitt.pilotwallet.platform.byteArrayRepository.data.repository.SharedPreferencesByteArrayRepository
import ch.admin.foitt.pilotwallet.platform.byteArrayRepository.data.repository.SharedPreferencesByteArrayRepository.PrefKey
import ch.admin.foitt.pilotwallet.platform.byteArrayRepository.domain.repository.ByteArrayRepository
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.repository.PepperIvRepository
import javax.inject.Inject

class PepperIvRepositoryImpl @Inject constructor(
    sharedPreferences: EncryptedSharedPreferences,
) : PepperIvRepository,
    ByteArrayRepository by SharedPreferencesByteArrayRepository(
        prefKey = PrefKey.WalletPassphrasePepperIv,
        sharedPreferences = sharedPreferences,
    )
