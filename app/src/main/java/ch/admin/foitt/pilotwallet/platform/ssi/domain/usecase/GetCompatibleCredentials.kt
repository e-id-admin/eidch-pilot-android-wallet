package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.InputDescriptor
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CompatibleCredential
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCompatibleCredentialsError
import com.github.michaelbull.result.Result

fun interface GetCompatibleCredentials {
    @CheckResult
    suspend operator fun invoke(inputDescriptors: List<InputDescriptor>): Result<List<CompatibleCredential>, GetCompatibleCredentialsError>
}
