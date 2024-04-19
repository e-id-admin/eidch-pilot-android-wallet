package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaimDisplay
import ch.admin.foitt.pilotwallet.platform.locale.domain.usecase.GetLocalizedDisplay
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimImage
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimText
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCredentialClaimsError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialClaimData
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialClaimDisplays
import ch.admin.foitt.pilotwallet.platform.utils.base64NonUrlStringToByteArray
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GetCredentialClaimDataImplTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getCredentialClaimData: GetCredentialClaimData

    @MockK
    private lateinit var mockGetCredentialClaimDisplays: GetCredentialClaimDisplays

    @MockK
    private lateinit var mockGetLocalizedDisplay: GetLocalizedDisplay

    private val credentialClaimDisplay = CredentialClaimDisplay(
        claimId = 0,
        name = "name",
        locale = "xxx"
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic("ch.admin.foitt.pilotwallet.platform.utils.ByteArrayExtKt")
        coEvery { any<String>().base64NonUrlStringToByteArray() } returns byteArrayOf()

        getCredentialClaimData = GetCredentialClaimDataImpl(
            getCredentialClaimDisplays = mockGetCredentialClaimDisplays,
            getLocalizedDisplay = mockGetLocalizedDisplay
        )

        coEvery { mockGetCredentialClaimDisplays(any()) } returns Ok(emptyList())
        coEvery { mockGetLocalizedDisplay<CredentialClaimDisplay>(displays = any()) } returns credentialClaimDisplay
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `CredentialClaim with supported valueType should return correct CredentialClaimData`() = runTest(testDispatcher) {
        val stringCredentialClaim = buildCredentialClaim("string")
        val stringClaim = getCredentialClaimData(credentialClaim = stringCredentialClaim).get()
        Assert.assertTrue("string valueType should return CredentialClaimText", stringClaim is CredentialClaimText)
        Assert.assertEquals(credentialClaimDisplay.name, (stringClaim as CredentialClaimText).localizedKey)
        Assert.assertEquals(stringCredentialClaim.value, stringClaim.value)

        val boolCredentialClaim = buildCredentialClaim("bool")
        val boolClaim = getCredentialClaimData(credentialClaim = boolCredentialClaim).get()
        Assert.assertTrue("bool valueType should return CredentialClaimText", boolClaim is CredentialClaimText)
        Assert.assertEquals(credentialClaimDisplay.name, (boolClaim as CredentialClaimText).localizedKey)
        Assert.assertEquals(stringCredentialClaim.value, boolClaim.value)

        val pngCredentialClaim = buildCredentialClaim("image/png")
        val pngClaim = getCredentialClaimData(credentialClaim = pngCredentialClaim).get()
        Assert.assertTrue("image/png valueType should return CredentialClaimImage", pngClaim is CredentialClaimImage)
        Assert.assertEquals(credentialClaimDisplay.name, (pngClaim as CredentialClaimImage).localizedKey)
        Assert.assertEquals(pngCredentialClaim.value.base64NonUrlStringToByteArray(), pngClaim.imageData)

        val jpegCredentialClaim = buildCredentialClaim("image/jpeg")
        val jpegClaim = getCredentialClaimData(credentialClaim = buildCredentialClaim("image/jpeg")).get()
        Assert.assertTrue("image/jpeg valueType should return CredentialClaimImage", jpegClaim is CredentialClaimImage)
        Assert.assertEquals(credentialClaimDisplay.name, (jpegClaim as CredentialClaimImage).localizedKey)
        Assert.assertEquals(jpegCredentialClaim.value.base64NonUrlStringToByteArray(), jpegClaim.imageData)
    }

    @Test
    fun `CredentialClaim with unsupported valueType should return an GetCredentialClaimsError`() = runTest(testDispatcher) {
        val brokenImageClaim = getCredentialClaimData(credentialClaim = buildCredentialClaim("image")).getError()
        Assert.assertTrue("broken image/jpg valueType should return Err", brokenImageClaim is GetCredentialClaimsError)

        val brokenJpgClaim = getCredentialClaimData(credentialClaim = buildCredentialClaim("image/jpg")).getError()
        Assert.assertTrue("broken image/jpg valueType should return Err", brokenJpgClaim is GetCredentialClaimsError)

        val nullClaim = getCredentialClaimData(credentialClaim = buildCredentialClaim("null")).getError()
        Assert.assertTrue("null valueType should return Err", nullClaim is GetCredentialClaimsError)

        val emptyStringClaim = getCredentialClaimData(credentialClaim = buildCredentialClaim("")).getError()
        Assert.assertTrue("null valueType should return Err", emptyStringClaim is GetCredentialClaimsError)

        val brokenBoolClaim = getCredentialClaimData(credentialClaim = buildCredentialClaim(" bool ")).getError()
        Assert.assertTrue("brokenBoolClaim valueType should return Err", brokenBoolClaim is GetCredentialClaimsError)
    }

    private fun buildCredentialClaim(valueType: String) = CredentialClaim(
        credentialId = 1,
        key = "key",
        value = "value",
        valueType = valueType
    )
}
