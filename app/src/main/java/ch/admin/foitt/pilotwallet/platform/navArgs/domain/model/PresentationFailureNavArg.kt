package ch.admin.foitt.pilotwallet.platform.navArgs.domain.model

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequest
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CompatibleCredential

data class PresentationFailureNavArg(
    val compatibleCredential: CompatibleCredential,
    val presentationRequest: PresentationRequest,
)
