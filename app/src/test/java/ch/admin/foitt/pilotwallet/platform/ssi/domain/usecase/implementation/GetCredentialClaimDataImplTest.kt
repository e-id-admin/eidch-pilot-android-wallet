package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCredentialClaimDataError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialClaimData
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialClaimDisplays
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.MapToCredentialClaimData
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockCredentialClaim.CLAIM_ID
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockCredentialClaim.credentialClaim
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockCredentialClaim.credentialClaimDisplays
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockCredentialClaim.credentialClaimText1
import ch.admin.foitt.pilotwallet.util.assertErrorType
import ch.admin.foitt.pilotwallet.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetCredentialClaimDataImplTest {

    private lateinit var getCredentialClaimData: GetCredentialClaimData

    @MockK
    private lateinit var mockGetCredentialClaimDisplays: GetCredentialClaimDisplays

    @MockK
    private lateinit var mockMapToCredentialClaimData: MapToCredentialClaimData

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        getCredentialClaimData = GetCredentialClaimDataImpl(
            getCredentialClaimDisplays = mockGetCredentialClaimDisplays, mapToCredentialClaimData = mockMapToCredentialClaimData
        )

        coEvery { mockGetCredentialClaimDisplays(CLAIM_ID) } returns Ok(credentialClaimDisplays)
        coEvery {
            mockMapToCredentialClaimData(
                claim = credentialClaim, displays = credentialClaimDisplays
            )
        } returns Ok(credentialClaimText1)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `GetCredentialClaimData fetches the displays for the claim and maps them to the right data`() = runTest {
        val result = getCredentialClaimData(credentialClaim)

        val data = result.assertOk()
        assertEquals(credentialClaimText1, data)
    }

    @Test
    fun `Errors when getting the displays are correctly returned`() = runTest {
        val throwable = IllegalStateException()
        coEvery { mockGetCredentialClaimDisplays(CLAIM_ID) } returns Err(SsiError.Unexpected(throwable))

        val result = getCredentialClaimData(credentialClaim)

        result.assertErrorType(GetCredentialClaimDataError::class)
        val error = result.error as SsiError.Unexpected
        assertEquals(error.cause, throwable)
    }

    @Test
    fun `Errors when mapping to data are correctly returned`() = runTest {
        val throwable = IllegalStateException()
        coEvery {
            mockMapToCredentialClaimData(credentialClaim, credentialClaimDisplays)
        } returns Err(SsiError.Unexpected(throwable))

        val result = getCredentialClaimData(credentialClaim)

        result.assertErrorType(GetCredentialClaimDataError::class)
        val error = result.error as SsiError.Unexpected
        assertEquals(error.cause, throwable)
    }
}
