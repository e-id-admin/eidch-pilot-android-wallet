package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.VerifiableCredential
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.CredentialSubject
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerCredentialInformation
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.SupportedCredential
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CredentialOfferError
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.SaveCredentialError
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.toSaveCredentialError
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.SaveCredential
import ch.admin.foitt.pilotwallet.platform.database.domain.model.Credential
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaimDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialIssuerDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialRaw
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DisplayLanguage
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.ParseSdJwt
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialClaimDisplayRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialClaimRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialDisplayRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialIssuerDisplayRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialRawRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.DeleteCredential
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath.using
import com.jayway.jsonpath.Option
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2

class SaveCredentialImpl @Inject constructor(
    private val parseSdJwt: ParseSdJwt,
    private val credentialRepo: CredentialRepo,
    private val credentialIssuerDisplayRepo: CredentialIssuerDisplayRepo,
    private val credentialDisplayRepo: CredentialDisplayRepo,
    private val credentialClaimRepo: CredentialClaimRepo,
    private val credentialClaimDisplayRepo: CredentialClaimDisplayRepo,
    private val credentialRawRepo: CredentialRawRepo,
    private val deleteCredential: DeleteCredential,
) : SaveCredential {

    override suspend fun invoke(
        issuerInfo: IssuerCredentialInformation,
        verifiableCredential: VerifiableCredential,
        credentialIdentifier: String
    ): Result<Long, SaveCredentialError> = coroutineBinding {
        val credentialId = createAndSaveCredential().bind()

        val savingResult = coroutineBinding {
            createAndSaveCredentialIssuerDisplays(
                credentialId = credentialId,
                issuerInfo = issuerInfo
            ).bind()

            val supportedCredential = getSupportedCredential(
                supportedCredentials = issuerInfo.supportedCredentials,
                credentialIdentifier = credentialIdentifier
            ).bind()

            createAndSaveCredentialDisplays(
                credentialId = credentialId,
                supportedCredential = supportedCredential,
                credentialIdentifier = credentialIdentifier,
            ).bind()

            createAndSaveCredentialClaims(
                credentialId = credentialId,
                supportedCredential = supportedCredential,
                verifiableCredential = verifiableCredential,
            ).bind()

            createAndSaveCredentialRaw(
                credentialId = credentialId,
                verifiableCredential = verifiableCredential
            ).bind()
        }

        if (savingResult.isErr) {
            deleteCredential(credentialId)
        }

        savingResult.bind()

        credentialId
    }

    private suspend fun createAndSaveCredentialIssuerDisplays(
        credentialId: Long,
        issuerInfo: IssuerCredentialInformation
    ): Result<Unit, SaveCredentialError> = coroutineBinding {
        val credentialIssuerDisplays = mutableSetOf<CredentialIssuerDisplay>()
        credentialIssuerDisplays.addAll(
            issuerInfo.display.map { display ->
                CredentialIssuerDisplay(
                    credentialId = credentialId,
                    name = display.name,
                    image = display.logo?.data,
                    locale = display.locale ?: DisplayLanguage.FALLBACK
                )
            }
        )

        if (credentialIssuerDisplays.none { it.locale == DisplayLanguage.FALLBACK }) {
            credentialIssuerDisplays.add(
                CredentialIssuerDisplay(
                    credentialId = credentialId,
                    name = issuerInfo.credentialIssuer,
                    locale = DisplayLanguage.FALLBACK
                )
            )
        }

        credentialIssuerDisplayRepo.insertAll(credentialIssuerDisplays).mapError {
            CredentialOfferError.DatabaseError
        }.bind()
    }

    private suspend fun createAndSaveCredential(): Result<Long, SaveCredentialError> = coroutineBinding {
        credentialRepo.insert(credential = Credential()).mapError {
            CredentialOfferError.DatabaseError
        }.bind()
    }

    private suspend fun createAndSaveCredentialDisplays(
        credentialId: Long,
        supportedCredential: SupportedCredential,
        credentialIdentifier: String,
    ): Result<Unit, SaveCredentialError> = coroutineBinding {
        val credentialDisplays: MutableList<CredentialDisplay> = mutableListOf()
        supportedCredential.display?.map { display ->
            credentialDisplays.add(
                CredentialDisplay(
                    credentialId = credentialId,
                    locale = display.locale ?: DisplayLanguage.FALLBACK,
                    name = display.name,
                    description = display.description,
                    logoUrl = display.logo?.url,
                    // TODO if null, set from downloaded logoUrl?
                    logoData = display.logo?.data,
                    logoAltText = display.logo?.altText,
                    backgroundColor = display.backgroundColor,
                    textColor = display.textColor
                )
            )
        }
        if (credentialDisplays.none { it.locale == DisplayLanguage.FALLBACK }) {
            credentialDisplays.add(
                CredentialDisplay(
                    credentialId = credentialId,
                    locale = DisplayLanguage.FALLBACK,
                    name = credentialIdentifier
                )
            )
        }

        credentialDisplayRepo.insertAll(credentialDisplays = credentialDisplays).mapError {
            CredentialOfferError.DatabaseError
        }.bind()
    }

    private suspend fun createAndSaveCredentialClaims(
        credentialId: Long,
        supportedCredential: SupportedCredential,
        verifiableCredential: VerifiableCredential,
    ): Result<Unit, SaveCredentialError> = coroutineBinding {
        // TODO: support nested credentialSubjects, e.g. for ref/dummy/sd_jwt_offer
        val displayCredentialSubject = getDisplayCredentialSubject(supportedCredential = supportedCredential).bind()

        val conf: Configuration = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS, Option.ALWAYS_RETURN_LIST).build()
        val sdJwt = parseSdJwt(verifiableCredential.credential).mapError { error ->
            error.toSaveCredentialError()
        }.bind()

        // TODO: support nested credentialSubjects, e.g. for ref/dummy/sd_jwt_offer
        val issuedCredentialSubjects: Map<String, String> = using(conf)
            .parse(sdJwt.jsonWithActualValues)
            .read<List<Map<String, JsonElement>>>("$.vc.credentialSubject")
            .firstOrNull()
            ?.mapValues { it.value.jsonPrimitive.content } ?: emptyMap()

        displayCredentialSubject.forEach { (subjectKey: String, subject: CredentialSubject) ->
            checkMandatoryClaimValue(subjectKey, subject.mandatory, issuedCredentialSubjects[subjectKey]).bind()
            val value = issuedCredentialSubjects[subjectKey] ?: return@forEach
            val credentialClaim = CredentialClaim(
                credentialId = credentialId,
                key = subjectKey,
                value = value,
                valueType = subject.valueType,
                order = supportedCredential.order?.indexOf(subjectKey) ?: -1
            )
            val claimId = credentialClaimRepo.insert(credentialClaim = credentialClaim)
                .mapError {
                    CredentialOfferError.DatabaseError
                }.bind()

            val claimDisplays =
                createCredentialClaimDisplays(claimId = claimId, subject = subject, subjectKey = subjectKey)
            credentialClaimDisplayRepo.insertAll(claimDisplays)
                .mapError {
                    CredentialOfferError.DatabaseError
                }.bind()
        }
    }

    private fun createCredentialClaimDisplays(claimId: Long, subject: CredentialSubject, subjectKey: String): List<CredentialClaimDisplay> {
        val credentialClaimDisplays = mutableListOf<CredentialClaimDisplay>()
        subject.display?.forEach { display ->
            credentialClaimDisplays.add(
                CredentialClaimDisplay(
                    claimId = claimId,
                    name = display.name,
                    locale = display.locale ?: DisplayLanguage.FALLBACK
                )
            )
        }
        if (credentialClaimDisplays.none { it.locale == DisplayLanguage.FALLBACK }) {
            credentialClaimDisplays.add(
                CredentialClaimDisplay(
                    claimId = claimId,
                    name = subjectKey,
                    locale = DisplayLanguage.FALLBACK
                )
            )
        }
        return credentialClaimDisplays
    }

    private fun checkMandatoryClaimValue(
        name: String,
        mandatory: Boolean?,
        value: String?
    ): Result<Unit, SaveCredentialError> = if (mandatory == true && value == null) {
        Err(CredentialOfferError.MissingMandatoryField(name))
    } else {
        Ok(Unit)
    }

    private fun getSupportedCredential(
        supportedCredentials: Map<String, SupportedCredential>,
        credentialIdentifier: String
    ): Result<SupportedCredential, SaveCredentialError> {
        val supportedCredential = supportedCredentials[credentialIdentifier]
        return supportedCredential?.let { Ok(it) } ?: Err(CredentialOfferError.UnsupportedCredentialFormat)
    }

    private fun getDisplayCredentialSubject(
        supportedCredential: SupportedCredential?
    ): Result<Map<String, CredentialSubject>, SaveCredentialError> = runSuspendCatching {
        Json.decodeFromString<Map<String, CredentialSubject>>(supportedCredential?.credentialDefinition?.credentialSubject ?: "")
    }.mapError {
        CredentialOfferError.UnsupportedCredentialFormat
    }

    private suspend fun createAndSaveCredentialRaw(
        credentialId: Long,
        verifiableCredential: VerifiableCredential
    ): Result<Long, SaveCredentialError> = coroutineBinding {
        val credentialRaw = CredentialRaw(
            credentialId = credentialId,
            keyIdentifier = verifiableCredential.signingKeyId,
            payload = verifiableCredential.credential,
            format = verifiableCredential.format
        )
        credentialRawRepo.insert(credentialRaw).mapError {
            CredentialOfferError.DatabaseError
        }.bind()
    }
}
