package ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.ParseSdJwtError
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.implementation.mock.ComplexSdJwt
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.implementation.mock.FlatDisclosures
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.implementation.mock.FlatSdJwt
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.implementation.mock.KeyBindingJwt
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.implementation.mock.RecursiveSdJwt
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.implementation.mock.StructuredSdJwt
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.implementation.mock.UndisclosedJwt
import ch.admin.foitt.pilotwallet.util.assertOk
import com.github.michaelbull.result.Err
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ParseSdJwtImplTest {

    private val testDispatcher = StandardTestDispatcher()

    private val parseRawSdJwt = ParseRawSdJwtImpl(testDispatcher)

    private lateinit var parseSdJwtUseCase: ParseSdJwtImpl

    @Before
    fun setUp() {
        parseSdJwtUseCase = ParseSdJwtImpl(testDispatcher, parseRawSdJwt)
    }

    @Test
    fun `parsing a valid undisclosed JWT should return the JWT and the JSON with actual values`() = runTest(testDispatcher) {
        val sdJwt = parseSdJwtUseCase(UndisclosedJwt.JWT).assertOk()

        assertEquals(UndisclosedJwt.JWT, sdJwt.signedJwt)
        assertJsonEquals(UndisclosedJwt.JSON, sdJwt.jsonWithActualValues)
    }

    @Test
    fun `parsing a valid flat SD-JWT should return the JWT and the JSON with actual values`() = runTest(testDispatcher) {
        val sdJwt = parseSdJwtUseCase(FlatSdJwt.JWT + FlatDisclosures).assertOk()

        assertEquals(FlatSdJwt.JWT, sdJwt.signedJwt)
        assertJsonEquals(FlatSdJwt.JSON, sdJwt.jsonWithActualValues)
    }

    @Test
    fun `parsing a valid structured SD-JWT should return the JWT and JSON with actual values`() = runTest(testDispatcher) {
        val sdJwt = parseSdJwtUseCase(StructuredSdJwt.JWT + FlatDisclosures).assertOk()

        assertEquals(StructuredSdJwt.JWT, sdJwt.signedJwt)
        assertJsonEquals(StructuredSdJwt.JSON, sdJwt.jsonWithActualValues)
    }

    @Test
    fun `parsing a valid structured SD-JWT with KeyBindingJWT should return the JWT and the JSON with actual values`() =
        runTest(testDispatcher) {
            val sdJwt = parseSdJwtUseCase(StructuredSdJwt.JWT + FlatDisclosures + KeyBindingJwt).assertOk()

            assertEquals(StructuredSdJwt.JWT, sdJwt.signedJwt)
            assertJsonEquals(StructuredSdJwt.JSON, sdJwt.jsonWithActualValues)
        }

    @Test
    fun `parsing a valid recursive SD-JWT should return the JWT and the JSON with actual values`() = runTest(testDispatcher) {
        val sdJwt = parseSdJwtUseCase(RecursiveSdJwt.SD_JWT).assertOk()

        assertEquals(RecursiveSdJwt.JWT, sdJwt.signedJwt)
        assertJsonEquals(RecursiveSdJwt.JSON, sdJwt.jsonWithActualValues)
    }

    @Test
    fun `parsing a valid complex SD-JWT should return the JWT and the JSON with actual values`() = runTest(testDispatcher) {
        val sdJwt = parseSdJwtUseCase(ComplexSdJwt.SD_JWT).assertOk()

        assertEquals(ComplexSdJwt.JWT, sdJwt.signedJwt)
        assertJsonEquals(ComplexSdJwt.JSON, sdJwt.jsonWithActualValues)
    }

    @Test
    fun `parsing an SD-JWT with only two JWT parts should return an invalid format error`() = runTest(testDispatcher) {
        val sdJwt = "test.test~disclosures~"
        val result = parseSdJwtUseCase(sdJwt)

        assertEquals(Err(ParseSdJwtError.InvalidJwt), result)
    }

    @Test
    fun `parsing an SD-JWT with only one JWT part should return an invalid format error`() = runTest(testDispatcher) {
        val sdJwt = "test~disclosures~"
        val result = parseSdJwtUseCase(sdJwt)

        assertEquals(Err(ParseSdJwtError.InvalidJwt), result)
    }

    @Test
    fun `parsing an SD-JWT with a disclosure with two elements should return an invalid disclosure error`() = runTest(testDispatcher) {
        // ["test_salt_1", "test_key_1"]
        val sdJwt = FlatSdJwt.JWT + "~WyJ0ZXN0X3NhbHRfMSIsICJ0ZXN0X2tleV8xIl0~"
        val result = parseSdJwtUseCase(sdJwt)

        assertEquals(Err(ParseSdJwtError.InvalidDisclosure), result)
    }

    @Test
    fun `parsing an SD-JWT with a disclosure with one element should return an invalid disclosure error`() = runTest(testDispatcher) {
        // ["test_salt_1"]
        val sdJwt = FlatSdJwt.JWT + "~WyJ0ZXN0X3NhbHRfMSJd~"
        val result = parseSdJwtUseCase(sdJwt)

        assertEquals(Err(ParseSdJwtError.InvalidDisclosure), result)
    }

    @Test
    fun `parsing an SD-JWT with a disclosure with too many elements should return an invalid disclosure error`() = runTest(testDispatcher) {
        // ["test_salt_1", "test_key_1", "test_value_1", "test"]
        val sdJwt = FlatSdJwt.JWT + "~WyJ0ZXN0X3NhbHRfMSIsICJ0ZXN0X2tleV8xIiwgInRlc3RfdmFsdWVfMSIsICJ0ZXN0Il0~"
        val result = parseSdJwtUseCase(sdJwt)

        assertEquals(Err(ParseSdJwtError.InvalidDisclosure), result)
    }

    @Test
    fun `parsing a random string should return an invalid format error`() = runTest(testDispatcher) {
        val sdJwt = "foobar"
        val result = parseSdJwtUseCase(sdJwt)

        assertEquals(Err(ParseSdJwtError.InvalidJwt), result)
    }

    @Test
    fun `parsing an empty string should return an invalid format error`() = runTest(testDispatcher) {
        val sdJwt = ""
        val result = parseSdJwtUseCase(sdJwt)

        assertEquals(Err(ParseSdJwtError.InvalidJwt), result)
    }

    @Test
    fun `parsing an SD-JWT where an _sd key references a json object should return an invalid digest array error`() =
        runTest(testDispatcher) {
            /*
            {
               "test":{
                  "_sd":{
                     "not_good":"true"
                  }
               },
               "_sd_alg":"sha-256"
            }
             */
            val jwt =
                "eyJhbGciOiJFUzUxMiIsInR5cCI6IkpXVCJ9.eyJ0ZXN0Ijp7Il9zZCI6eyJub3RfZ29vZCI6InRydWUifX0sIl9zZF9hbGciOiJzaGEtMjU2IiwiaWF0IjoxNjk3ODA5NzMxfQ.AaQdx2Pwj0jPE2Z8dCa9Jiam8tyzkOJb_5HCEZumuLRlh3nFtvmAxLGWBqYO54zotDOgGMH5WBdPuad5sJzdbWfHAbpFN6APM9FSNk3uk4C2qvb1osGeehE2REtJ1EjPOqFldgO36zqmMG8jSHm5YH9p1Xw4oYkeehXJpLL2qRsZPdZU"
            val result = parseSdJwtUseCase(jwt + FlatDisclosures)

            assertEquals(Err(ParseSdJwtError.InvalidDigestArray), result)
        }

    @Test
    fun `parsing an SD-JWT where an _sd key references a json primitive should return an invalid digest array error`() = runTest(
        testDispatcher
    ) {
        /*
        {
           "test":{
              "_sd":"not good"
           },
           "_sd_alg":"sha-256"
        }
         */
        val jwt =
            "eyJhbGciOiJFUzUxMiIsInR5cCI6IkpXVCJ9.eyJ0ZXN0Ijp7Il9zZCI6Im5vdCBnb29kIn0sIl9zZF9hbGciOiJzaGEtMjU2IiwiaWF0IjoxNjk3ODEwODkzfQ.AZmwJlPifMvTWxUJfTrbnq4-lqzKPsrqi2CDjuIaDwSIeyouTcCEO5SNHfYQwlFEiMq5qpM5qUo6opVGbTOsge2HAH81GBymZR4n5cKPvMmIVe6rQ-fcdV-rfbV4RfEuXSla_qZGl6NR8CX9slVc3YRBr_UK7rgl_bGh_EH2sJAP19-N"
        val result = parseSdJwtUseCase(jwt + FlatDisclosures)

        assertEquals(Err(ParseSdJwtError.InvalidDigestArray), result)
    }

    private fun assertJsonEquals(expected: String, actualJson: JsonElement) {
        val expectedJson = Json.parseToJsonElement(expected)
        val filteredJson = actualJson.jsonObject.filterKeys { key ->
            key != ISSUED_AT_KEY && key != SD_ALGORITHM_KEY
        }
        assertEquals(expectedJson, filteredJson)
    }

    companion object {
        private const val ISSUED_AT_KEY = "iat"
        private const val SD_ALGORITHM_KEY = "_sd_alg"
    }
}
