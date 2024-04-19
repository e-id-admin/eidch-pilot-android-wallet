package ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.CreatePresentationRequestBodyError
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.CreateVerifiablePresentationTokenError
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequest
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.SendPresentationError
import ch.admin.foitt.openid4vc.domain.usecase.CreatePresentationRequestBody
import ch.admin.foitt.openid4vc.domain.usecase.CreateVerifiablePresentationToken
import ch.admin.foitt.openid4vc.domain.usecase.SendPresentation
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.model.PresentationConfig
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.model.PresentationRequestError
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.model.SubmitPresentationError
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.model.toSubmitPresentationError
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.usecase.SubmitPresentation
import ch.admin.foitt.pilotwallet.platform.di.IoDispatcher
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.CreateDisclosedSdJwtError
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.CreateDisclosedSdJwt
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CompatibleCredential
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialRawRepositoryError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialRawRepo
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URL
import javax.inject.Inject

class SubmitPresentationImpl @Inject constructor(
    private val credentialRawRepo: CredentialRawRepo,
    private val createVerifiablePresentationToken: CreateVerifiablePresentationToken,
    private val createDisclosedSdJwt: CreateDisclosedSdJwt,
    private val sendPresentation: SendPresentation,
    private val presentationConfig: PresentationConfig,
    private val createPresentationRequestBody: CreatePresentationRequestBody,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : SubmitPresentation {
    override suspend fun invoke(
        presentationRequest: PresentationRequest,
        compatibleCredential: CompatibleCredential,
    ): Result<Unit, SubmitPresentationError> = withContext(ioDispatcher) {
        coroutineBinding {
            val inputDescriptor = runSuspendCatching {
                presentationRequest.presentationDefinition.inputDescriptors.first()
            }.mapError { throwable ->
                PresentationRequestError.Unexpected(throwable)
            }.bind()

            val algorithm = inputDescriptor.format.jwtVc.alg
            if (algorithm != presentationConfig.algorithm) {
                Timber.w(
                    "Algorithm do not match.\nApp: ${presentationConfig.algorithm}\nServer: $algorithm"
                )
            }

            val rawCredentials = credentialRawRepo.getByCredentialId(
                credentialId = compatibleCredential.credentialId
            ).mapError(
                CredentialRawRepositoryError::toSubmitPresentationError
            ).bind()

            // TODO support multiple raw credentials per credential
            val rawCredential = runSuspendCatching {
                rawCredentials.first()
            }.mapError {
                PresentationRequestError.CredentialNoFound
            }.bind()

            val disclosedSdJwt = createDisclosedSdJwt(
                sdJwtString = rawCredential.payload,
                attributesKey = compatibleCredential.requestedFields.map { it.key },
            ).mapError(CreateDisclosedSdJwtError::toSubmitPresentationError)
                .bind()

            val vpToken = createVerifiablePresentationToken(
                keyAlias = rawCredential.keyIdentifier,
                nonce = presentationRequest.nonce,
                disclosedSdJwt = disclosedSdJwt,
                config = presentationConfig,
            ).mapError(CreateVerifiablePresentationTokenError::toSubmitPresentationError)
                .bind()

            val submissionObject = createPresentationRequestBody(
                vpToken = vpToken,
                presentationRequest = presentationRequest,
                presentationConfig = presentationConfig,
            ).mapError(CreatePresentationRequestBodyError::toSubmitPresentationError)
                .bind()

            val responseURL = runSuspendCatching {
                URL(presentationRequest.responseUri)
            }.mapError {
                PresentationRequestError.InvalidUrl
            }.bind()

            sendPresentation(
                url = responseURL,
                presentationRequestBody = submissionObject,
            ).mapError(SendPresentationError::toSubmitPresentationError)
                .bind()
        }
    }
}
