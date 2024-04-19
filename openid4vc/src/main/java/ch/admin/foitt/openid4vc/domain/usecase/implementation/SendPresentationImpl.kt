package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestBody
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.SendPresentationError
import ch.admin.foitt.openid4vc.domain.repository.PresentationRequestRepository
import ch.admin.foitt.openid4vc.domain.usecase.SendPresentation
import com.github.michaelbull.result.Result
import java.net.URL
import javax.inject.Inject

class SendPresentationImpl @Inject constructor(
    private val presentationRequestRepository: PresentationRequestRepository,
) : SendPresentation {
    override suspend fun invoke(url: URL, presentationRequestBody: PresentationRequestBody): Result<Unit, SendPresentationError> =
        presentationRequestRepository.submitPresentation(url, presentationRequestBody)
}
