package ch.admin.foitt.pilotwallet.feature.credentialDetail.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCredentialClaimsError
import com.github.michaelbull.result.Result

fun interface GetPoliceQrCode {
    @CheckResult
    suspend operator fun invoke(credentialId: Long): Result<String, GetCredentialClaimsError>
}
