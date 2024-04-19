package ch.admin.foitt.pilotwallet.platform.login.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.appSetupState.domain.repository.UseBiometricLoginRepository
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.BiometricLoginEnabled
import javax.inject.Inject

class BiometricLoginEnabledImpl @Inject constructor(
    private val repo: UseBiometricLoginRepository,
) : BiometricLoginEnabled {

    override suspend fun invoke(): Boolean = repo.getUseBiometricLogin()
}
