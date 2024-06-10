package ch.admin.foitt.pilotwallet.platform.updateVCs

import android.annotation.SuppressLint
import ch.admin.foitt.openid4vc.domain.model.vcStatus.FetchVCStatusError
import ch.admin.foitt.openid4vc.domain.usecase.FetchVCStatus
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialBodiesByCredentialId
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.model.CredentialBodyValues
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.GetAndRefreshCredentialValidity
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.implementation.GetAndRefreshCredentialValidityImpl
import ch.admin.foitt.pilotwallet.platform.updateVCs.mock.MockCredentials.credentialBody
import ch.admin.foitt.pilotwallet.platform.updateVCs.mock.MockCredentials.credentials
import ch.admin.foitt.pilotwallet.platform.updateVCs.mock.MockCredentials.expiredCredentialBodyValues
import ch.admin.foitt.pilotwallet.platform.updateVCs.mock.MockCredentials.notYetValidCredentialBodyValues
import ch.admin.foitt.pilotwallet.platform.updateVCs.mock.MockCredentials.validCredentialBodyValues
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetAndRefreshCredentialValidityTest {
    @MockK
    private lateinit var mockJson: Json

    @MockK
    private lateinit var mockGetCredentialBodiesByCredentialId: GetCredentialBodiesByCredentialId

    @MockK
    private lateinit var mockFetchVCStatus: FetchVCStatus

    @MockK
    private lateinit var mockCredentialRepo: CredentialRepo

    private lateinit var getAndRefreshCredentialValidity: GetAndRefreshCredentialValidity

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        getAndRefreshCredentialValidity = GetAndRefreshCredentialValidityImpl(
            json = mockJson,
            getCredentialBodiesByCredentialId = mockGetCredentialBodiesByCredentialId,
            fetchVCStatus = mockFetchVCStatus,
            credentialRepo = mockCredentialRepo,
        )

        coEvery { mockGetCredentialBodiesByCredentialId.invoke(any()) } returns Ok(credentialBody)
        coEvery {
            mockJson.decodeFromString<CredentialBodyValues>(credentialBody.first().body)
        } returns validCredentialBodyValues
        coEvery { mockFetchVCStatus.invoke(any(), any(), any()) } returns Ok(false)
        coEvery { mockCredentialRepo.update(any()) } returns Ok(0)
    }

    @SuppressLint("CheckResult")
    @Test
    fun `updating the status successfully runs specific things`() = runTest {
        getAndRefreshCredentialValidity.invoke(credentials.first())

        coVerifyOrder {
            mockGetCredentialBodiesByCredentialId.invoke(0)
            mockFetchVCStatus.invoke(any(), any(), any())
            mockFetchVCStatus.invoke(any(), any(), any())
            mockCredentialRepo.update(any())
        }
    }

    @Test
    fun `a not yet valid raw credential updates the status to invalid`() = runTest {
        coEvery {
            mockJson.decodeFromString<CredentialBodyValues>(credentialBody.first().body)
        } returns notYetValidCredentialBodyValues

        getAndRefreshCredentialValidity.invoke(credentials.first())

        coVerify {
            mockCredentialRepo.update(credentials.first().copy(status = CredentialStatus.INVALID))
        }
    }

    @Test
    fun `an expired raw credential updates the status to invalid`() = runTest {
        coEvery {
            mockJson.decodeFromString<CredentialBodyValues>(credentialBody.first().body)
        } returns expiredCredentialBodyValues

        getAndRefreshCredentialValidity.invoke(credentials.first())

        coVerify {
            mockCredentialRepo.update(credentials.first().copy(status = CredentialStatus.INVALID))
        }
    }

    @Test
    fun `revoked and suspended updates the status to invalid`() = runTest {
        coEvery {
            mockFetchVCStatus.invoke(any(), any(), any())
        } returns Ok(true)

        getAndRefreshCredentialValidity.invoke(credentials.first())

        coVerify {
            mockCredentialRepo.update(credentials.first().copy(status = CredentialStatus.INVALID))
        }
    }

    @Test
    fun `revoked and non-suspended updates the status to invalid`() = runTest {
        coEvery {
            mockFetchVCStatus.invoke(any(), any(), any())
        } returns Ok(true) andThen Ok(false)

        getAndRefreshCredentialValidity.invoke(credentials.first())

        coVerify {
            mockCredentialRepo.update(credentials.first().copy(status = CredentialStatus.INVALID))
        }
    }

    @Test
    fun `revoked and error during suspended check updates the status to invalid`() = runTest {
        coEvery {
            mockFetchVCStatus.invoke(any(), any(), any())
        } returns Ok(true) andThen Err(FetchVCStatusError.NetworkError)

        getAndRefreshCredentialValidity.invoke(credentials.first())

        coVerify {
            mockCredentialRepo.update(credentials.first().copy(status = CredentialStatus.INVALID))
        }
    }

    @Test
    fun `non-revoked and suspended updates the status to invalid`() = runTest {
        coEvery {
            mockFetchVCStatus.invoke(any(), any(), any())
        } returns Ok(false) andThen Ok(true)

        getAndRefreshCredentialValidity.invoke(credentials.first())

        coVerify {
            mockCredentialRepo.update(credentials.first().copy(status = CredentialStatus.INVALID))
        }
    }

    @Test
    fun `non-revoked and non-suspended updates the status to valid`() = runTest {
        coEvery {
            mockFetchVCStatus.invoke(any(), any(), any())
        } returns Ok(false) andThen Ok(false)

        getAndRefreshCredentialValidity.invoke(credentials.first())

        coVerify {
            mockCredentialRepo.update(credentials.first().copy(status = CredentialStatus.VALID))
        }
    }

    @Test
    fun `non-revoked and error during suspended check uses the previous state`() = runTest {
        coEvery {
            mockFetchVCStatus.invoke(any(), any(), any())
        } returnsMany listOf(
            Ok(false),
            Err(FetchVCStatusError.NetworkError),
            Ok(false),
            Err(FetchVCStatusError.NetworkError),
            Ok(false),
            Err(FetchVCStatusError.NetworkError),
        )

        val expectValid = CredentialStatus.VALID
        val resultValid = getAndRefreshCredentialValidity.invoke(credentials[0])

        assertEquals(expectValid, resultValid.get())

        val expectInvalid = CredentialStatus.INVALID
        val resultInvalid = getAndRefreshCredentialValidity.invoke(credentials[1])

        assertEquals(expectInvalid, resultInvalid.get())

        val expectUnknown = CredentialStatus.UNKNOWN
        val resultUnknown = getAndRefreshCredentialValidity.invoke(credentials[2])

        assertEquals(expectUnknown, resultUnknown.get())
    }

    @Test
    fun `error during revoked check and suspended updates the status to invalid`() = runTest {
        coEvery {
            mockFetchVCStatus.invoke(any(), any(), any())
        } returns Err(FetchVCStatusError.NetworkError) andThen Ok(true)

        getAndRefreshCredentialValidity.invoke(credentials.first())

        coVerify {
            mockCredentialRepo.update(credentials.first().copy(status = CredentialStatus.INVALID))
        }
    }

    @Test
    fun `error during revoked and non-suspended uses the previous state`() = runTest {
        coEvery {
            mockFetchVCStatus.invoke(any(), any(), any())
        } returnsMany listOf(
            Err(FetchVCStatusError.NetworkError),
            Ok(false),
            Err(FetchVCStatusError.NetworkError),
            Ok(false),
            Err(FetchVCStatusError.NetworkError),
            Ok(false),
        )

        val expectValid = CredentialStatus.VALID
        val resultValid = getAndRefreshCredentialValidity.invoke(credentials[0])

        assertEquals(expectValid, resultValid.get())

        val expectInvalid = CredentialStatus.INVALID
        val resultInvalid = getAndRefreshCredentialValidity.invoke(credentials[1])

        assertEquals(expectInvalid, resultInvalid.get())

        val expectUnknown = CredentialStatus.UNKNOWN
        val resultUnknown = getAndRefreshCredentialValidity.invoke(credentials[2])

        assertEquals(expectUnknown, resultUnknown.get())
    }

    @Test
    fun `error during revoked check and error during suspended check uses the previous state`() = runTest {
        coEvery {
            mockFetchVCStatus.invoke(any(), any(), any())
        } returns Err(FetchVCStatusError.NetworkError)

        val expectValid = CredentialStatus.VALID
        val resultValid = getAndRefreshCredentialValidity.invoke(credentials[0])

        assertEquals(expectValid, resultValid.get())

        val expectInvalid = CredentialStatus.INVALID
        val resultInvalid = getAndRefreshCredentialValidity.invoke(credentials[1])

        assertEquals(expectInvalid, resultInvalid.get())

        val expectUnknown = CredentialStatus.UNKNOWN
        val resultUnknown = getAndRefreshCredentialValidity.invoke(credentials[2])

        assertEquals(expectUnknown, resultUnknown.get())
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }
}
