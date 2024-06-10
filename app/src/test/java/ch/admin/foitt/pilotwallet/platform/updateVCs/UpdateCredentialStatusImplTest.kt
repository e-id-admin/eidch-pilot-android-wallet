package ch.admin.foitt.pilotwallet.platform.updateVCs

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialById
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.model.UpdateCredentialStatusError
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.GetAndRefreshCredentialValidity
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.UpdateCredentialStatus
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.implementation.UpdateCredentialStatusImpl
import ch.admin.foitt.pilotwallet.platform.updateVCs.mock.MockCredentials.credentials
import ch.admin.foitt.pilotwallet.util.assertErrorType
import ch.admin.foitt.pilotwallet.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateCredentialStatusImplTest {

    private val testDispatcher = StandardTestDispatcher()

    @MockK
    private lateinit var mockGetCredentialById: GetCredentialById

    @MockK
    private lateinit var mockGetAndRefreshCredentialValidity: GetAndRefreshCredentialValidity

    private lateinit var updateCredentialStatus: UpdateCredentialStatus

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        updateCredentialStatus = UpdateCredentialStatusImpl(
            testDispatcher,
            mockGetCredentialById,
            mockGetAndRefreshCredentialValidity,
        )

        coEvery { mockGetCredentialById(any()) } returns Ok(credentials[0])
        coEvery { mockGetAndRefreshCredentialValidity(any()) } returns Ok(CredentialStatus.VALID)
    }

    @Test
    fun `successfully getting the credential updates the status`() = runTest(testDispatcher) {
        val result = updateCredentialStatus(0).assertOk()

        coVerify(exactly = 1) {
            mockGetAndRefreshCredentialValidity(any())
        }

        assertEquals(result, CredentialStatus.VALID)
    }

    @Test
    fun `error during getting credential does not update status`() = runTest(testDispatcher) {
        coEvery { mockGetCredentialById(any()) } returns Err(SsiError.Unexpected(Exception("get credential failed")))

        updateCredentialStatus(0)

        coVerify(exactly = 0) {
            mockGetAndRefreshCredentialValidity(any())
        }
    }

    @Test
    fun `error during status update returns error`() = runTest(testDispatcher) {
        coEvery {
            mockGetAndRefreshCredentialValidity(any())
        } returns Err(UpdateCredentialStatusError.Unexpected(Exception("get credential bodies failed")))

        updateCredentialStatus(0).assertErrorType(UpdateCredentialStatusError.Unexpected::class)

        coVerify {
            mockGetAndRefreshCredentialValidity(any())
        }
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }
}
