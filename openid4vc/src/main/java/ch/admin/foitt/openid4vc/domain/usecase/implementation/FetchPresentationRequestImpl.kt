package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.FetchPresentationRequestError
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequest
import ch.admin.foitt.openid4vc.domain.repository.PresentationRequestRepository
import ch.admin.foitt.openid4vc.domain.usecase.FetchPresentationRequest
import com.github.michaelbull.result.Result
import java.net.URL
import javax.inject.Inject

internal class FetchPresentationRequestImpl @Inject constructor(
    private val presentationRequestRepository: PresentationRequestRepository,
) : FetchPresentationRequest {
    override suspend fun invoke(url: URL): Result<PresentationRequest, FetchPresentationRequestError> =
        presentationRequestRepository.fetchPresentationRequest(url)
}
