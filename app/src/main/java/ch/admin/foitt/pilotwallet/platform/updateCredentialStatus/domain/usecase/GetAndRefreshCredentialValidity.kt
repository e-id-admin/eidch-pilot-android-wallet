package ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase

import ch.admin.foitt.pilotwallet.platform.database.domain.model.Credential
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.model.CheckCredentialValidityError
import com.github.michaelbull.result.Result

fun interface GetAndRefreshCredentialValidity {
    suspend operator fun invoke(credential: Credential): Result<CredentialStatus, CheckCredentialValidityError>
}
