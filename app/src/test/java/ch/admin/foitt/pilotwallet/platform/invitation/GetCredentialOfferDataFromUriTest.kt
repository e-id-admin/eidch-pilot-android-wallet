package ch.admin.foitt.pilotwallet.platform.invitation

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOffer
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.Grant
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.PreAuthorizedContent
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.InvitationError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.impl.GetCredentialOfferFromUriImpl
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getError
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.junit.Assert
import org.junit.Test
import java.net.URI

class GetCredentialOfferDataFromUriTest {

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val getCredentialOfferUseCase = GetCredentialOfferFromUriImpl(
        json = json
    )

    @Test
    fun `valid VC invitation should return a CredentialOffer`() {
        val input =
            URI(
                "openid-credential-offer://?credential_offer=%7B%22credential_issuer%22%3A%22testissuer%22%2C%22credentials%22%3A%5B%22testcred%22%5D%2C%22grants%22%3A%7B%22urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Apre-authorized_code%22%3A%7B%22pre-authorized_code%22%3A%22test%22%7D%7D%7D"
            )
        val expected = Ok(
            CredentialOffer(
                credentialIssuer = "testissuer",
                credentials = listOf("testcred"),
                grants = Grant(
                    preAuthorizedCode = PreAuthorizedContent(preAuthorizedCode = "test"),
                    authorizedCode = null
                )
            )
        )
        Assert.assertEquals(expected, getCredentialOfferUseCase(uri = input))
    }

    @Test
    fun `invalid VC invitation should return an error`() {
        Assert.assertTrue(
            "empty input should return an error",
            getCredentialOfferUseCase(uri = URI("")).getError() is InvitationError.DeserializationFailed
        )

        // Known schema, no valid query
        Assert.assertTrue(
            "input without a query should return an error",
            getCredentialOfferUseCase(
                uri = URI("openid-credential-offer://foo")
            ).getError() is InvitationError.DeserializationFailed
        )

        // Known schema, no valid query
        Assert.assertTrue(
            "input without a valid query should return an error",
            getCredentialOfferUseCase(
                uri = URI("openid-credential-offer://?foo")
            ).getError() is InvitationError.DeserializationFailed
        )

        // Known schema, no valid query
        Assert.assertTrue(
            "input without a valid query should return an error",
            getCredentialOfferUseCase(
                uri = URI("openid-credential-offer://?foo=")
            ).getError() is InvitationError.DeserializationFailed
        )

        // Known schema, valid query but no credential offer
        Assert.assertTrue(
            "input without a valid query should return an error",
            getCredentialOfferUseCase(
                uri = URI("openid-credential-offer://?foo=bar")
            ).getError() is InvitationError.DeserializationFailed
        )

        // Known schema, valid query and credential offer but unsupported grant type
        Assert.assertTrue(
            "input with unsupported grant type should return an error",
            getCredentialOfferUseCase(
                uri = URI(
                    "openid-credential-offer://?credential_offer=%7B%22credential_issuer%22%3A%22testissuer%22%2C%22credentials%22%3A%5B%22testcred%22%5D%2C%22grants%22%3A%7B%7D%7D"
                )
            ).getError() is InvitationError.UnsupportedGrantType
        )
    }
}
