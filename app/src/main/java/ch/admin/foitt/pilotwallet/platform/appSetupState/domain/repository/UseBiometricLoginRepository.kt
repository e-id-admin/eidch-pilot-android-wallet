package ch.admin.foitt.pilotwallet.platform.appSetupState.domain.repository

interface UseBiometricLoginRepository {
    suspend fun saveUseBiometricLogin(isEnabled: Boolean)
    suspend fun getUseBiometricLogin(): Boolean
}
