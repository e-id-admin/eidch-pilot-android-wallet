package ch.admin.foitt.pilotwallet.platform.passphraseHashing.data.repository

import androidx.security.crypto.EncryptedSharedPreferences
import ch.admin.foitt.pilotwallet.platform.byteArrayRepository.data.repository.SharedPreferencesByteArrayRepository
import ch.admin.foitt.pilotwallet.platform.byteArrayRepository.data.repository.SharedPreferencesByteArrayRepository.PrefKey
import ch.admin.foitt.pilotwallet.platform.byteArrayRepository.domain.repository.ByteArrayRepository
import ch.admin.foitt.pilotwallet.platform.passphraseHashing.domain.repository.SaltRepository
import javax.inject.Inject

class SaltRepositoryImpl @Inject constructor(
    private val sharedPreferences: EncryptedSharedPreferences,
) : SaltRepository,
    ByteArrayRepository by SharedPreferencesByteArrayRepository(
        prefKey = PrefKey.WalletPassphraseSalt,
        sharedPreferences = sharedPreferences,
    )
