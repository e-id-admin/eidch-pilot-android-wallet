package ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.impl

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOffer
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.GetCredentialOfferError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.InvitationError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.GetCredentialOfferFromUri
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.toErrorIf
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.URLDecoder
import javax.inject.Inject
import javax.inject.Named

internal class GetCredentialOfferFromUriImpl @Inject constructor(
    @Named("jsonSerializer") private val json: Json,
) : GetCredentialOfferFromUri {
    override fun invoke(uri: URI): Result<CredentialOffer, GetCredentialOfferError> = runSuspendCatching {
        val jsonString = URLDecoder.decode(
            uri.query.split("=").last(),
            "UTF-8"
        )
        json.decodeFromString<CredentialOffer>(jsonString)
    }.mapError { throwable ->
        val message = throwable.localizedMessage ?: throwable.toString()
        InvitationError.DeserializationFailed(message)
    }.toErrorIf(predicate = { it.grants.preAuthorizedCode == null }) {
        InvitationError.UnsupportedGrantType("Unsupported grant type: ${it.grants}")
    }.toErrorIf(predicate = { it.credentials.isEmpty() }) {
        InvitationError.NoCredentialsFound
    }
}
