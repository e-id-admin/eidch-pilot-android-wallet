package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.pilotwallet.platform.locale.domain.usecase.GetLocalizedDisplay
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimData
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimImage
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimText
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCredentialClaimDataError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.toGetCredentialClaimDataError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialClaimData
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialClaimDisplays
import ch.admin.foitt.pilotwallet.platform.utils.base64NonUrlStringToByteArray
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class GetCredentialClaimDataImpl @Inject constructor(
    private val getCredentialClaimDisplays: GetCredentialClaimDisplays,
    private val getLocalizedDisplay: GetLocalizedDisplay,
) : GetCredentialClaimData {
    override suspend fun invoke(credentialClaim: CredentialClaim): Result<CredentialClaimData, GetCredentialClaimDataError> =
        coroutineBinding {
            val claimDisplays = getCredentialClaimDisplays(credentialClaim.id)
                .mapError { error ->
                    error.toGetCredentialClaimDataError()
                }.bind()

            val localizedDisplay =
                getLocalizedDisplay(claimDisplays) ?: return@coroutineBinding Err(SsiError.Unexpected(null)).bind<CredentialClaimData>()

            when (credentialClaim.valueType) {
                "bool", "string" -> CredentialClaimText(localizedKey = localizedDisplay.name, value = credentialClaim.value)
                "image/png", "image/jpeg" -> {
                    val byteArray = credentialClaim.value.base64NonUrlStringToByteArray()
                    CredentialClaimImage(localizedKey = localizedDisplay.name, imageData = byteArray)
                }

                else -> Err(SsiError.Unexpected(null)).bind<CredentialClaimData>()
            }
        }
}
