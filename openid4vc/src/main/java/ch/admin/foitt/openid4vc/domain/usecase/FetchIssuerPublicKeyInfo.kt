package ch.admin.foitt.openid4vc.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchIssuerPublicKeyInfoError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerPublicKeyInfo
import com.github.michaelbull.result.Result

interface FetchIssuerPublicKeyInfo {
    @CheckResult
    suspend operator fun invoke(issuerEndpoint: String): Result<IssuerPublicKeyInfo, FetchIssuerPublicKeyInfoError>
}
