package ch.admin.foitt.openid4vc.domain.model.presentationRequest

import ch.admin.foitt.openid4vc.domain.model.GetKeyPairError
import ch.admin.foitt.openid4vc.domain.model.KeyPairError

interface PresentationRequestError {
    data object NetworkError : FetchPresentationRequestError, SendPresentationError, SubmitPresentationErrorError
    data object CertificateNotPinnedError : FetchPresentationRequestError, SendPresentationError, SubmitPresentationErrorError
    data object ValidationError : SendPresentationError
    data class Unexpected(val throwable: Throwable?) :
        FetchPresentationRequestError,
        SendPresentationError,
        SubmitPresentationErrorError,
        CreateVerifiablePresentationTokenError,
        CreatePresentationRequestBodyError
}

sealed interface FetchPresentationRequestError : PresentationRequestError
sealed interface SendPresentationError : PresentationRequestError
sealed interface SubmitPresentationErrorError : PresentationRequestError
sealed interface CreateVerifiablePresentationTokenError : PresentationRequestError
sealed interface CreatePresentationRequestBodyError : PresentationRequestError

fun GetKeyPairError.toCreateVerifiablePresentationError() = when (this) {
    is KeyPairError.Unexpected -> PresentationRequestError.Unexpected(throwable)
    KeyPairError.NotFound -> PresentationRequestError.Unexpected(null)
}
