package ch.admin.foitt.openid4vc.domain.usecase

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestBody
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.SendPresentationError
import com.github.michaelbull.result.Result
import java.net.URL

fun interface SendPresentation {
    suspend operator fun invoke(
        url: URL,
        presentationRequestBody: PresentationRequestBody,
    ): Result<Unit, SendPresentationError>
}
