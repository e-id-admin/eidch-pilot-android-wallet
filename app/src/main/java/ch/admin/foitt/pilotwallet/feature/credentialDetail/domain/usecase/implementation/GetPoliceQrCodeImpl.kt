package ch.admin.foitt.pilotwallet.feature.credentialDetail.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.feature.credentialDetail.domain.usecase.GetPoliceQrCode
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCredentialClaimsError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialClaims
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import javax.inject.Inject

class GetPoliceQrCodeImpl @Inject constructor(
    private val getCredentialClaims: GetCredentialClaims
) : GetPoliceQrCode {
    private val policeQrCodeIdentifier = "policeQRImage"

    override suspend fun invoke(credentialId: Long): Result<String, GetCredentialClaimsError> = coroutineBinding {
        val claims = getCredentialClaims(credentialId = credentialId).bind()
        val policeQrCode = claims.firstOrNull {
            it.key == policeQrCodeIdentifier && it.valueType?.startsWith("image/") ?: false
        }
        policeQrCode?.value ?: ""
    }
}
