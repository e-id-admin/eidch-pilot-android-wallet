package ch.admin.foitt.pilotwallet.platform.login.domain.usecase

import ch.admin.foitt.pilotwallet.platform.login.domain.model.CanUseBiometricsForLoginResult

interface CanUseBiometricsForLogin {
    suspend operator fun invoke(): CanUseBiometricsForLoginResult
}
