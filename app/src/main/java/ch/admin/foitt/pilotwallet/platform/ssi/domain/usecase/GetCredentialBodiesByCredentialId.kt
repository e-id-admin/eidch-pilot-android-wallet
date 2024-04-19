package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialBody
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCredentialBodiesByCredentialIdError
import com.github.michaelbull.result.Result

fun interface GetCredentialBodiesByCredentialId {
    @CheckResult
    suspend operator fun invoke(credentialId: Long): Result<List<CredentialBody>, GetCredentialBodiesByCredentialIdError>
}
