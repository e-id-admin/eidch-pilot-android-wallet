package ch.admin.foitt.pilotwallet.feature.presentationRequest.mock

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.Constraints
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.DescriptorMap
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.Field
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.Filter
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.Format
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.InputDescriptor
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.JwtVc
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PathNested
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationDefinition
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequest
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestBody
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationSubmission
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.model.PresentationConfig
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialRaw

internal object MockPresentationValues {

    private val presentationConfig by lazy {
        PresentationConfig()
    }

    val presentationRequest by lazy {
        PresentationRequest(
            nonce = "6bc8779252a396c1a8c4bb1af622830c",
            presentationDefinition = presentationDefinition,
            responseUri = "https://verifier.example.org/request-object/60863858-3cf0-4594-a0ff-0a31ca90396x/response-data",
            responseMode = "direct_post",
            state = "someState",
            clientMetaData = null,
        )
    }

    val presentationDefinition by lazy {
        PresentationDefinition(
            id = "someId",
            inputDescriptors = listOf(inputDescriptor),
        )
    }

    val inputDescriptor by lazy {
        InputDescriptor(
            id = "someInputDescriptorId",
            format = Format(
                jwtVc = JwtVc(
                    alg = presentationConfig.algorithm
                )
            ),
            constraints = Constraints(
                fields = listOf(constraintField01)
            )
        )
    }

    private val constraintField01 by lazy {
        Field(
            filter = Filter(
                pattern = "somePattern",
                type = "string"
            ),
            path = listOf("$.vc.type[*]"),
        )
    }

    val credential by lazy {
        CredentialRaw(
            id = 57L,
            keyIdentifier = "someIdentifier",
            payload = "somePayload",
            format = "someFormat",
            credentialId = 42L,
        )
    }

    val disclosedSdJwt by lazy {
        "someDisclosedJwt"
    }

    val presentationToken by lazy {
        "somePresentationToken"
    }

    val presentationRequestBody by lazy {
        PresentationRequestBody(
            vpToken = presentationToken,
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
                id = "someUUID",
            )
        )
    }
}
