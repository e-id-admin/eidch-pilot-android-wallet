package ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.vcStatus.FetchVCStatusError
import ch.admin.foitt.openid4vc.domain.usecase.FetchVCStatus
import ch.admin.foitt.pilotwallet.platform.database.domain.model.Credential
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialBody
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialRepositoryError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCredentialBodiesByCredentialIdError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialBodiesByCredentialId
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.model.CheckCredentialValidityError
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.model.CredentialBodyValues
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.model.CredentialValidityStatus
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.model.UpdateCredentialStatusError
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.model.toCheckCredentialValidityError
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.GetAndRefreshCredentialValidity
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getOr
import com.github.michaelbull.result.mapEither
import com.github.michaelbull.result.mapError
import kotlinx.serialization.json.Json
import java.net.URL
import java.time.Instant
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Named

class GetAndRefreshCredentialValidityImpl @Inject constructor(
    private val fetchVCStatus: FetchVCStatus,
    private val getCredentialBodiesByCredentialId: GetCredentialBodiesByCredentialId,
    private val credentialRepo: CredentialRepo,
    @Named("jsonSerializerIgnoreKeys") private val json: Json,
) : GetAndRefreshCredentialValidity {
    override suspend fun invoke(credential: Credential): Result<CredentialStatus, CheckCredentialValidityError> = coroutineBinding {
        val credentialBodies = getCredentialBodiesByCredentialId(credential.id)
            .mapError(GetCredentialBodiesByCredentialIdError::toCheckCredentialValidityError).bind()

        val refreshResult = credentialBodies.map { credentialBody ->
            getCredentialValidityStatus(credentialBody)
        }
        // assumption: if at least one raw credential is valid -> the whole credential is also valid
        val hasValidResults = refreshResult.any { credentialBodyValidity ->
            credentialBodyValidity.get() is CredentialValidityStatus.Valid
        }

        val hasUnknownResults = refreshResult.any { credentialBodyValidity ->
            credentialBodyValidity.get() is CredentialValidityStatus.Unknown
        }

        val currentState = credential.status

        val newState = when {
            hasValidResults -> CredentialStatus.VALID
            hasUnknownResults -> currentState
            else -> CredentialStatus.INVALID
        }

        updateCredentialStatus(credential, newState).bind()
    }

    private suspend fun getCredentialValidityStatus(
        credentialBody: CredentialBody
    ): Result<CredentialValidityStatus, CheckCredentialValidityError> = coroutineBinding {
        // TODO: support other credential formats
        val credentialBodyValues = runSuspendCatching {
            json.decodeFromString<CredentialBodyValues>(credentialBody.body)
        }.mapError(UpdateCredentialStatusError::Unexpected).bind()
        val credentialStatusLists = credentialBodyValues.vc.credentialStatus

        val isExpired = hasCredentialExpired(credentialBodyValues).bind()
        val isRevoked = isCredentialRevoked(credentialStatusLists)
        val isSuspended = isCredentialSuspended(credentialStatusLists)

        when {
            isExpired -> CredentialValidityStatus.Expired
            isRevoked.getOr(false) -> CredentialValidityStatus.Revoked
            isSuspended.getOr(false) -> CredentialValidityStatus.Suspended
            // revocation and suspension calls can fail, leading to unknown states
            isRevoked.isErr || isSuspended.isErr -> CredentialValidityStatus.Unknown
            else -> CredentialValidityStatus.Valid
        }
    }

    private fun hasCredentialExpired(
        credentialBodyValues: CredentialBodyValues
    ): Result<Boolean, CheckCredentialValidityError> = runSuspendCatching {
        val now = Instant.now()

        val validFrom = credentialBodyValues.vc.validFrom?.let { parseDate(it) }
        val validUntil = credentialBodyValues.vc.validUntil?.let { parseDate(it) }

        when {
            validFrom != null && now.isBefore(validFrom.minusSeconds(BUFFER)) -> true
            validUntil != null && now.isAfter(validUntil.plusSeconds(BUFFER)) -> true
            else -> false
        }
    }.mapError(UpdateCredentialStatusError::Unexpected)

    private suspend fun isCredentialSuspended(
        statusLists: List<CredentialBodyValues.Vc.CredentialStatus>
    ): Result<Boolean, CheckCredentialValidityError> {
        val statusList = statusLists.find { it.statusPurpose == suspensionPurpose } ?: return Ok(false)
        return isStatusValid(statusList)
    }

    private suspend fun isCredentialRevoked(
        statusLists: List<CredentialBodyValues.Vc.CredentialStatus>
    ): Result<Boolean, CheckCredentialValidityError> {
        val statusList = statusLists.find { it.statusPurpose == revocationPurpose } ?: return Ok(false)
        return isStatusValid(statusList)
    }

    private suspend fun isStatusValid(
        credentialStatus: CredentialBodyValues.Vc.CredentialStatus
    ): Result<Boolean, CheckCredentialValidityError> =
        fetchVCStatus(
            statusListType = credentialStatus.type,
            url = URL(credentialStatus.id),
            index = credentialStatus.statusListIndex.toInt()
        ).mapError(FetchVCStatusError::toCheckCredentialValidityError)

    private fun parseDate(from: String): Instant = OffsetDateTime.parse(from).toInstant()

    private suspend fun updateCredentialStatus(
        credential: Credential,
        status: CredentialStatus,
    ): Result<CredentialStatus, CheckCredentialValidityError> {
        return credentialRepo.update(credential.copy(status = status)).mapEither(
            success = { status },
            failure = CredentialRepositoryError::toCheckCredentialValidityError
        )
    }

    companion object {
        private const val revocationPurpose = "revocation"
        private const val suspensionPurpose = "suspension"
        private const val BUFFER = 15L
    }
}
