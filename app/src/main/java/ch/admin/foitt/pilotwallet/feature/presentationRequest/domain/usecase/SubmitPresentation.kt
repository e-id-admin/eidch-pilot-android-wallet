package ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.usecase

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequest
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.model.SubmitPresentationError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CompatibleCredential
import com.github.michaelbull.result.Result

fun interface SubmitPresentation {

    suspend operator fun invoke(
        presentationRequest: PresentationRequest,
        compatibleCredential: CompatibleCredential,
    ): Result<Unit, SubmitPresentationError>
}
