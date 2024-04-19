package ch.admin.foitt.pilotwallet.feature.deeplink

import ch.admin.foitt.pilotwallet.platform.deeplink.data.DeepLinkIntentRepositoryImpl
import ch.admin.foitt.pilotwallet.platform.deeplink.domain.repository.DeepLinkIntentRepository
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DeepLinkIntentRepositoryTest {

    private lateinit var testDeepLink: String

    private lateinit var intentRepository: DeepLinkIntentRepository

    @Before
    fun setUp() {
        testDeepLink = "openid-credential-offer://credential_offer=..."
        intentRepository = DeepLinkIntentRepositoryImpl()
    }

    @Test
    fun `Set, get and delete a deepLink`() = runTest {
        intentRepository.deepLink = testDeepLink

        val retrievedIntent = intentRepository.deepLink

        Assert.assertEquals(testDeepLink, retrievedIntent)

        intentRepository.deepLink = null

        val retrievedIntent2 = intentRepository.deepLink

        Assert.assertNull(retrievedIntent2)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}
