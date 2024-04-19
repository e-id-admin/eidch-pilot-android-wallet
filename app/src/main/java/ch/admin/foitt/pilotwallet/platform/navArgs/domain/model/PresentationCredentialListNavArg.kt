package ch.admin.foitt.pilotwallet.platform.navArgs.domain.model

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequest
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CompatibleCredential

data class PresentationCredentialListNavArg(
    val compatibleCredentials: Array<CompatibleCredential>,
    val presentationRequest: PresentationRequest,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PresentationCredentialListNavArg

        if (!compatibleCredentials.contentEquals(other.compatibleCredentials)) return false
        return presentationRequest == other.presentationRequest
    }

    override fun hashCode(): Int {
        var result = compatibleCredentials.contentHashCode()
        result = 31 * result + presentationRequest.hashCode()
        return result
    }
}
