package ch.admin.foitt.pilotwallet.platform.updateVCs.mock

import ch.admin.foitt.pilotwallet.platform.database.domain.model.Credential
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialBody
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.model.CredentialBodyValues
import java.time.Instant

object MockCredentials {
    val credentials = listOf(
        Credential(
            id = 0,
            status = CredentialStatus.VALID,
            createdAt = 1700463600000,
            updatedAt = null,
        ),
        Credential(
            id = 1,
            status = CredentialStatus.INVALID,
            createdAt = 1700463600000,
            updatedAt = null,
        ),
        Credential(
            id = 2,
            status = CredentialStatus.UNKNOWN,
            createdAt = 1700463500000,
            updatedAt = null,
        )
    )

    val credentialBody = listOf(
        CredentialBody(
            credentialId = 0,
            body = "",
        ),
        CredentialBody(
            credentialId = -1L,
            body = "/",
        )
    )

    val validCredentialBodyValues: CredentialBodyValues
        get() {
            val now = Instant.now()
            val yesterday = now.minusSeconds(86400)
            val tomorrow = now.plusSeconds(86400)
            return generateCredentialBodyValues(validFrom = yesterday.toString(), validUntil = tomorrow.toString())
        }

    val notYetValidCredentialBodyValues: CredentialBodyValues
        get() {
            val now = Instant.now()
            val tomorrow = now.plusSeconds(86400)
            return generateCredentialBodyValues(validFrom = tomorrow.toString(), validUntil = tomorrow.toString())
        }

    val expiredCredentialBodyValues: CredentialBodyValues
        get() {
            val now = Instant.now()
            val yesterday = now.minusSeconds(86400)
            return generateCredentialBodyValues(validFrom = yesterday.toString(), validUntil = yesterday.toString())
        }

    private fun generateCredentialBodyValues(validFrom: String, validUntil: String): CredentialBodyValues =
        CredentialBodyValues(
            vc = CredentialBodyValues.Vc(
                validFrom = validFrom,
                validUntil = validUntil,
                credentialStatus = listOf(
                    CredentialBodyValues.Vc.CredentialStatus(
                        id = "https://example.com",
                        type = "statuslist2021",
                        statusPurpose = "suspension",
                        statusListIndex = "782",
                        statusListCredential = "https://example.com"
                    ),
                    CredentialBodyValues.Vc.CredentialStatus(
                        id = "https://example.com",
                        type = "statuslist2021",
                        statusPurpose = "revocation",
                        statusListIndex = "782",
                        statusListCredential = "https://example.com"
                    )
                )
            ),
        )
}
