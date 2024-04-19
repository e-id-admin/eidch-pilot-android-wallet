package ch.admin.foitt.openid4vc.domain.usecase

import ch.admin.foitt.openid4vc.domain.model.VerifiableCredentialConfig
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.CreatePresentationRequestBodyError
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequest
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestBody
import com.github.michaelbull.result.Result

fun interface CreatePresentationRequestBody {
    operator fun invoke(
        vpToken: String,
        presentationRequest: PresentationRequest,
        presentationConfig: VerifiableCredentialConfig,
    ): Result<PresentationRequestBody, CreatePresentationRequestBodyError>
}
