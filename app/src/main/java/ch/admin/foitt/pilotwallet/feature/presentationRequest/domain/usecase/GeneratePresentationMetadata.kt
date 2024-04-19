package ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.usecase

import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.model.GeneratePresentationMetadataError
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.model.PresentationMetadata
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CompatibleCredential
import com.github.michaelbull.result.Result

interface GeneratePresentationMetadata {
    suspend operator fun invoke(
        compatibleCredential: CompatibleCredential
    ): Result<PresentationMetadata, GeneratePresentationMetadataError>
}
