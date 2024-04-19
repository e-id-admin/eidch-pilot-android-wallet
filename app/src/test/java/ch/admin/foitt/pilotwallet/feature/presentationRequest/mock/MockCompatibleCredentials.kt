package ch.admin.foitt.pilotwallet.feature.presentationRequest.mock

import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CompatibleCredential
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.PresentationRequestField

object MockCompatibleCredentials {

    val compatibleCredential by lazy {
        CompatibleCredential(
            credentialId = 3L,
            requestedFields = listOf(requestedField)
        )
    }

    private val requestedField by lazy {
        PresentationRequestField(
            jsonPath = "$.vc.type[*]",
            value = "someType",
        )
    }
}
