package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.VerifiableCredentialConfig
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.CreatePresentationRequestBodyError
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.DescriptorMap
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PathNested
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequest
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestBody
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestError
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationSubmission
import ch.admin.foitt.openid4vc.domain.usecase.CreatePresentationRequestBody
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import java.util.UUID
import javax.inject.Inject

class CreatePresentationRequestBodyImpl @Inject constructor() : CreatePresentationRequestBody {
    override fun invoke(
        vpToken: String,
        presentationRequest: PresentationRequest,
        presentationConfig: VerifiableCredentialConfig,
    ): Result<PresentationRequestBody, CreatePresentationRequestBodyError> = runSuspendCatching {
        PresentationRequestBody(
            vpToken = vpToken,
            presentationSubmission = PresentationSubmission(
                definitionId = presentationRequest.presentationDefinition.id,
                descriptorMap = listOf(
                    DescriptorMap(
                        format = presentationConfig.format,
                        id = presentationRequest.presentationDefinition.inputDescriptors.first().id,
                        path = presentationConfig.path,
                        pathNested = PathNested(
                            format = presentationConfig.algorithm,
                            path = presentationConfig.credentialPath,
                        )
                    )
                ),
                id = UUID.randomUUID().toString(),
            )
        )
    }.mapError { throwable ->
        PresentationRequestError.Unexpected(throwable)
    }
}
