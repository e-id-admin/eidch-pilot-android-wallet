package ch.admin.foitt.pilotwallet.platform.updateVCs

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetAllCredentials
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.GetAndRefreshCredentialValidity
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.UpdateAllCredentialStatuses
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.implementation.UpdateAllCredentialStatusesImpl
import ch.admin.foitt.pilotwallet.platform.updateVCs.mock.MockCredentials.credentials
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class UpdateAllCredentialStatusesImplTest {
    @MockK
    private lateinit var mockGetAllCredentials: GetAllCredentials

    @MockK
    private lateinit var mockGetAndRefreshCredentialValidity: GetAndRefreshCredentialValidity

    private lateinit var updateAllCredentialStatuses: UpdateAllCredentialStatuses

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        updateAllCredentialStatuses = UpdateAllCredentialStatusesImpl(
            mockGetAllCredentials,
            mockGetAndRefreshCredentialValidity
        )

        coEvery { mockGetAllCredentials() } returns Ok(credentials)
        coEvery { mockGetAndRefreshCredentialValidity(any()) } returns Ok(CredentialStatus.VALID)
    }

    @Test
    fun `status is checked for all credentials`() = runTest {
        updateAllCredentialStatuses.invoke()

        coVerify(exactly = 3) {
            mockGetAndRefreshCredentialValidity(any())
        }
    }

    @Test
    fun `silently fail when getting credentials fails`() = runTest {
        coEvery {
            mockGetAllCredentials()
        } returns Err(SsiError.Unexpected(Exception("get credentials failed")))

        updateAllCredentialStatuses.invoke()

        coVerify(exactly = 0) {
            mockGetAndRefreshCredentialValidity(any())
        }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}
