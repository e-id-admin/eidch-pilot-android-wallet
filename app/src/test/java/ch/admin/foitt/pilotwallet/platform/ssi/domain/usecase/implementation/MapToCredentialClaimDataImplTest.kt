package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaimDisplay
import ch.admin.foitt.pilotwallet.platform.locale.domain.usecase.GetLocalizedDisplay
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimImage
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimText
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.MapToCredentialClaimDataError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.MapToCredentialClaimData
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockCredentialClaim.buildCredentialClaim
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockCredentialClaim.credentialClaimDisplay
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockCredentialClaim.credentialClaimDisplays
import ch.admin.foitt.pilotwallet.platform.utils.base64NonUrlStringToByteArray
import ch.admin.foitt.pilotwallet.util.assertErrorType
import ch.admin.foitt.pilotwallet.util.assertOk
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MapToCredentialClaimDataImplTest {

    private lateinit var mapToCredentialClaimData: MapToCredentialClaimData

    @MockK
    private lateinit var mockGetLocalizedDisplay: GetLocalizedDisplay

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic("ch.admin.foitt.pilotwallet.platform.utils.ByteArrayExtKt")
        coEvery { any<String>().base64NonUrlStringToByteArray() } returns byteArrayOf()

        mapToCredentialClaimData = MapToCredentialClaimDataImpl(
            getLocalizedDisplay = mockGetLocalizedDisplay
        )

        coEvery { mockGetLocalizedDisplay(displays = credentialClaimDisplays) } returns credentialClaimDisplay
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Claim with string valueType should return correct data`() = runTest {
        val claim = buildCredentialClaim("string")

        val data = mapToCredentialClaimData(claim = claim, displays = credentialClaimDisplays).assertOk()
        assertTrue("string valueType should return ${CredentialClaimText::class.simpleName}", data is CredentialClaimText)
        assertEquals(credentialClaimDisplay.name, data.localizedKey)
        assertEquals(claim.value, (data as CredentialClaimText).value)
    }

    @Test
    fun `Claim with bool valueType should return correct data`() = runTest {
        val claim = buildCredentialClaim("bool")

        val data = mapToCredentialClaimData(claim = claim, displays = credentialClaimDisplays).assertOk()
        assertTrue("bool valueType should return ${CredentialClaimText::class.simpleName}", data is CredentialClaimText)
        assertEquals(credentialClaimDisplay.name, data.localizedKey)
        assertEquals(claim.value, (data as CredentialClaimText).value)
    }

    @Test
    fun `Claim with png valueType should return correct data`() = runTest {
        val claim = buildCredentialClaim("image/png")

        val data = mapToCredentialClaimData(claim = claim, displays = credentialClaimDisplays).assertOk()
        assertTrue("image/png valueType should return ${CredentialClaimImage::class.simpleName}", data is CredentialClaimImage)
        assertEquals(credentialClaimDisplay.name, data.localizedKey)
        assertEquals(claim.value.base64NonUrlStringToByteArray(), (data as CredentialClaimImage).imageData)
    }

    @Test
    fun `Claim with jpeg valueType should return correct data`() = runTest {
        val claim = buildCredentialClaim("image/jpeg")

        val data = mapToCredentialClaimData(claim = claim, displays = credentialClaimDisplays).assertOk()
        assertTrue("image/jpeg valueType should return ${CredentialClaimImage::class.simpleName}", data is CredentialClaimImage)
        assertEquals(credentialClaimDisplay.name, data.localizedKey)
        assertEquals(claim.value.base64NonUrlStringToByteArray(), (data as CredentialClaimImage).imageData)
    }

    @Test
    fun `Claim with jpg valueType should return an error`() = runTest {
        mapToCredentialClaimData(claim = buildCredentialClaim("image/jpg"), displays = credentialClaimDisplays)
            .assertErrorType(MapToCredentialClaimDataError::class)
    }

    @Test
    fun `Claim with null valueType should return an error`() = runTest {
        mapToCredentialClaimData(claim = buildCredentialClaim("null"), displays = credentialClaimDisplays)
            .assertErrorType(MapToCredentialClaimDataError::class)
    }

    @Test
    fun `Claim with empty valueType should return an error`() = runTest {
        mapToCredentialClaimData(claim = buildCredentialClaim(""), displays = credentialClaimDisplays)
            .assertErrorType(MapToCredentialClaimDataError::class)
    }

    @Test
    fun `Claim with string valueType but no displays should return an error`() = runTest {
        coEvery { mockGetLocalizedDisplay(displays = any<List<CredentialClaimDisplay>>()) } returns null
        val claim = buildCredentialClaim("string")

        mapToCredentialClaimData(claim = claim, displays = emptyList())
            .assertErrorType(MapToCredentialClaimDataError::class)
    }
}
