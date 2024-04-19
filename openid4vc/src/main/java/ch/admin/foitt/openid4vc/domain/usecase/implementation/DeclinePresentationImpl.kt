package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestErrorBody
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.SubmitPresentationErrorError
import ch.admin.foitt.openid4vc.domain.repository.PresentationRequestRepository
import ch.admin.foitt.openid4vc.domain.usecase.DeclinePresentation
import com.github.michaelbull.result.Result
import javax.inject.Inject

class DeclinePresentationImpl @Inject constructor(
    private val presentationRequestRepository: PresentationRequestRepository,
) : DeclinePresentation {
    override suspend fun invoke(url: String): Result<Unit, SubmitPresentationErrorError> {
        val body = PresentationRequestErrorBody(PresentationRequestErrorBody.ErrorType.CLIENT_REJECTED)
        return presentationRequestRepository.submitPresentationError(url, body)
    }
}
