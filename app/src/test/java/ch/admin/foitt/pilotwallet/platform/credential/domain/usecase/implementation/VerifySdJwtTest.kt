package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.ParseRawSdJwt
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.ParseSdJwt
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.implementation.ParseRawSdJwtImpl
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.implementation.ParseSdJwtImpl
import ch.admin.foitt.pilotwallet.platform.utils.base64StringToByteArray
import ch.admin.foitt.pilotwallet.util.assertOk
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// Documentation
// https://www.ietf.org/archive/id/draft-ietf-oauth-selective-disclosure-jwt-05.html#name-verification-of-the-sd-jwt
class VerifySdJwtTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var parseRawSdJwt: ParseRawSdJwt
    private lateinit var parseSdJwt: ParseSdJwt

    @BeforeEach
    fun setUp() {
        parseRawSdJwt = ParseRawSdJwtImpl(testDispatcher)
        parseSdJwt = ParseSdJwtImpl(
            testDispatcher,
            parseRawSdJwt
        )
    }

    @Test
    fun `JWT with two digests and two disclosures`() = runTest(testDispatcher) {
        val sdJwt = "$JWT_WITH_TWO_DIGESTS~$FIRST_DISCLOSURE~$SECOND_DISCLOSURE~"

        val result = parseSdJwt(sdJwt).assertOk()
        val claims = getClaims(result.jsonWithActualValues)

        assertEquals(2, claims.size)
    }

    // checks 6.1.3.3.1
    @Test
    fun `JWT with two digests, but one disclosure returns only one claim, undisclosed digest is ignored`() = runTest(testDispatcher) {
        val sdJwt = "$JWT_WITH_TWO_DIGESTS~$FIRST_DISCLOSURE~"

        val result = parseSdJwt(sdJwt).assertOk()
        val claims = getClaims(result.jsonWithActualValues)
        assertEquals(1, claims.size)

        val decodedDisclosure = getDecodedDisclosure(FIRST_DISCLOSURE)
        assertEquals(claims.keys.first(), decodedDisclosure.first)
        assertEquals(claims.values.first(), decodedDisclosure.second.jsonPrimitive.content)
    }

    private fun getClaims(json: JsonElement): Map<String, String> {
        val conf: Configuration = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS, Option.ALWAYS_RETURN_LIST).build()
        return JsonPath.using(conf)
            .parse(json)
            .read<List<Map<String, JsonElement>>>("$.vc.credentialSubject")
            .firstOrNull()
            ?.mapValues { it.value.jsonPrimitive.content } ?: emptyMap()
    }

    private fun getDecodedDisclosure(disclosure: String): Pair<String, JsonElement> {
        val decoded = disclosure.base64StringToByteArray()
        val jsonString = String(decoded)
        val array = Json.parseToJsonElement(jsonString).jsonArray
        return array[1].jsonPrimitive.content to array[2]
    }

    companion object {
        /*
        {
          "vc": {
            "credentialSubject": {
              "_sd": [
                "xgTkiyJdFsrXWU_4vTmSY5mF53dJCc86WMHxK4SNAwI",
                "_vTMbIzmHUGcfs8ciMKOGaOsqebkBNEci4NanpvBTjQ",
              ]
            }
          }
        }
         */
        private const val JWT_WITH_TWO_DIGESTS =
            "eyJraWQiOiJzdXBwb3J0ZWRDcnlwdG9ncmFwaGljQmluZGluZ01ldGhvZDpleUpyZEhraU9pSkZReUlzSW1OeWRpSTZJbEF0TlRJeElpd2llQ0k2SWtGSk5rSnVWbmszUXpaNlZVUXdiMGxJWlVkU1puTlJiM1V4Y1c1VWNqRmxjbU5hTjNwSk1HVTNWRWhpVEhSalQyNWpUV1JOV2tJM2J6TjJTblZpVjNSSmNFbHhZV3hmZDJ0NFdtbDVSbGxtTVhkNGJGQkNhbG9pTENKNUlqb2lRVXRmWTNwc2FXMU1TbWc1YW1wQ1VHa3lVbFV5TjFoalNVSmhPRkZMWmsweVRVdzVSbmM0WVRKdFlqa3lPWGROYkZsallscDFTVTl6UTFSdE1VZEVWVkZRV2xsTlRWaEtSWFZzU2tjemFuVlpha0Y1V0hsVVFTSjkiLCJ0eXAiOiJKV1QiLCJhbGciOiJFUzUxMiJ9.eyJ2YyI6eyJjcmVkZW50aWFsU3ViamVjdCI6eyJfc2QiOlsieGdUa2l5SmRGc3JYV1VfNHZUbVNZNW1GNTNkSkNjODZXTUh4SzRTTkF3SSIsIl92VE1iSXptSFVHY2ZzOGNpTUtPR2FPc3FlYmtCTkVjaTROYW5wdkJUalEiXX19LCJpYXQiOjE3MDU1ODA1OTh9.AAbfTl_hdkeHYoa7-tgJo488aQXVZcYRf9FcFSuL3IuhX-1PNcdahNNEBEuZcS-tekt74lwm5E4DRACfJSbd16YWAAhL9PBWHVStAVHlP3baiDYrLjjq6dOqdq1mnhBFNXhNXMblbOCIVDTWChb6LKe7Ztso7O493SnOaftpApqw8dZ9"

        private const val FIRST_DISCLOSURE = "WyJzYWx0IiwgImtleSIsICJ2YWx1ZSJd"
        private const val SECOND_DISCLOSURE = "WyJzYWx0MiIsICJrZXkyIiwgInZhbHVlMiJd"
    }
}
