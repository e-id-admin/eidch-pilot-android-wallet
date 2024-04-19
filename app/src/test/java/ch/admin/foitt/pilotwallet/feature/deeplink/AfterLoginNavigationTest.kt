package ch.admin.foitt.pilotwallet.feature.deeplink

import ch.admin.foitt.pilotwallet.platform.deeplink.domain.repository.DeepLinkIntentRepository
import ch.admin.foitt.pilotwallet.platform.deeplink.domain.usecase.AfterLoginNavigation
import ch.admin.foitt.pilotwallet.platform.deeplink.domain.usecase.implementation.AfterLoginNavigationImpl
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.ProcessInvitation
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.navigation.domain.model.NavigationAction
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.HomeScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.ImpressumScreenDestination
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class AfterLoginNavigationTest {
    @MockK
    private lateinit var mockNavigationManager: NavigationManager

    @MockK
    private lateinit var mockDeepLinkIntentRepository: DeepLinkIntentRepository

    @MockK
    private lateinit var mockProcessInvitationUseCase: ProcessInvitation

    private lateinit var afterLoginNavigationUseCase: AfterLoginNavigation

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        every { mockNavigationManager.navigateToAndClearCurrent(any()) } returns Unit
        every { mockNavigationManager.navigateUp() } returns Unit
        coEvery { mockProcessInvitationUseCase(any()) } returns Ok(NavigationAction { })

        afterLoginNavigationUseCase = AfterLoginNavigationImpl(
            mockNavigationManager,
            mockDeepLinkIntentRepository,
            mockProcessInvitationUseCase
        )
    }

    @Test
    fun `AfterLoginNavigation navigates to Overview on no intent if previous destination is null`() = runTest {
        every { mockNavigationManager.previousDestination } returns null
        every { mockDeepLinkIntentRepository.deepLink } returns null

        afterLoginNavigationUseCase().navigate()

        coVerify(exactly = 1) {
            mockNavigationManager.navigateToAndClearCurrent(HomeScreenDestination)
        }
    }

    @Test
    fun `AfterLoginNavigation navigates up on no intent if previous destination is available`() = runTest {
        every { mockNavigationManager.previousDestination } returns ImpressumScreenDestination
        every { mockDeepLinkIntentRepository.deepLink } returns null

        afterLoginNavigationUseCase().navigate()

        coVerify(exactly = 1) {
            mockNavigationManager.navigateUp()
        }
    }

    @Test
    fun `AfterLoginNavigation calls ProcessInvitation on valid credential offer deeplink`() = runTest {
        every { mockDeepLinkIntentRepository.deepLink } returns validDeepLink
        every { mockDeepLinkIntentRepository::deepLink.set(null) } returns Unit

        afterLoginNavigationUseCase().navigate()

        coVerify(exactly = 1) {
            mockProcessInvitationUseCase(validDeepLink)
        }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    companion object {
        private const val validDeepLink = "openid-credential-offer://credential_offer=..."
    }
}
