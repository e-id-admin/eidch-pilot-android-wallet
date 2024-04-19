package ch.admin.foitt.openid4vc.domain.usecase

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.SubmitPresentationErrorError
import com.github.michaelbull.result.Result

fun interface DeclinePresentation {
    suspend operator fun invoke(url: String): Result<Unit, SubmitPresentationErrorError>
}
