package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOfferError
import ch.admin.foitt.openid4vc.domain.repository.CredentialOfferRepository
import ch.admin.foitt.openid4vc.domain.usecase.FetchIssuerConfiguration
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer
import ch.admin.foitt.openid4vc.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FetchIssuerConfigurationImplTest {

    @MockK
    private lateinit var mockCredentialOfferRepository: CredentialOfferRepository

    private lateinit var fetchIssuerConfigurationUseCase: FetchIssuerConfiguration

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        success()

        fetchIssuerConfigurationUseCase = FetchIssuerConfigurationImpl(
            mockCredentialOfferRepository,
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `successful fetch should return a success`() = runTest {
        val issuerConfig = fetchIssuerConfigurationUseCase("")

        issuerConfig.assertOk()
        assertEquals(MockCredentialOffer.validIssuerConfig, issuerConfig.get())
    }

    @Test
    fun `failed fetch of issuer configuration should return an error`() = runTest {
        val expected = Err(CredentialOfferError.NetworkInfoError)
        coEvery { mockCredentialOfferRepository.fetchIssuerConfiguration(any()) } returns expected

        val issuerConfig = fetchIssuerConfigurationUseCase("")
        assertEquals(
            expected,
            issuerConfig
        )
    }

    private fun success() {
        coEvery {
            mockCredentialOfferRepository.fetchIssuerConfiguration(any())
        } returns Ok(MockCredentialOffer.validIssuerConfig)
    }
}
